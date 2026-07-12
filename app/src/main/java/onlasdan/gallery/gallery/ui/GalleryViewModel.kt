package onlasdan.gallery.gallery.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel
	@Inject
	constructor(
		app: Application,
	) : AndroidViewModel(app)
