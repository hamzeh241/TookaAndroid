package ir.tdaapp.tooka.util

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class TookaSwipeCallback(
  swipeDir: Int,
  val onSwipe: (viewHolder: RecyclerView.ViewHolder, direction: Int)->Unit,
  val onDraw: (decorator: RecyclerViewSwipeDecorator.Builder)->Unit
): ItemTouchHelper.SimpleCallback(0, swipeDir) {
  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    onSwipe.invoke(viewHolder, direction)
  }

  override fun onChildDraw(
    c: Canvas,
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    dX: Float,
    dY: Float,
    actionState: Int,
    isCurrentlyActive: Boolean
  ) {
    val decorator = RecyclerViewSwipeDecorator.Builder(
      c,
      recyclerView,
      viewHolder,
      dX,
      dY,
      actionState,
      isCurrentlyActive
    )

    onDraw.invoke(decorator)

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
  }
}