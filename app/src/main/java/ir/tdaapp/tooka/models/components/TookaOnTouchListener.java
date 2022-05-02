package ir.tdaapp.tooka.models.components;

import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TookaOnTouchListener implements View.OnTouchListener {

  public TookaOnTouchListener(float initalX, float initialY) {
    lastX = initalX;
    lastY = initialY;
  }

  boolean isDragging = false;
  float lastX;
  float lastY;
  float deltaX;

  float screenWidth, screenHight;

  DisplayMetrics displaymetrics = new DisplayMetrics();


  float newX, newY, dX, dY;

  @Override
  public boolean onTouch(View arg0, MotionEvent arg1) {

//    screenHight = displaymetrics.heightPixels;
//    screenWidth = displaymetrics.widthPixels;

    int action = arg1.getAction();

    if (action == MotionEvent.ACTION_DOWN && !isDragging) {
      isDragging = true;
      deltaX = arg1.getX();
      return true;
    } else if (isDragging) {
      if (action == MotionEvent.ACTION_MOVE) {

//        newX = arg1.getRawX() + dX;
//        newY = arg1.getRawY() + dY;
//
//        // check if the view out of screen
//        if ((newX <= 0 || newX >= screenWidth - arg0.getWidth()) || (newY <= 0 || newY >= screenHight - arg0.getHeight())) {
//          TransitionManager.beginDelayedTransition((ViewGroup) arg0.getRootView(), new Slide(Gravity.START));
//          arg0.setVisibility(View.GONE);
//          return false;
//        }
        arg0.setX(arg0.getX() + arg1.getX() - deltaX);
        arg0.setY(arg0.getY());
        return true;
      } else if (action == MotionEvent.ACTION_UP) {
        isDragging = false;
        lastX = arg1.getX();
        lastY = arg1.getY();

        TransitionManager.beginDelayedTransition((ViewGroup) arg0.getRootView(), new Slide(Gravity.START));
        arg0.setVisibility(View.GONE);
        return true;
      } else if (action == MotionEvent.ACTION_CANCEL) {
        arg0.setX(lastX);
        arg0.setY(lastY);
        isDragging = false;
        return true;
      }
    }

    return false;
  }
}