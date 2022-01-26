package ir.tdaapp.tooka.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.bumptech.glide.Glide;

import ir.tdaapp.tooka.R;

public class TookaCustomTextView extends LinearLayout {

  TextView title, subtitle;
  ImageView image;

  public TookaCustomTextView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context);

    TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TookaCustomTextView, 0, 0);
    readAttrs(arr);
    arr.recycle();
  }

  public TookaCustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context);

    TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TookaCustomTextView, defStyleAttr, 0);
    readAttrs(arr);
    arr.recycle();
  }

  public TookaCustomTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize(context);

    TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TookaCustomTextView, defStyleAttr, defStyleRes);
    readAttrs(arr);
    arr.recycle();
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.component_custom_text_view, this, true);

    title = view.findViewById(R.id.txtTitle);
    subtitle = view.findViewById(R.id.txtSubtitle);
    image = view.findViewById(R.id.image);
  }

  private void readAttrs(TypedArray array) {
    boolean iconVisible = array.getBoolean(R.styleable.TookaCustomTextView_customTextViewIconVisible, false);
    boolean subtitleVisible = array.getBoolean(R.styleable.TookaCustomTextView_customTextViewSubtitleVisible, false);

    image.setVisibility(iconVisible ? VISIBLE : GONE);
    subtitle.setVisibility(subtitleVisible ? VISIBLE : GONE);

    image.setImageResource(array.getResourceId(R.styleable.TookaCustomTextView_customTextViewIcon,
      R.drawable.ic_broken_image));

    title.setText(
      array.getText(R.styleable.TookaCustomTextView_customTextViewTitleText)
    );
    title.setTextAppearance(
      array.getResourceId(R.styleable.TookaCustomTextView_customTextViewTitleTextAppearance, R.style.TextAppearance_MyTheme_Headline6)
    );

    subtitle.setText(
      array.getText(R.styleable.TookaCustomTextView_customTextViewSubtitleText)
    );
    subtitle.setTextAppearance(
      array.getResourceId(R.styleable.TookaCustomTextView_customTextViewSubtitleTextAppearance, R.style.TextAppearance_MyTheme_Price1)
    );
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  public void setTitle(@StringRes int title) {
    this.title.setText(title);
  }

  public void setSubtitle(String subtitle) {
    this.subtitle.setText(subtitle);
  }

  public void setSubtitle(@StringRes int subtitle) {
    this.subtitle.setText(subtitle);
  }

  public void setImage(String url) {
    if (url.length() > 0)
      Glide.with(this.getContext())
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(image);
    else image.setVisibility(View.GONE);
  }

  public void setImage(@DrawableRes int url) {
    if (url == 0)
      Glide.with(this.getContext())
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(image);
    else image.setVisibility(View.GONE);
  }


}
