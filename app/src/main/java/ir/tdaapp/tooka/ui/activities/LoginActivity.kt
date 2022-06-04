package ir.tdaapp.tooka.ui.activities

import android.content.IntentFilter
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import ir.tdaapp.tooka.databinding.ActivityLoginBinding
import ir.tdaapp.tooka.models.preference.UserPreferences
import ir.tdaapp.tooka.models.util.TookaSmsRetriever
import ir.tdaapp.tooka.models.viewmodels.LoginActivityViewModel
import ir.tdaapp.tooka.ui.activities.base.BaseActivity
import org.koin.android.ext.android.inject

class LoginActivity: BaseActivity() {

  private lateinit var binding: ActivityLoginBinding

  private lateinit var intentFilter: IntentFilter
  private lateinit var bReceiver: TookaSmsRetriever

  lateinit var userPrefs: UserPreferences

  private val viewModel by inject<LoginActivityViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    bReceiver = TookaSmsRetriever()

    userPrefs = UserPreferences(this)

    bReceiver.listener = TookaSmsRetriever.OnSmsReceived {
      viewModel.recieveVerificationCode(it)
    }
  }

  override fun onResume() {
    super.onResume()
    registerReceiver(bReceiver, intentFilter)
  }

  override fun onPause() {
    super.onPause()
    unregisterReceiver(bReceiver)
  }
}