package ir.tdaapp.tooka.models.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import ir.tdaapp.tooka.R;

public class TookaToggleButton extends CardView implements View.OnClickListener {

  LinearLayout firstRoot, secondRoot;
  TextView firstChoice, secondChoice;
  ImageView imgFirstChoice, imgSecondChoice;

  boolean isSecondSelected = false;

  private ToggleCallback callback;

  public void setCallback(ToggleCallback callback) {
    this.callback = callback;
  }

  public interface ToggleCallback {
    void onSelected(int position);
  }

  public TookaToggleButton(@NonNull Context context) {
    super(context);
    initialize(context);
  }

  public TookaToggleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context);
  }

  public TookaToggleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context);
  }

  private void initialize(Context context) {
    View view = LayoutInflater.from(context).inflate(R.layout.component_tooka_toggle_button, this, true);

    firstChoice = view.findViewById(R.id.txtFirst);
    secondChoice = view.findViewById(R.id.txtSecond);
    imgFirstChoice = view.findViewById(R.id.imgFirst);
    imgSecondChoice = view.findViewById(R.id.imgSecond);
    firstRoot = view.findViewById(R.id.firstChoice);
    secondRoot = view.findViewById(R.id.secondChoice);

    firstRoot.setOnClickListener(this);
    secondRoot.setOnClickListener(this);
    firstRoot.performClick();
  }

  public void setFirstChoiceText(String text) {
    firstChoice.setText(text);
  }

  public void setSecondChoiceText(String text) {
    secondChoice.setText(text);
  }

  public void setFirstChoiceIcon(@DrawableRes int drawable) {
    if (drawable != 0)
      Glide.with(this.getContext())
        .load(drawable)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(imgFirstChoice);
    else imgFirstChoice.setVisibility(GONE);
  }

  public void setFirstChoiceIcon(String url) {
    if (url.length() > 0)
      Glide.with(this.getContext())
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(imgFirstChoice);
    else imgFirstChoice.setVisibility(GONE);
  }

  public void setSecondChoiceIcon(@DrawableRes int drawable) {
    if (drawable != 0)
      Glide.with(this.getContext())
        .load(drawable)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(imgSecondChoice);
    else imgSecondChoice.setVisibility(GONE);
  }

  public void setSecondChoiceIcon(String url) {
    if (url.length() > 0)
      Glide.with(this.getContext())
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_broken_image)
        .into(imgSecondChoice);
    else imgSecondChoice.setVisibility(GONE);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.firstChoice:
        selectPosition(1);
        break;
      case R.id.secondChoice:
        selectPosition(2);
        break;
    }
  }

  private void selectPosition(int pos) {
    if (pos == 1) {
      firstChoice.setAlpha(1f);
      imgFirstChoice.setAlpha(1f);
      secondChoice.setAlpha(0.3f);
      imgSecondChoice.setAlpha(0.3f);
      isSecondSelected = false;
      if (callback != null)
        callback.onSelected(1);
    } else {
      firstChoice.setAlpha(0.3f);
      imgFirstChoice.setAlpha(0.3f);
      secondChoice.setAlpha(1f);
      imgSecondChoice.setAlpha(1f);
      isSecondSelected = true;
      if (callback != null)
        callback.onSelected(2);
    }
  }

  public int getSelectedPosition() {
    return isSecondSelected ? 1 : 2;
  }
}
