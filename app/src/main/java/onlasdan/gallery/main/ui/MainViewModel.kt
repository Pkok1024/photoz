package onlasdan.gallery.main.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import onlasdan.gallery.gallery.ui.importing.SharedUrisStore
import onlasdan.gallery.main.ui.navigation.MainMenuUiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel
	@Inject
	constructor(
		app: Application,
		private val sharedUrisStore: SharedUrisStore,
	) : AndroidViewModel(app) {
		private val _mainMenuUiState = MutableStateFlow(MainMenuUiState(0))
		val mainMenuUiState = _mainMenuUiState.asStateFlow()
	}
