/*
 *   Copyright 2020-2026 PhotoZ
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package onlasdan.gallery.encryption.domain

import onlasdan.gallery.encryption.domain.crypto.IV_SIZE
import onlasdan.gallery.encryption.domain.crypto.KeyGen
import onlasdan.gallery.encryption.domain.crypto.SALT_SIZE
import onlasdan.gallery.encryption.domain.handlers.BiometricVaultProtectionHandler
import onlasdan.gallery.encryption.domain.handlers.PasswordVaultProtectionHandler
import onlasdan.gallery.encryption.domain.handlers.RecoveryPhraseVaultProtectionHandler
import onlasdan.gallery.encryption.domain.models.Algorithm
import onlasdan.gallery.encryption.domain.models.CreateRequest
import onlasdan.gallery.encryption.domain.models.Kdf
import onlasdan.gallery.encryption.domain.models.UnlockRequest
import onlasdan.gallery.encryption.domain.models.VaultProtection
import onlasdan.gallery.encryption.domain.models.VaultProtectionParams
import onlasdan.gallery.encryption.domain.models.VaultProtectionType
import onlasdan.gallery.encryption.domain.models.VaultSession
import onlasdan.gallery.security.BreakInDetector
import onlasdan.gallery.security.SecurityChecker
import onlasdan.gallery.settings.data.Config
import timber.log.Timber
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64

private const val KEK_SIZE = 256
private const val KEK_ITERATIONS = 100_000

/**
 * Service for vault management and unlocking.
 * Sits between the UI/ViewModels and the low-level ProtectionHandlers.
 *
 * @since 1.0.0
 * @author PhotoZ
 */
