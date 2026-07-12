package onlasdan.gallery.gallery.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GalleryScreen(vararg args: Any?) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text("GalleryScreen Placeholder")
	}
}
