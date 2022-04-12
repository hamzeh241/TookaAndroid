package ir.tdaapp.tooka.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.lang.Exception

class DatastoreManager(val context: Context) {

  private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "Tooka")

  companion object {
    val ID = intPreferencesKey("ID")
    val PHONE_NUMBER = stringPreferencesKey("PHONE_NUMBER")
  }

  suspend fun saveUserInfo(phone: String, id: Int) {
    context.datastore.edit {

      it[PHONE_NUMBER] = phone
      it[ID] = id

    }
  }

  fun getUserId() = context.datastore.data.map {
    it[ID]
  }

  suspend fun isUserSignedIn(
    exception: (e: Exception)->Unit = {},
    action: (result: Boolean, id: Int?)->Unit,
  ) {
    getUserId().catch { e ->
      exception(e as Exception)
    }.collect {
      action(it != null, it)
    }
  }
}