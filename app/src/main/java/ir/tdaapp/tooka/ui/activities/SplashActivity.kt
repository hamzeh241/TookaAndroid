package ir.tdaapp.tooka.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.microsoft.signalr.HubConnection
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.databinding.ActivitySplashBinding
import ir.tdaapp.tooka.models.util.signalr.SignalR
import ir.tdaapp.tooka.ui.activities.base.BaseActivity

class SplashActivity: BaseActivity(), SignalR.OnSignalRCallback {

  lateinit var binding: ActivitySplashBinding
  lateinit var connection: HubConnection

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivitySplashBinding.inflate(layoutInflater)
    setContentView(binding.root)

//    (application as App).preferenceHelper.nightModeLive.observe(this) { nightMode ->
//      nightMode?.let {
//        delegate.localNightMode = it
//      }
//    }

//    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//    delegate.applyDayNight()

    connection = SignalR.hubConnection
    SignalR.connect(this)

    binding.button.setOnClickListener {
      SignalR.connect(this)
      binding.button.visibility = View.GONE
    }
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