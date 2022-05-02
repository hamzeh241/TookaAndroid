package ir.tdaapp.tooka.models.components;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;


import java.math.BigDecimal;

import ir.tdaapp.tooka.R;
import ir.tdaapp.tooka.models.util.UtilKt;

public class ChartMarkerView extends MarkerView {

  private TextView tvContent;

  public ChartMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    tvContent = (TextView) findViewById(R.id.txtMarker);
  }

  @Override
  public void refreshContent(Entry e, Highlight highlight) {

    BigDecimal bigDecimal = new BigDecimal(UtilKt.toEnglishNumbers(String.valueOf(e.getY()))
      .replace("٬","").replace(",",""));

    tvContent.setText("" + bigDecimal.toPlainString());

    super.refreshContent(e, highlight);
  }

  private MPPointF mOffset;

  @Override
  public MPPointF getOffset() {

    if(mOffset == null) {

      mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
    }

    return mOffset;
  }}