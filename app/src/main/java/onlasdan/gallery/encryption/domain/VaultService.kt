package onlasdan.gallery.encryption.domain

import kotlinx.coroutines.delay
import onlasdan.gallery.encryption.domain.crypto.KeyGen
import onlasdan.gallery.encryption.domain.handlers.*
import onlasdan.gallery.encryption.domain.models.*
import onlasdan.gallery.security.BreakInDetector
import onlasdan.gallery.security.SecurityChecker
import onlasdan.gallery.settings.data.Config
import javax.inject.Inject
import javax.inject.Singleton

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
	) {
		suspend fun unlock(request: UnlockRequest): Result<VaultSession> = runCatching {
			val vmk = when (request) {
				is UnlockRequest.Password -> {
					val protections = vaultProtectionRepository.getAllProtections(VaultProtectionType.Password)
					unlockMultiVaultPassword(request, protections)
				}
				is UnlockRequest.Biometric -> {
					val protection = vaultProtectionRepository.getProtection(VaultProtectionType.Biometric)
					biometricProtectionHandler.unlock(request, protection!!)
				}
				is UnlockRequest.RecoveryPhrase -> {
					val protection = vaultProtectionRepository.getProtection(VaultProtectionType.RecoveryPhrase)
					recoveryPhraseProtectionHandler.unlock(request, protection!!)
				}
			}
			VaultSession(vmk)
		}

		private suspend fun unlockMultiVaultPassword(
			request: UnlockRequest.Password,
			protections: List<VaultProtection>
		): javax.crypto.SecretKey {
			val startTime = System.currentTimeMillis()
			var foundVmk: javax.crypto.SecretKey? = null

			val maxSimulatedVaults = 5
			val actualCount = protections.size

			for (i in 0 until maxOf(actualCount, maxSimulatedVaults)) {
				if (i < actualCount) {
					try {
						val vmk = passwordProtectionHandler.unlock(request, protections[i])
						if (foundVmk == null) foundVmk = vmk
					} catch (e: Exception) {
					}
				} else {
					delay(10)
				}
			}

			val elapsed = System.currentTimeMillis() - startTime
			val targetTime = 1000L
			if (elapsed < targetTime) {
				delay(targetTime - elapsed)
			}

			return foundVmk ?: throw IllegalStateException("Invalid password")
		}

		suspend fun create(request: CreateRequest) {
			val protection = when (request) {
				is CreateRequest.Password -> passwordProtectionHandler.create(request)
				is CreateRequest.Biometric -> biometricProtectionHandler.create(request)
				is CreateRequest.RecoveryPhrase -> recoveryPhraseProtectionHandler.create(request)
			}
			vaultProtectionRepository.createProtection(protection)
		}

		suspend fun createNewVault(password: String): VaultSession {
			create(CreateRequest.Password(password))
			val result = unlock(UnlockRequest.Password(password))
			return result.getOrThrow()
		}

		suspend fun createPasswordProtectionFromSession(password: String, session: VaultSession) {
		}

		suspend fun isSetup(type: VaultProtectionType): Boolean {
			return vaultProtectionRepository.countProtections(type) > 0
		}

		suspend fun canUnlock(): Boolean {
			return vaultProtectionRepository.countProtections(VaultProtectionType.Password) > 0
		}

		suspend fun reset(type: VaultProtectionType) {
			vaultProtectionRepository.removeProtection(type)
		}
	}
