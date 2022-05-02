package ir.tdaapp.tooka.models.components

import android.content.Context
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import ir.tdaapp.tooka.R

class TookaCircleImage(context: Context
) : AppCompatImageView(context) {

  init {
    //the outline (view edges) of the view should be derived    from the background
    outlineProvider = ViewOutlineProvider.BACKGROUND
    //cut the view to match the view to the outline of the background
    clipToOutline = true
    //use the following background to calculate the outline
    setBackgroundResource(R.drawable.circle_image_background)

    //fill in the whole image view, crop if needed while keeping the center
    scaleType = ScaleType.CENTER_CROP
  }
}