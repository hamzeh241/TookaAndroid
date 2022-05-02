package ir.tdaapp.tooka.models.preference

import android.content.Context
import android.content.SharedPreferences

class TokenPreferences {

  companion object {
    const val TAG = "TokenPreferences"

    const val PREFERENCE_KEY = "TOOKA_PREFS"
    const val TOKEN_KEY = "TOOKA_TOKEN_KEY"
  }

  fun add(context: Context, token: String) {
    val editor: SharedPreferences.Editor =
      context.getSharedPreferences(UserPreferences.PREFERENCE_KEY, Context.MODE_PRIVATE)
        .edit()
    editor.putString(TOKEN_KEY, token)
    editor.apply()
  }

  fun getToken(context: Context): String? {
    val editor: SharedPreferences = context.getSharedPreferences(
      UserPreferences.PREFERENCE_KEY,
      Context.MODE_PRIVATE
    )
    return editor.getString(TOKEN_KEY, "")
  }
}