package onlasdan.gallery.gallery.albums.detail.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel
	@Inject
	constructor(
		app: Application,
	) : AndroidViewModel(app)
