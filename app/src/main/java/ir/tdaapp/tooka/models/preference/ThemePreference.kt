package ir.tdaapp.tooka.models.preference

import android.content.Context
import android.content.SharedPreferences

class ThemePreference(private val context: Context) {

  companion object {
    const val PREFERENCE_KEY = "TOOKA_PREFS"
    const val THEME_KEY = "TOOKA_THEME_KEY"
  }

  fun add(isNight: Boolean,callback:(SharedPreferences,String) -> Unit): Boolean {
    val editor: SharedPreferences.Editor =
      context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        .edit()

    context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(callback)
    editor.clear()
    editor.putBoolean(THEME_KEY, isNight)
    return editor.commit()
  }

  fun isNightMode(): Boolean {
    val editor: SharedPreferences = context.getSharedPreferences(
      PREFERENCE_KEY,
      Context.MODE_PRIVATE
    )
    return editor.getBoolean(THEME_KEY, false)
  }
}