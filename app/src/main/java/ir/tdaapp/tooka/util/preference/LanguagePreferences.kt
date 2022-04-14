package ir.tdaapp.tooka.util.preference

import android.content.Context
import android.content.SharedPreferences

class LanguagePreferences {

  companion object {
    const val TAG = "LanguagePreferences"

    const val PREFERENCE_KEY = "TOOKA_PREFS"
    const val LANG_KEY = "TOOKA_LANG_KEY"
  }

  fun add(context: Context, lang: String) {
    val editor: SharedPreferences.Editor =
      context.getSharedPreferences(UserPreferences.PREFERENCE_KEY, Context.MODE_PRIVATE)
        .edit()
    editor.putString(LANG_KEY, lang)
    editor.apply()
  }

  fun getLang(context: Context): String? {
    val editor: SharedPreferences = context.getSharedPreferences(
      UserPreferences.PREFERENCE_KEY,
      Context.MODE_PRIVATE
    )
    return editor.getString(LANG_KEY, "en")
  }

}