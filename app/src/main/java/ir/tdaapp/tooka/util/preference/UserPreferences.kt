package ir.tdaapp.tooka.util.preference

import android.content.Context
import android.content.SharedPreferences

class UserPreferences {

  companion object {
    const val PREFERENCE_KEY = "TOOKA_PREFS"
    const val USER_KEY = "TOOKA_USER_KEY"
  }

  fun add(context: Context, userId: Int) {
    val editor: SharedPreferences.Editor =
      context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        .edit()
    editor.putInt(USER_KEY, userId)
    editor.apply()
  }

  fun hasAccount(context: Context): Boolean = getUserId(context) > 0

  fun getUserId(context: Context): Int {
    val editor: SharedPreferences = context.getSharedPreferences(
      PREFERENCE_KEY,
      Context.MODE_PRIVATE
    )
    return editor.getInt(USER_KEY, 0)
  }
}