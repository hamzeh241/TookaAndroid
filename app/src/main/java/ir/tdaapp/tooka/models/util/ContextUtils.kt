package ir.tdaapp.tooka.models.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import androidx.annotation.DimenRes
import java.util.*

class ContextUtils(val base: Context): ContextWrapper(base) {

  companion object {

    fun getLocale(c: Context): Locale {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        c.resources.configuration.locales[0]
      } else {
        c.resources.configuration.locale
      }
    }

    fun updateLocale(c: Context, localeToSwitchTo: Locale): ContextWrapper {
      var context = c
      val resources: Resources = context.resources
      val configuration: Configuration = resources.configuration
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val localeList = LocaleList(localeToSwitchTo)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
      } else {
        configuration.locale = localeToSwitchTo
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        context = context.createConfigurationContext(configuration)
      } else {
        resources.updateConfiguration(configuration, resources.displayMetrics)
      }
      return ContextUtils(context)
    }


  }

  fun getDimen(@DimenRes id: Int): Int =
    base.resources.getDimension(id).toInt()
}