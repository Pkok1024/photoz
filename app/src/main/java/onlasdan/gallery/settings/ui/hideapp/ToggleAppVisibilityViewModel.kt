package onlasdan.gallery.settings.ui.hideapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import onlasdan.gallery.settings.data.Config
import onlasdan.gallery.settings.ui.hideapp.usecase.ToggleMainComponentUseCase
import javax.inject.Inject

@HiltViewModel
class ToggleAppVisibilityViewModel
	@Inject
	constructor(
		app: Application,
		private val config: Config,
		private val toggleMainComponentUseCase: ToggleMainComponentUseCase,
	) : AndroidViewModel(app)
