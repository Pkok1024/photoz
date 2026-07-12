package onlasdan.gallery.imageviewer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import onlasdan.gallery.model.repositories.PhotoRepository
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel
	@Inject
	constructor(
		app: Application,
		private val photoRepository: PhotoRepository,
	) : AndroidViewModel(app)
