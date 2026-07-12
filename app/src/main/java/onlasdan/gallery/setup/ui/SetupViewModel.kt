package onlasdan.gallery.setup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import onlasdan.gallery.encryption.domain.PasswordUtils
import onlasdan.gallery.encryption.domain.SessionRepository
import onlasdan.gallery.encryption.domain.VaultService
import onlasdan.gallery.encryption.domain.crypto.Bip39WordCount
import onlasdan.gallery.encryption.domain.models.CreateRequest
import onlasdan.gallery.encryption.domain.models.UnlockRequest
import onlasdan.gallery.other.extensions.empty
import onlasdan.gallery.settings.data.Config
import onlasdan.gallery.sync.rclone.RepoManager
import onlasdan.gallery.telemetry.domain.Signal
import onlasdan.gallery.telemetry.domain.TelemetryService
import javax.inject.Inject

@HiltViewModel
class SetupViewModel
	@Inject
	constructor(
		app: Application,
		private val config: Config,
		private val vaultService: VaultService,
		private val sessionRepository: SessionRepository,
		private val telemetryService: TelemetryService,
		private val repoManager: RepoManager,
	) : AndroidViewModel(app) {
		var password: String = String.empty
		var confirmPassword: String = String.empty
		val setupState: MutableStateFlow<SetupState> = MutableStateFlow(SetupState.SETUP)

		fun onSetupClicked() = viewModelScope.launch {
			if (PasswordUtils.validatePasswords(password, confirmPassword)) {
				setupState.value = SetupState.LOADING
				vaultService.create(CreateRequest.Password(password))
				vaultService.unlock(UnlockRequest.Password(password)).onSuccess { session ->
					sessionRepository.set(session)
					vaultService.create(CreateRequest.RecoveryPhrase(session, Bip39WordCount.Twelve))
					config.justFinishedSetup = true
					telemetryService.signal(Signal.SetupCompleted)
					setupState.value = SetupState.SHOW_RECOVERY_PHRASE
				}.onFailure {
					setupState.value = SetupState.SETUP
				}
			}
		}
	}
