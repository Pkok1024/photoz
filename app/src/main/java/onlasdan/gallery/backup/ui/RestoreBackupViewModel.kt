package onlasdan.gallery.backup.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestoreBackupViewModel
	@Inject
	constructor(
		app: Application,
	) : AndroidViewModel(app)
