package ir.tdaapp.tooka.views.activities

import android.content.Intent
import android.view.View
import androidx.viewbinding.ViewBinding
import com.microsoft.signalr.HubConnection
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.databinding.ActivitySplashBinding
import ir.tdaapp.tooka.util.signalr.SignalR
import ir.tdaapp.tooka.views.activities.base.BaseActivity

class SplashActivity: BaseActivity(),SignalR.OnSignalRCallback {

  lateinit var binding: ActivitySplashBinding
  lateinit var connection: HubConnection

  override fun init() {
    connection = SignalR.hubConnection
    SignalR.connect(this)

    binding.button.setOnClickListener {
      SignalR.connect(this)
      binding.button.visibility = View.GONE
    }
  }

  override fun initTransitions() = Unit

  override fun initToolbar() = Unit

  override fun initLanguage() = Unit

  override fun initTheme() = Unit

  override fun getLayout(): ViewBinding {
    binding = ActivitySplashBinding.inflate(layoutInflater)
    return binding
  }

  override fun onConnecting() {
    runOnUiThread {
      binding.button.visibility = View.GONE
      binding.txtSignalRLog.text = "connecting"
    }
  }

  override fun onConnected() {
    runOnUiThread {
      this@SplashActivity.startActivity(
        Intent(
          this@SplashActivity,
          MainActivity::class.java
        )
      )
      this@SplashActivity.finish()
    }
  }

  override fun onDisconnected() {
    runOnUiThread {
      binding.button.visibility = View.VISIBLE
      binding.txtSignalRLog.text = "disconnected"
    }
  }
}