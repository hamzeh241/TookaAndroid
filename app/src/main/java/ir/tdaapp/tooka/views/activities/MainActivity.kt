package ir.tdaapp.tooka

import android.os.Bundle
import android.os.CountDownTimer
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import ir.tdaapp.tooka.application.App
import ir.tdaapp.tooka.databinding.ActivityMainBinding
import ir.tdaapp.tooka.util.UserPreferences
import ir.tdaapp.tooka.util.signalr.SignalR
import ir.tdaapp.tooka.viewmodels.MainActivityViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import org.koin.android.ext.android.inject

class MainActivity: AppCompatActivity(), NavigationBarView.OnItemSelectedListener,
  NavigationBarView.OnItemReselectedListener, SignalR.OnSignalRCallback {

  companion object {
    private const val SECOND_10: Long = 10000
    private const val SECOND_1: Long = 1000
    private const val TAG = "MainActivity"
  }

  private lateinit var binding: ActivityMainBinding
  lateinit var userPrefs: UserPreferences
  private val viewModel: MainActivityViewModel by inject()
  private val sharedViewModel: SharedViewModel by inject()

  /*Set kardane Visibility 'BottomNavigationBar'*/
  var bottomNavVisibility: Boolean = true
    set(value) {
      binding.bottomNav.visibility = if (value) View.VISIBLE else View.GONE
      field = value
    }

  /*Namaieshe Toaste dast saz Tooka*/
  var toastVisibility: Boolean = false
    set(value) = when (value) {
      true -> {
        field = value
        TransitionManager.beginDelayedTransition(binding.root, Slide(Gravity.START))
        binding.cardToast.visibility = View.VISIBLE
      }
      false -> {
        field = value
        TransitionManager.beginDelayedTransition(binding.root, Slide(Gravity.START))
        binding.cardToast.visibility = View.GONE
      }
    }

  private lateinit var navController: NavController
  private lateinit var appBarConfiguration: AppBarConfiguration

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val navHostFragment = supportFragmentManager.findFragmentById(
      R.id.fragmentContainerView
    ) as NavHostFragment
    navController = navHostFragment.navController

    // Setup the bottom navigation view with navController
    binding.bottomNav.setupWithNavController(navController)

    // Setup the ActionBar with navController and 3 top level destinations
    appBarConfiguration = AppBarConfiguration(
      setOf(R.id.home, R.id.markets, R.id.portfolio, R.id.news, R.id.settings)
    )

    App.preferenceHelper!!.nightModeLive.observe(this) { nightMode ->
      nightMode?.let {
        delegate.localNightMode = it
      }
    }
    lifecycleScope.launchWhenCreated {
      viewModel.subscribeToLivePrice()
    }
    userPrefs = UserPreferences()

    binding.bottomNav.setOnItemReselectedListener(this)
  }

  /**
   * Callbacke marbut Toaste dast saz Tooka
   * @param v Viewi ke rui an click mishavad
   */
  interface OnToastCallback {
    fun callback(v: View)
    fun cancel(v: View)
  }

  fun infoToast(
    strings: Pair<String, String>,
    bools: Pair<Boolean, Boolean>,
    callback: OnToastCallback
  ) {
    val (visibility, isPersistent) = bools

    val (title, message) = strings
    if (binding.cardToast.visibility == View.GONE) {
      binding.txtToastTitle.text = title
      binding.txtToastDesc.text = message
      binding.toastSidebar.setBackgroundColor(getColor(R.color.blue_200))
      binding.btnToastRetry.visibility = if (visibility) View.VISIBLE else View.GONE

      binding.btnToastRetry.setOnClickListener {
        callback.callback(it)
      }

      binding.imgClearToast.setOnClickListener {
        toastVisibility = false
        callback.cancel(it)
      }

      toastVisibility = true
    } else {
      binding.cardToast.visibility = View.GONE
      infoToast(strings, bools, callback)
    }


    if (!isPersistent) {
      object: CountDownTimer(SECOND_10, SECOND_1) {
        override fun onTick(millisUntilFinished: Long) {
          /*Not Necessary*/
        }

        override fun onFinish() {
          toastVisibility = false
        }
      }.start()
    }
  }

  /**
   * Toaste dast saze Tooka ke yek error ra be User neshan midahad
   * @param title Onvane khata
   * @param message Tozihate khata
   * @param callback Callback baraie click kardane dokmeie 'Retry'
   */
  fun errorToast(pair: Pair<String, String>, callback: OnToastCallback) {
    val (title, message) = pair
    if (binding.cardToast.visibility == View.GONE) {
      binding.txtToastTitle.text = title
      binding.txtToastDesc.text = message
      binding.toastSidebar.setBackgroundColor(getColor(R.color.red_200))

      binding.btnToastRetry.setOnClickListener {
        callback.callback(it)
      }

      binding.imgClearToast.setOnClickListener {
        toastVisibility = false
        callback.cancel(it)
      }

      toastVisibility = true
    } else {
      binding.cardToast.visibility = View.GONE
      errorToast(pair, callback)
    }
    object: CountDownTimer(SECOND_10, SECOND_1) {
      override fun onTick(millisUntilFinished: Long) {
        /*Not Necessary*/
      }

      override fun onFinish() {
        toastVisibility = false
      }
    }.start()
  }

  /**
   * Toaste dast saze Tooka ke yek error ra be User neshan midahad
   * @param title Onvane khata
   * @param message Tozihate khata
   * @param buttonText Matne dokmeie 'Retry'
   * @param callback Callback baraie click kardane dokmeie 'Retry'
   */
  fun errorToast(pair: Pair<String, String>, buttonText: String, callback: OnToastCallback) {
    val (title, message) = pair
    if (binding.cardToast.visibility == View.GONE) {
      binding.txtToastTitle.text = title
      binding.txtToastDesc.text = message
      binding.btnToastRetry.text = buttonText
      binding.toastSidebar.setBackgroundColor(getColor(R.color.red_200))

      binding.btnToastRetry.setOnClickListener {
        callback.callback(it)
      }

      toastVisibility = true
    } else {
      binding.cardToast.visibility = View.GONE
      errorToast(pair, buttonText, callback)
    }
    object: CountDownTimer(SECOND_10, SECOND_1) {
      override fun onTick(millisUntilFinished: Long) {
        /*Not Necessary*/
      }

      override fun onFinish() {
        toastVisibility = false
      }
    }.start()
  }

  override fun onNavigationItemSelected(p0: MenuItem): Boolean {
    return false
  }

  override fun onNavigationItemReselected(p0: MenuItem) {
  }

  override fun onConnecting() {
  }

  override fun onConnected() {
    viewModel.subscribeToLivePrice()
  }

  /**
   * Agar connection beine Server va Client dar SignalR ghat shavad in method farakhani mishavad
   * @see SignalR
   */
  override fun onDisconnected() {
    sharedViewModel.sendError("Error")
  }
}