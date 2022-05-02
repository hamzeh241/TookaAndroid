package ir.tdaapp.tooka.models.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.card.MaterialCardView;

public class TookaMaterialCardView extends MaterialCardView {

  public TookaMaterialCardView(Context context) {
    super(context);
  }

  public TookaMaterialCardView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TookaMaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return true;
  }
}
