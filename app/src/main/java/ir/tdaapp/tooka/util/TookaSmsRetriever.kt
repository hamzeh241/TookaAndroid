package ir.tdaapp.tooka.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class TookaSmsRetriever: BroadcastReceiver() {

  fun interface OnSmsReceived {
    fun onReceived(code: Int)
  }

  var listener: OnSmsReceived? = null
    get() = field
    set(value) {
      field = value
    }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent?.getAction())) {
      val extras = intent?.getExtras();
      val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?

      when (status!!.statusCode) {
        CommonStatusCodes.SUCCESS -> {
          // Get SMS message contents
          val message: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

          val split = message.split("\n")
          val code = split[0].replace(Regex("[^0-9]"), "")

          listener?.onReceived(code.toInt())
          // Extract one-time code from the message and complete verification
          // by sending the code back to your server.
        }
        CommonStatusCodes.TIMEOUT -> {

          // Waiting for SMS timed out (5 minutes)
          // Handle the error ...
        }
      }
    }
  }
}