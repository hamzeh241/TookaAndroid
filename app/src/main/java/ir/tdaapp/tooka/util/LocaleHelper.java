package ir.tdaapp.tooka.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;

import java.util.Locale;

//public class LocaleHelper {
//  public static final String LAN_PERSIAN   = "fa";
//  public static final String LAN_ENGLISH      = "en";
//
//  private static Locale sLocale;
//
//  public static void setLocale(Locale locale) {
//    sLocale = locale;
//    if(sLocale != null) {
//      Locale.setDefault(sLocale);
//    }
//  }
//
//  public static void updateConfig(ContextThemeWrapper wrapper) {
//    if(sLocale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//      Configuration configuration = new Configuration();
//      configuration.setLocale(sLocale);
//      wrapper.applyOverrideConfiguration(configuration);
//    }
//  }
//
//  public static void updateConfig(Application app, Configuration configuration) {
//    if(sLocale != null) {
//      //Wrapping the configuration to avoid Activity endless loop
//      Configuration config = new Configuration(configuration);
//      config.locale = sLocale;
//      Resources res = app.getBaseContext().getResources();
//      res.updateConfiguration(config, res.getDisplayMetrics());
//    }
//  }
//}
public class LocaleHelper {
  public static Context localeUpdateResources(Context context, String languageCode) {
    Context newContext = context;

    Locale locale = new Locale(languageCode);
    Locale.setDefault(locale);

    Resources resources = context.getResources();
    Configuration config = new Configuration(resources.getConfiguration());

    config.setLocale(locale);
    newContext = context.createConfigurationContext(config);

    return newContext;
  }
}