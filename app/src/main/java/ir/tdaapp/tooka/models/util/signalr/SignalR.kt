package ir.tdaapp.tooka.models.util.signalr

import android.os.Handler
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.util.*

object SignalR {

  const val SIGNAL_R_URL = "https://www.razhashop.ir/signalr"

  /**
   * Ijade instance 'HubConnection'
   * @see HubConnection
   */
  val hubConnection: HubConnection by lazy {
    HubConnectionBuilder.create(SIGNAL_R_URL).build()
  }

  /**
   * Callbacke marbut be waziate connection
   */
  interface OnSignalRCallback {
    fun onConnecting()
    fun onConnected()
    fun onDisconnected()
  }

  /**
   * Shorou kardan proseie connect shodane SignalR
   * @param callback Callbacke marbut be waziate connection
   * @see OnSignalRCallback Interface waziate connection
   */
  fun connect(callback: OnSignalRCallback) {
    if (hubConnection.connectionState == HubConnectionState.CONNECTED){
      callback.onConnected()
      return
    }
    callback.onConnecting()
    hubConnection.start().subscribe({
      callback.onConnected()
      start(callback)
    }, {
      callback.onDisconnected()
    })

    hubConnection.onClosed {
      callback.onDisconnected()
      connect(callback)
    }
  }

  private const val SECONDS_3 = 3000L

  private val timer = Timer()
  private val handler = Handler()

  /**
   * Agar connectione beine Client va Server mojud bashad javabe 'true' pas midahad
   */
  private val isConnected: Boolean
    get() = hubConnection.connectionState == HubConnectionState.CONNECTED

  /**
   * Dar inja proseie check kardane connection tavasote yek Timer be surat Schedule shode shorou mishavad
   * @param callback Interface marbut be waziate connection
   * @see Timer
   * @see TimerTask
   * @see OnConnectionLost
   */
  fun start(callback: OnSignalRCallback) {
    timer.scheduleAtFixedRate(object: TimerTask() {
      override fun run() {
        callback.onDisconnected()
        if (hubConnection.connectionState == HubConnectionState.CONNECTED)
          hubConnection.send("ConnectionCheck", "Ping")

        hubConnection.on("Heartbeat", object: Action1<String> {
          override fun invoke(param1: String?) = Unit
        }, String::class.java)

        handler.postDelayed({
          if (!isConnected) {
            callback.onDisconnected()
            timer.cancel()
            timer.purge()
          }
        }, 2900)
      }
    }, 0, SECONDS_3)
  }
}