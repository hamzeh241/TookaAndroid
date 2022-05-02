package ir.tdaapp.tooka.models.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import ir.tdaapp.tooka.R;

public class TookaSearchView extends LinearLayout {

  private EditText search;
  private ImageButton submit;

  private OnSearchCallback callback;

  public void setCallback(OnSearchCallback callback) {
    this.callback = callback;
  }

  public interface OnSearchCallback {
    void onClick();

    void onLongClick();
  }

  public TookaSearchView(Context context) {
    super(context);
    initialize(context);
  }

  public TookaSearchView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context);
  }

  public TookaSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context);
  }

  public TookaSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.component_tooka_search_view, this, true);

    search = view.findViewById(R.id.edtSearch);
    submit = view.findViewById(R.id.imgSearch);

    search.setOnKeyListener((v, keyCode, event) -> {
      if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
        (keyCode == KeyEvent.KEYCODE_ENTER)) {
        submit.performClick();
        return true;
      }
      return false;
    });

    submit.setOnClickListener(v -> {
      callback.onClick();
    });
    submit.setOnLongClickListener(v -> {
      callback.onLongClick();
      return true;
    });
  }

  public String getQuery() {
    return search.getText().toString();
  }


}
