package onlasdan.gallery.imageviewer.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ImageViewerScreen(vararg args: Any?) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		Text("ImageViewerScreen Placeholder")
	}
}
