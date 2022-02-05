package ir.tdaapp.tooka

import android.os.CountDownTimer
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.Visibility
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.navigation.NavigationBarView
import ir.tdaapp.tooka.databinding.ActivityMainBinding
import ir.tdaapp.tooka.util.signalr.SignalR
import ir.tdaapp.tooka.viewmodels.MainActivityViewModel
import ir.tdaapp.tooka.viewmodels.SharedViewModel
import ir.tdaapp.tooka.views.activities.base.BaseActivity
import org.koin.android.ext.android.inject

class MainActivity: BaseActivity(), NavigationBarView.OnItemSelectedListener,
  NavigationBarView.OnItemReselectedListener, SignalR.OnSignalRCallback {

  companion object {
    private const val SECOND_10: Long = 10000
    private const val SECOND_1: Long = 1000
  }

  private val TAG = "MainActivity"

  private lateinit var binding: ActivityMainBinding

  private val viewModel: MainActivityViewModel by inject()
  private val sharedViewModel: SharedViewModel by inject()

  /*Set kardane Visibility 'BottomNavigationBar'*/
  var bottomNavVisibility: Boolean = true
    get() {
      return when (binding.bottomNav.visibility) {
        View.VISIBLE -> true
        else -> false
      }
    }
    set(value) {
      binding.bottomNav.visibility = if (value) View.VISIBLE else View.GONE
      field = value
    }

  /*Namaieshe Toaste dast saz Tooka*/
  var toastVisibility: Boolean
    get() = when (binding.cardToast.visibility) {
      View.VISIBLE -> true
      else -> false
    }
    set(value) = when (value) {
      true -> {
        TransitionManager.beginDelayedTransition(binding.root, Slide(Gravity.START))
        binding.cardToast.visibility = View.VISIBLE
      }
      false -> {
        TransitionManager.beginDelayedTransition(binding.root, Slide(Gravity.START))
        binding.cardToast.visibility = View.GONE
      }
    }

  /**
   * Gereftan 'ViewBinding' mokhtas be 'MainActivity'.
   * Baraie darke me'mari estefade shode dar app, BaseActivity ra bebinid
   * @see BaseActivity
   */
  override fun getLayout(): ViewBinding {
    binding = ActivityMainBinding.inflate(layoutInflater)
    return binding
  }

  /**
   * Avalin methodi ke bad az 'onCreate' va 'getLayout' farakhani mishavad.
   * Baraie darke me'mari estefade shode dar app, BaseActivity ra bebinid
   * @see BaseActivity
   */
  override fun init() {
    val navController = findNavController(R.id.fragmentContainerView)

    binding.bottomNav.setOnItemReselectedListener(this)
    binding.bottomNav.setupWithNavController(navController)
  }

  /**
   * Check mikonad ke aya 'GooglePlayServices' dar dastres ast
   */
//  private fun checkGooglePlayServices(): Boolean {
//    val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
//    return if (status != ConnectionResult.SUCCESS) {
//      Log.e(TAG, "Error")
//      false
//    } else {
//      Log.i(TAG, "Google play services updated")
//      true
//    }
//  }

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

  /**
   * Dar inja har Animation ya Transitioni ke baiad dar shoru'e app ejra shavad inja
   * neveshte mishavad
   * @see TransitionManager
   * @see Transition
   * @see Animation
   */
  override fun initTransitions() = Unit

  /**
   * Dar inja har amaliati ke bayad rui Toolbar anjam shavad neveshte mishavad
   * @see Toolbar
   */
  override fun initToolbar() = Unit

  override fun initLanguage() = Unit

  override fun initTheme() {
    preferenceRepository!!.nightModeLive.observe(this) { nightMode ->
      nightMode?.let {
        delegate.localNightMode = it
      }
    }
  }

  override fun onNavigationItemSelected(p0: MenuItem): Boolean {
    return false
  }

  override fun onNavigationItemReselected(p0: MenuItem) {
  }

  override fun onConnecting() {
  }

  override fun onConnected() {
  }

  /**
   * Agar connection beine Server va Client dar SignalR ghat shavad in method farakhani mishavad
   * @see SignalR
   */
  override fun onDisconnected() {
    sharedViewModel.sendError("Error")
  }
}