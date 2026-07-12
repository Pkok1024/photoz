/*
 *   Copyright 2020–2026 PhotoZ
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package onlasdan.gallery.main.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import onlasdan.gallery.R
import onlasdan.gallery.ui.theme.AppTheme

@Composable
fun MainMenu(
	currentRoute: String?,
	onNavigationItemClicked: (String) -> Unit,
) {
	NavigationBar(
		containerColor = MaterialTheme.colorScheme.surface,
	) {
		MainNavItem(
			route = Routes.Gallery,
			currentRoute = currentRoute,
			iconRes = R.drawable.ic_image,
			label = stringResource(R.string.gallery_all_photos_label),
			onNavigationItemClicked = onNavigationItemClicked,
		)

		MainNavItem(
			route = Routes.Albums,
			additionalRoutes = listOf(Routes.AlbumDetail),
			currentRoute = currentRoute,
			iconRes = R.drawable.ic_folder,
			label = stringResource(R.string.gallery_albums_label),
			onNavigationItemClicked = onNavigationItemClicked,
		)

		MainNavItem(
			route = Routes.Settings,
			currentRoute = currentRoute,
			iconRes = R.drawable.ic_settings,
			label = stringResource(R.string.menu_main_settings),
			onNavigationItemClicked = onNavigationItemClicked,
		)
	}
}

@Preview
@Composable
private fun MainMenuPreview() {
	AppTheme {
		MainMenu(
			currentRoute = Routes.Gallery,
			onNavigationItemClicked = {},
		)
	}
}

@Composable
private fun RowScope.MainNavItem(
	route: String,
	currentRoute: String?,
	iconRes: Int,
	label: String,
	onNavigationItemClicked: (String) -> Unit,
	additionalRoutes: List<String> = emptyList(),
) {
	val selected = currentRoute == route ||
		additionalRoutes.any {
			currentRoute?.startsWith(it.substringBefore("/{")) == true
		}

	NavigationBarItem(
		selected = selected,
		onClick = { onNavigationItemClicked(route) },
		icon = {
			Icon(painter = painterResource(iconRes), contentDescription = label)
		},
		label = {
			Text(label)
		},
		alwaysShowLabel = true,
	)
}
