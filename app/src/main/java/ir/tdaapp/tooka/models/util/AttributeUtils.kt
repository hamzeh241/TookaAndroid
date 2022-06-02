package ir.tdaapp.tooka.models.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

fun getAttributeColor(c: Context, @AttrRes attr: Int): Int {
  val typedValue = TypedValue()
  val theme: Resources.Theme = c.theme
  theme.resolveAttribute(attr, typedValue, true)
  return typedValue.data
}