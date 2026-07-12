package onlasdan.gallery.settings.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import onlasdan.gallery.R
import onlasdan.gallery.settings.domain.models.SettingsEnum

data class PreferenceScreenConfig(
	val sections: List<PreferenceSection>,
)

data class PreferenceSection(
	@get:StringRes val title: Int,
	@get:StringRes val summary: Int?,
	val preferences: List<Preference>,
)

sealed interface Preference {
	val key: String

	@get:DrawableRes val icon: Int

	@get:StringRes val title: Int

	data class Simple(
		override val key: String,
		override val icon: Int,
		override val title: Int,
		val summary: Int,
	) : Preference

	data class Switch(
		override val key: String,
		override val icon: Int,
		override val title: Int,
		val summary: Int,
		val default: Boolean,
	) : Preference

	data class Enum<T : SettingsEnum>(
		override val key: String,
		override val icon: Int,
		override val title: Int,
		val default: T,
		val possibleValues: List<T>,
	) : Preference

	data class DynamicSummary(
		override val key: String,
		override val icon: Int,
		override val title: Int,
		val summaryPlaceholder: Int,
	) : Preference

	data class Info(
		override val key: String,
		override val icon: Int,
		override val title: Int,
		val summaryPlaceholder: Int,
	) : Preference
}

val PreferenceScreenConfigContent: List<PreferenceSection> = listOf(
	PreferenceSection(
		title = R.string.settings_category_security,
		summary = null,
		preferences = listOf(
			Preference.Simple(
				key = "action_change_password",
				icon = R.drawable.ic_password,
				title = R.string.change_password_title,
				summary = R.string.settings_security_change_password_summary
			)
		)
	)
)
