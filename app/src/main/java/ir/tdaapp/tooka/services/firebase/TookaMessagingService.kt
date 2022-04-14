package ir.tdaapp.tooka.services.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.tdaapp.tooka.application.App
import java.util.*

class TookaMessagingService: FirebaseMessagingService() {

  companion object {
    const val TAG = "TookaMessagingService"
  }

  /**
   * Tokeni ke firebase generate mikonad inja bargasht dade mishavad
   * @param token Tokene firebase
   */
  override fun onNewToken(token: String) {
    super.onNewToken(token)

    (application as App).tokenPreferences.add(applicationContext, token)
  }

  /**
   * Hengame dariaft notification az firebase in method farakhani mishavad
   * @param message Etelaate notification
   */
  override fun onMessageReceived(message: RemoteMessage) {
    if (message.notification == null)
      return

    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(this, "price_alerts")
      .setContentTitle(message.notification!!.title)
      .setContentText(message.notification!!.body)
      .setSmallIcon(android.R.drawable.ic_dialog_alert)
      .setStyle(NotificationCompat.BigTextStyle())
      .build()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
        NotificationChannel("price_alerts", "Price Alerts", NotificationManager.IMPORTANCE_HIGH)
      manager.createNotificationChannel(channel)
    }

    manager.notify(Random().nextInt(), notification)

    super.onMessageReceived(message)
  }
}