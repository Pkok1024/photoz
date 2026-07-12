package onlasdan.gallery.settings.ui.compose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import onlasdan.gallery.settings.data.Config
import onlasdan.gallery.settings.domain.PreferenceScreenConfig
import onlasdan.gallery.settings.domain.PreferenceScreenConfigContent
import javax.inject.Inject

sealed interface SyncConfigStatus {
	data object NotConfigured : SyncConfigStatus

	data object Validating : SyncConfigStatus

	data class Configured(
		val remoteName: String
	) : SyncConfigStatus

	data class AwaitingRemoteChoice(
		val remotes: List<String>
	) : SyncConfigStatus

	data class Invalid(
		val reason: String
	) : SyncConfigStatus

	data class ImportFailed(
		val reason: String
	) : SyncConfigStatus
}

@HiltViewModel
class SettingsViewModel
	@Inject
	constructor(
		app: Application,
		private val config: Config,
	) : AndroidViewModel(app) {
		val screenConfig = PreferenceScreenConfig(PreferenceScreenConfigContent)
		val infoSummaries = MutableStateFlow<Map<String, String>>(emptyMap())
		private val _syncConfigStatus = MutableStateFlow<SyncConfigStatus>(SyncConfigStatus.NotConfigured)
		val syncConfigStatus: StateFlow<SyncConfigStatus> = _syncConfigStatus.asStateFlow()
		private val _trashCount = MutableStateFlow<Int?>(null)
		val trashCount: StateFlow<Int?> = _trashCount.asStateFlow()

		fun cleanupBackup() {}

		fun refreshStorageStats() {}

		fun resetApp() {}
	}
