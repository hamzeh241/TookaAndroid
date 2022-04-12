package ir.tdaapp.tooka.components

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class TookaSnackBar(
  private val view: View,
  private val text: String,
  @BaseTransientBottomBar.Duration private val duration: Int
) {

  private var snackbar: Snackbar

  init {
    snackbar = Snackbar.make(view, text, duration)
  }

  fun textConfig(config: (TextView)->Unit):TookaSnackBar {
    config(snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text))
    return this
  }

  fun backgroundConfig(config: (ViewGroup)->Unit):TookaSnackBar {
    config(snackbar.view as ViewGroup)
    return this
  }

  fun otherConfigs(config: (Snackbar) -> Unit):TookaSnackBar{
    config(snackbar)
    return this
  }

  fun show() = snackbar.show()
}