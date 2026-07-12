package onlasdan.gallery.settings.data

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import onlasdan.gallery.settings.data.Config.Companion.SECURITY_ALLOW_SCREENSHOTS

val Config.securityAllowScreenshotsFlow: Flow<Boolean>
	get() = callbackFlow {
		val listener = SharedPreferences.OnSharedPreferenceChangeListener { p, key ->
			if (key == SECURITY_ALLOW_SCREENSHOTS) {
				trySend(p.getBoolean(key, false))
			}
		}
		preferences.registerOnSharedPreferenceChangeListener(listener)
		awaitClose { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
	}.onStart {
		emit(preferences.getBoolean(SECURITY_ALLOW_SCREENSHOTS, false))
	}
