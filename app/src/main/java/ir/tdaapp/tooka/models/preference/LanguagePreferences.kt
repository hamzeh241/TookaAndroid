package ir.tdaapp.tooka.models.preference

import android.content.Context
import android.content.SharedPreferences

class LanguagePreferences(private val context: Context) {

  companion object {
    const val TAG = "LanguagePreferences"

    const val PREFERENCE_KEY = "TOOKA_PREFS"
    const val LANG_KEY = "TOOKA_LANG_KEY"
  }

  fun add(lang: String, callback: (SharedPreferences, String)->Unit) {
    val preference = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preference.edit()

    preference.registerOnSharedPreferenceChangeListener(callback)
    editor.putString(LANG_KEY, lang)
    editor.apply()
  }

  fun getLang(): String {
    val editor: SharedPreferences = context.getSharedPreferences(
      PREFERENCE_KEY,
      Context.MODE_PRIVATE
    )
    return editor.getString(LANG_KEY, "en") ?: "en"
  }

}