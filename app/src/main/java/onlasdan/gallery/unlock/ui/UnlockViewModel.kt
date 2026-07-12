package onlasdan.gallery.unlock.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import onlasdan.gallery.encryption.domain.SessionRepository
import onlasdan.gallery.encryption.domain.VaultService
import onlasdan.gallery.encryption.domain.crypto.Bip39WordCount
import onlasdan.gallery.encryption.domain.models.CreateRequest
import onlasdan.gallery.encryption.domain.models.UnlockRequest
import onlasdan.gallery.encryption.domain.models.VaultProtectionType
import onlasdan.gallery.encryption.migration.LegacyEncryptionMigrator
import onlasdan.gallery.other.extensions.empty
import onlasdan.gallery.security.BreakInDetector
import onlasdan.gallery.security.SecurityChecker
import onlasdan.gallery.settings.data.Config
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel
	@Inject
	constructor(
		app: Application,
		private val config: Config,
		private val vaultService: VaultService,
		private val sessionRepository: SessionRepository,
		private val legacyEncryptionMigrator: LegacyEncryptionMigrator,
		private val breakInDetector: BreakInDetector,
		private val securityChecker: SecurityChecker,
	) : AndroidViewModel(app) {
		var password: String = String.empty
		val unlockState = MutableStateFlow<UnlockState>(UnlockState.Initial)
		val securityWarning = MutableStateFlow<String?>(null)
		val breakInWarning = MutableStateFlow<String?>(null)

		init {
			securityWarning.value = securityChecker.getSecurityWarning()
		}

		fun unlockWithPassword() {
			unlockState.update { UnlockState.Loading }
			viewModelScope.launch {
				vaultService.unlock(UnlockRequest.Password(password)).onSuccess { session ->
					sessionRepository.set(session)
					breakInWarning.value = breakInDetector.consumeWarningIfAny()
					if (legacyEncryptionMigrator.migrationNeeded() || config.preferences.getBoolean("legacy^currentlyMigrating", false)) {
						unlockState.update { UnlockState.StartLegacyMigration }
					} else if (!vaultService.isSetup(VaultProtectionType.RecoveryPhrase)) {
						vaultService.create(CreateRequest.RecoveryPhrase(session, Bip39WordCount.Twelve))
						unlockState.update { UnlockState.ShowRecoveryPhrase }
					} else {
						unlockState.update { UnlockState.Unlocked }
					}
				}.onFailure {
					unlockState.update { UnlockState.PasswordError }
				}
			}
		}
	}
