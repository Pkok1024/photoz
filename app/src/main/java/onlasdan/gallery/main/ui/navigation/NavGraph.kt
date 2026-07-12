package onlasdan.gallery.main.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import onlasdan.gallery.encryption.domain.VaultService
import onlasdan.gallery.gallery.ui.navigation.NavigateToGallery

object Routes {
	const val Initial = "initial"
	const val OnBoarding = "onboarding"
	const val RepoSetup = "repo_setup"
	const val Setup = "setup"
	const val Unlock = "unlock"
	const val Gallery = "gallery"
	const val Albums = "albums"
	const val Settings = "settings"
	const val Credits = "credits"
	const val Trash = "trash"
	const val About = "about"
	const val AlbumDetail = "album_detail/{album_uuid}"
	const val ImageViewer = "image_viewer/{photo_uuid}?album_uuid={album_uuid}"
	const val EncryptionMigration = "encryption_migration"
	const val RecoveryPhraseRestore = "recovery_phrase_restore"
}

@Composable
fun PhotoZNavGraph(
	navController: NavHostController,
	vaultService: VaultService,
	navigateToGallery: NavigateToGallery,
	modifier: Modifier = Modifier
) {
	NavHost(
		navController = navController,
		startDestination = Routes.Initial,
		modifier = modifier
	) {
		composable(Routes.Initial) { PlaceholderScreen("Initial") }
		composable(Routes.OnBoarding) { PlaceholderScreen("OnBoarding") }
		composable(Routes.RepoSetup) { PlaceholderScreen("RepoSetup") }
		composable(Routes.Setup) { PlaceholderScreen("Setup") }
		composable(Routes.Unlock) { PlaceholderScreen("Unlock") }
		composable(Routes.Gallery) { PlaceholderScreen("Gallery") }
		composable(Routes.Albums) { PlaceholderScreen("Albums") }
		composable(Routes.Settings) { PlaceholderScreen("Settings") }
		composable(Routes.Trash) { PlaceholderScreen("Trash") }
		composable(Routes.AlbumDetail) { PlaceholderScreen("AlbumDetail") }
		composable(Routes.ImageViewer) { PlaceholderScreen("ImageViewer") }
		composable(Routes.EncryptionMigration) { PlaceholderScreen("EncryptionMigration") }
		composable(Routes.RecoveryPhraseRestore) { PlaceholderScreen("RecoveryPhraseRestore") }
	}
}

@Composable
fun PlaceholderScreen(name: String) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text(text = "TODO: $name Screen")
	}
}
