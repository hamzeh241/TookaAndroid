package ir.tdaapp.tooka.views.activities

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ActivityLoginBinding
import ir.tdaapp.tooka.util.TookaSmsRetriever
import ir.tdaapp.tooka.util.UserPreferences
import ir.tdaapp.tooka.viewmodels.LoginActivityViewModel
import ir.tdaapp.tooka.views.activities.base.BaseActivity
import org.koin.android.ext.android.inject

class LoginActivity: BaseActivity() {

  private lateinit var binding: ActivityLoginBinding

  private lateinit var intentFilter: IntentFilter
  private lateinit var bReceiver: TookaSmsRetriever

  lateinit var userPrefs: UserPreferences

  private val viewModel by inject<LoginActivityViewModel>()

  override fun init() {
    intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    bReceiver = TookaSmsRetriever()

    userPrefs = UserPreferences()

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

  override fun initTransitions() {
  }

  override fun initToolbar() {
  }

  override fun initLanguage() {
  }

  override fun initTheme() {
  }

  override fun getLayout(): ViewBinding {
    binding = ActivityLoginBinding.inflate(layoutInflater)
    return binding
  }
}