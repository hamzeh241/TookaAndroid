package ir.tdaapp.tooka.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class PreferenceHelper(private val sharedPreferences: SharedPreferences,private val context: Context) {

    private val nightMode: Int
        get() = sharedPreferences.getInt(PREFERENCE_NIGHT_MODE, PREFERENCE_NIGHT_MODE_DEF_VAL)

    private val _nightModeLive: MutableLiveData<Int> = MutableLiveData()
    val nightModeLive: LiveData<Int>
        get() = _nightModeLive

    var isDarkTheme: Boolean = false
        get() = nightMode == AppCompatDelegate.MODE_NIGHT_YES
        set(value) {
            sharedPreferences.edit().putInt(
                PREFERENCE_NIGHT_MODE, if (value) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            ).apply()
            field = value
        }

    private val _isDarkThemeLive: MutableLiveData<Boolean> = MutableLiveData()
    val isDarkThemeLive: LiveData<Boolean>
        get() = _isDarkThemeLive

    private val locale: String?
        get() = sharedPreferences.getString(PREFERENCE_LOCALE, PREFERENCE_LOCALE_DEF_VAL)

    private val _localeLive: MutableLiveData<String> = MutableLiveData()
    val localeLive: LiveData<String>
        get() = _localeLive

    var localeMode: String = "fa"
        get() = ContextUtils.getLocale(context).toString()
        set(value) {
            ContextUtils.updateLocale(context, Locale("fa"))
            sharedPreferences.edit().putString(
                PREFERENCE_LOCALE, value
            ).apply()
            field = value
        }

    private val _isLocaleLive: MutableLiveData<String> = MutableLiveData()
    val isLocaleLive: LiveData<String>
        get() = _isLocaleLive

    private val preferenceChangedListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PREFERENCE_NIGHT_MODE -> {
                    _nightModeLive.value = nightMode
                    _isDarkThemeLive.value = isDarkTheme
                }
                PREFERENCE_LOCALE -> {
                    _localeLive.value = locale
                    _isLocaleLive.value = localeMode
                }
            }
        }

    init {
        // Init preference LiveData objects.
        _nightModeLive.value = nightMode
        _isDarkThemeLive.value = isDarkTheme

        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangedListener)
    }

    companion object {
        private const val PREFERENCE_NIGHT_MODE = "preference_night_mode"
        private const val PREFERENCE_LOCALE = "preference_locale"
        private const val PREFERENCE_LOCALE_DEF_VAL = "fa"
        private const val PREFERENCE_NIGHT_MODE_DEF_VAL = AppCompatDelegate.MODE_NIGHT_NO
    }
}