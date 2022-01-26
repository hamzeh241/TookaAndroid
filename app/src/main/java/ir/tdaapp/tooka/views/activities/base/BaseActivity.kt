package ir.tdaapp.tooka.views.activities.base

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.application.App
import java.util.*

abstract class BaseActivity: AppCompatActivity() {

  val preferenceRepository = App.preferenceHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(getLayout().root)
    init()
    initToolbar()
    initTransitions()
    initTheme()
  }

  override fun attachBaseContext(newBase: Context?) {
    ContextUtils.updateLocale(newBase!!, Locale("fa"))
    super.attachBaseContext(newBase)
  }

  fun setAllTypefaces(view: View, tf: Typeface?) {
    if (view is ViewGroup) {
      for (i in 0 until view.childCount) setAllTypefaces(
        view.getChildAt(
          i
        ), tf
      )
    } else if (view is TextView) view.setTypeface(tf)
  }

  fun centerToolbarTitle(toolbar: Toolbar) {
    val title: CharSequence = toolbar.getTitle()
    val outViews: ArrayList<View> = ArrayList(1)
    toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT)
    if (!outViews.isEmpty()) {
      val titleView = outViews[0] as TextView
      titleView.gravity = Gravity.CENTER
      val layoutParams: Toolbar.LayoutParams = titleView.layoutParams as Toolbar.LayoutParams
      layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
      toolbar.requestLayout()
      //also you can use titleView for changing font: titleView.setTypeface(Typeface);
    }
  }

  fun getCurrentLocale(): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      this.resources.configuration.locales[0]
    } else {
      this.resources.configuration.locale
    }
  }

  abstract fun init()

  abstract fun initTransitions()

  abstract fun initToolbar()

  abstract fun initLanguage()

  abstract fun initTheme()

  abstract fun getLayout(): ViewBinding
}