@Singleton
class VaultService
	@Inject
	constructor(
		private val passwordProtectionHandler: PasswordVaultProtectionHandler,
		private val biometricProtectionHandler: BiometricVaultProtectionHandler,
		private val recoveryPhraseProtectionHandler: RecoveryPhraseVaultProtectionHandler,
		private val vaultProtectionRepository: VaultProtectionRepository,
		private val keyGen: KeyGen,
		private val config: Config,
		private val breakInDetector: BreakInDetector,
		private val securityChecker: SecurityChecker,
		private val vaultIdBackfillHook: VaultIdBackfillHook?,
	) {
		suspend fun unlock(request: UnlockRequest): Result<VaultSession> {
			val result =
				runCatching {
					val vmk =
						when (request) {
							is UnlockRequest.Password -> {
								// Sprint 2 / M7: unlock path iterates all Password rows.
								val passwordProtections =
									vaultProtectionRepository.getAllProtections(VaultProtectionType.Password)
								require(passwordProtections.isNotEmpty()) { "No Password protections found" }
								unlockMultiVaultPassword(request, passwordProtections)
							}
							is UnlockRequest.Biometric -> {
								val protection = vaultProtectionRepository.getProtection(VaultProtectionType.Biometric)
								requireNotNull(protection)
								biometricProtectionHandler.unlock(request, protection)
							}
							is UnlockRequest.RecoveryPhrase -> {
								val protection =
									vaultProtectionRepository.getProtection(VaultProtectionType.RecoveryPhrase)
								requireNotNull(protection)
								recoveryPhraseProtectionHandler.unlock(request, protection)
							}
						}

					config.lastUsedUnlockMethod = request.protectionType

					val session = VaultSession(vmk = vmk)

					// ─── Sprint 2 / M7: vault_id backfill (one-time migration) ─────────
					try {
						vaultIdBackfillHook?.backfillVaultId(session.vaultId)
					} catch (e: Exception) {
						Timber.w(e, "vault_id backfill failed (non-fatal) — will retry next unlock")
					}

					// ─── Sprint 10 / L3 — Stamp last unlock time for self-destruct ──
					config.lastUnlockAt = System.currentTimeMillis()

					// ─── TODO #9 — Security warning on unlock ──────────────────────
					// Check for root/debugger and log a warning.
					try {
						if (securityChecker.isDebuggerAttached()) {
							Timber.w("SECURITY WARNING: Debugger is attached during unlock!")
						}
						if (securityChecker.isRooted()) {
							Timber.w("SECURITY WARNING: Device is rooted during unlock!")
						}
					} catch (e: Exception) {
						Timber.w(e, "Security check failed (non-fatal)")
					}

					session
				}

			// Sprint 7 / P2 — Record failed attempt on unlock failure.
			if (result.isFailure) {
				try {
					breakInDetector.recordFailedAttempt()
				} catch (e: Exception) {
					Timber.w(e, "BreakInDetector.recordFailedAttempt failed (non-fatal)")
				}
			}

			return result
		}

		/**
		 * Try unwrapping each [passwordProtections] row with the user's password.
		 * Returns the first VMK that successfully unwraps (auth tag verifies).
		 * Throws if no row unwraps successfully (= wrong password).
		 */
		private suspend fun unlockMultiVaultPassword(
			request: UnlockRequest.Password,
			passwordProtections: List<VaultProtection>,
		): javax.crypto.SecretKey {
			for (protection in passwordProtections) {
				try {
					return passwordProtectionHandler.unlock(request, protection)
				} catch (e: Exception) {
					// Auth tag verification failed (wrong password for THIS row) —
					// try the next row.
					Timber.d("unlockMultiVaultPassword: row ${protection.id} rejected password — trying next")
				}
			}
			throw IllegalStateException("Password did not unlock any vault")
		}

		suspend fun create(request: CreateRequest) {
			val protection =
				when (request) {
					is CreateRequest.Password -> passwordProtectionHandler.create(request)
					is CreateRequest.Biometric -> biometricProtectionHandler.create(request)
					is CreateRequest.RecoveryPhrase -> recoveryPhraseProtectionHandler.create(request)
				}

			vaultProtectionRepository.createProtection(protection)
		}

		/**
		 * Create a new vault protected by [password].
		 */
		suspend fun createNewVault(password: String): VaultSession {
			create(CreateRequest.Password(password))
			val result = unlock(UnlockRequest.Password(password))
			return result.getOrThrow()
		}

		/**
		 * Create a local [VaultProtection] of type [VaultProtectionType.Password] that wraps
		 * an EXISTING VMK (from [session]) with the given [password].
		 */
		suspend fun createPasswordProtectionFromSession(
			password: String,
			session: VaultSession,
		) {
			val salt = ByteArray(SALT_SIZE).also { SecureRandom().nextBytes(it) }
			val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }
			val kdf = Kdf.PBKDF2WithHmacSHA256
			val algorithm = Algorithm.AesCbcPkcs7Padding

			val params =
				VaultProtectionParams(
					salt = Base64.encode(salt),
					iv = Base64.encode(iv),
					kdf = kdf,
					kdfIterations = KEK_ITERATIONS,
					algorithm = algorithm,
					keySize = KEK_SIZE,
				)

			val kek =
				keyGen.derivePasswordKeyEncryptionKey(
					password = password,
					salt = salt,
					kdf = kdf,
					kdfIterations = KEK_ITERATIONS,
					keySize = KEK_SIZE,
				)

			val cipher =
				Cipher.getInstance(algorithm.value).apply {
					init(Cipher.ENCRYPT_MODE, kek, IvParameterSpec(iv))
				}

			val wrappedVmk = cipher.doFinal(session.vmk.encoded)

			val protection =
				VaultProtection(
					id = UUID.randomUUID().toString(),
					type = VaultProtectionType.Password,
					wrappedVMK = wrappedVmk,
					params = params,
				)
			vaultProtectionRepository.createProtection(protection)
		}

		suspend fun reset(type: VaultProtectionType) {
			vaultProtectionRepository.removeProtection(type)

			when (type) {
				VaultProtectionType.Password -> passwordProtectionHandler.reset()
				VaultProtectionType.Biometric -> {
					config.biometricAuthenticationEnabled = false
					biometricProtectionHandler.reset()
				}
				VaultProtectionType.RecoveryPhrase -> recoveryPhraseProtectionHandler.reset()
			}
		}

		suspend fun isSetup(type: VaultProtectionType): Boolean = vaultProtectionRepository.getProtection(type) != null

		suspend fun canMigrate(type: VaultProtectionType): Boolean =
			when (type) {
				VaultProtectionType.Password -> passwordProtectionHandler.canMigrate()
				VaultProtectionType.Biometric -> biometricProtectionHandler.canMigrate()
				VaultProtectionType.RecoveryPhrase -> recoveryPhraseProtectionHandler.canMigrate()
			}

		suspend fun canUnlock(): Boolean {
			val passwordSetup = isSetup(VaultProtectionType.Password)
			val biometricSetup = isSetup(VaultProtectionType.Biometric)
			val recoveryPhraseSetup = isSetup(VaultProtectionType.RecoveryPhrase)
			val protectionsAreSetup = passwordSetup || biometricSetup || recoveryPhraseSetup
			val canMigrate = passwordProtectionHandler.canMigrate() || biometricProtectionHandler.canMigrate()

			return protectionsAreSetup || canMigrate
		}
	}

/**
 * Hook called by [VaultService.unlock] after a successful unlock to backfill
 * any rows with `vault_id IS NULL` (created before v11 migration).
 */
fun interface VaultIdBackfillHook {
	suspend fun backfillVaultId(vaultId: String)
}
