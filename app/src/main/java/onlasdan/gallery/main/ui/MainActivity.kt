package onlasdan.gallery.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import onlasdan.gallery.encryption.domain.VaultService
import onlasdan.gallery.gallery.ui.navigation.NavigateToGallery
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@Inject
	lateinit var vaultService: VaultService

	@Inject
	lateinit var navigateToGallery: NavigateToGallery

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
		}
	}
}
