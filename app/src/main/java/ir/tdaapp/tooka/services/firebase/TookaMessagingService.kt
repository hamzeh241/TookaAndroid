package ir.tdaapp.tooka.services.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class TookaMessagingService: FirebaseMessagingService() {

  companion object {
    const val TAG = "TookaMessagingService"
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)

    Log.i(TAG, "onNewToken: $token")
  }

  override fun onMessageReceived(message: RemoteMessage) {
    if (message.notification == null)
      return

    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val notification = NotificationCompat.Builder(this,"price_alerts")
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