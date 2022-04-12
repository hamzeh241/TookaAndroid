package ir.tdaapp.tooka.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.airbnb.lottie.model.Marker;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class TookaCandlestickChart extends CandleStickChart {

  public TookaCandlestickChart(Context context) {
    super(context);
  }

  public TookaCandlestickChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TookaCandlestickChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setOnChartValueSelectedListener(OnChartValueSelectedListener l) {
    super.setOnChartValueSelectedListener(l);
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
      case MotionEvent.ACTION_DOWN:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
      case MotionEvent.ACTION_UP:
        getParent().requestDisallowInterceptTouchEvent(true);
        break;
    }
    return super.dispatchTouchEvent(ev);
  }

  final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
    public void onLongPress(MotionEvent e) {
      MarkerView markerView = (MarkerView) getMarker();
      isLongClicked = true;

      //get angle and index of dataset
//      float angle = getAngleForPoint(e.getX(), e.getY());
//      int indexForAngle = getIndexForAngle(angle);

      // takes index of angle, Y-Position and dataSetIndex of 0
//      Highlight highlight = new Highlight(indexForAngle, e.getY(), 0);
//      highlightValue(highlight);

      // redraw
      invalidate();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      isLongClicked = false;
      return super.onSingleTapUp(e);
    }
  });

  Boolean isLongClicked = false;

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean handled = true;
    // if there is no marker view or drawing marker is disabled
    if (isShowingMarker() && this.getMarker() instanceof MarkerView){
      gestureDetector.onTouchEvent(event);
    }
    handled = super.onTouchEvent(event);
    return handled;
  }

  // draw markers on highlighter values
  @Override
  protected void drawMarkers(Canvas canvas) {
    // if there is no marker view or drawing marker is disabled
    if (mMarker == null || !isDrawMarkersEnabled() || !valuesToHighlight())
      return;

    for (Highlight highlight : mIndicesToHighlight) {

      IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

      Entry e = mData.getEntryForHighlight(highlight);
      int entryIndex = set.getEntryIndex(e);

      // make sure entry not null
      if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
        continue;

      float[] pos = getMarkerPosition(highlight);

      // check bounds
      if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
        continue;

      // callbacks to update the content
      mMarker.refreshContent(e, highlight);

      // draw the marker
      if (isLongClicked) {
        mMarker.draw(canvas, pos[0], pos[1]);
      }
    }
  }

  private boolean isShowingMarker(){
    return mMarker != null && isDrawMarkersEnabled();
  }
}
