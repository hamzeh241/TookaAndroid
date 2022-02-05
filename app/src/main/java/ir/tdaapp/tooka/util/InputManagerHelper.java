package ir.tdaapp.tooka.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class InputManagerHelper {

  public static InputMethodManager getManager(Context context){
    return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
  }
}
