package ir.tdaapp.tooka.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class TookaRecyclerView extends RecyclerView {

  public TookaRecyclerView(@NonNull Context context) {
    super(context);
  }

  public TookaRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TookaRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {

      case MotionEvent.ACTION_MOVE:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
    }
    return super.dispatchTouchEvent(ev);
  }
}
