package ir.tdaapp.tooka.models.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ProgressBar
import com.google.android.material.button.MaterialButton
import ir.tdaapp.tooka.models.util.toPx

class TookaButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
  ) : MaterialButton(context, attrs, defStyle) {

    private var progressDrawable: Drawable

    init {
      progressDrawable = ProgressBar(context).indeterminateDrawable.apply {
        // apply any customization on drawable. not on progress view
        setBounds(0, 0, 24.toPx, 24.toPx)
        setTint(Color.WHITE)
      }
      compoundDrawablePadding = 4.toPx
    }

    var isLoading: Boolean = false
      set(value) {
        if (isLoading == value) return
        field = value

        val (startDrawable, topDrawable, endDrawable, bottomDrawable) = compoundDrawablesRelative
        if (value) {
          // add progress and keep others
          setCompoundDrawablesRelative(
            progressDrawable,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.start()
        } else {
          // remove progress
          setCompoundDrawablesRelative(
            null,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.stop()
        }
      }

    override fun onDetachedFromWindow() {
      (progressDrawable as? Animatable)?.stop()
      super.onDetachedFromWindow()
    }
}