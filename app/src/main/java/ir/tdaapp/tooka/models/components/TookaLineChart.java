package ir.tdaapp.tooka.models.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;

public class TookaLineChart extends LineChart {

  public TookaLineChart(Context context) {
    super(context);
  }

  public TookaLineChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TookaLineChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    switch (ev.getAction()) {
      case MotionEvent.ACTION_MOVE:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_SCROLL:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.AXIS_SCROLL:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
    }
    return super.dispatchTouchEvent(ev);
  }
}
