package ir.tdaapp.tooka.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.components.TookaSnackBar
import ir.tdaapp.tooka.databinding.FragmentLoginBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.UserErrors.*
import ir.tdaapp.tooka.models.util.toPx
import ir.tdaapp.tooka.models.viewmodels.LoginActivityViewModel
import ir.tdaapp.tooka.ui.dialogs.ConfirmationDialog
import ir.tdaapp.tooka.ui.dialogs.ConfirmationDialog.ConfirmationChoice.*
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.lang.Exception
import java.lang.StringBuilder
import kotlin.coroutines.CoroutineContext

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class LoginFragment: BaseFragmentSecond(), View.OnClickListener, CoroutineScope {

  companion object {
    const val GOOGLE_SIGNIN_CODE = 53584
    const val RESOLVE_HINT = 2254
  }

  private val activityViewModel: LoginActivityViewModel by inject()

  private lateinit var binding: FragmentLoginBinding

  private lateinit var mGoogleSignInClient: GoogleSignInClient

  private var isNumberSubmitted = false

  private lateinit var progressDrawable: Drawable
  private var isLoading: Boolean = false
    set(value) {
      if (isLoading == value) return
      field = value

      binding.txtSubmit.apply {
        compoundDrawablePadding = 8
        val (_, topDrawable, endDrawable, bottomDrawable) = compoundDrawablesRelative
        if (value) {
          // add progress and keep others
          setCompoundDrawablesRelative(
            progressDrawable,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.start()
        } else {
          // remove progress
          setCompoundDrawablesRelative(
            null,
            topDrawable,
            endDrawable,
            bottomDrawable
          )
          (progressDrawable as? Animatable)?.stop()
        }
      }
    }

  override fun init() {
    progressDrawable = ProgressBar(context).indeterminateDrawable.apply {
      // apply any customization on drawable. not on progress view
      setBounds(0, 0, 24.toPx, 24.toPx)
      setTint(Color.WHITE)
    }

    lifecycleScope.launchWhenCreated {
      requestUserPhoneNumber()
    }
  }

  private fun requestUserPhoneNumber() {
    val hintRequest = HintRequest.Builder()
      .setPhoneNumberIdentifierSupported(true)
      .build()
    val intent = Credentials.getClient(requireActivity()).getHintPickerIntent(hintRequest)
    startIntentSenderForResult(
      intent.intentSender,
      RESOLVE_HINT,
      null,
      0,
      0,
      0,
      null
    )
  }

  override fun initTransitions() = Unit

  override fun initToolbar() = Unit

  override fun initListeners(view: View) {
    binding.submit.setOnClickListener(this)
    binding.googleSubmit.setOnClickListener(this)
  }

  override fun initObservables() {
    activityViewModel.loginResponse.observe(viewLifecycleOwner) {
      isNumberSubmitted = true
      isLoading = false

      val client = SmsRetriever.getClient(requireActivity())
      client.startSmsRetriever()

      findNavController().navigate(R.id.action_loginFragment2_to_verificationFragment)
    }

    activityViewModel.googleLogin.observe(viewLifecycleOwner) {
      TookaSnackBar(
        binding.root,
        getString(R.string.login_success),
        Snackbar.LENGTH_SHORT
      ).textConfig {
        it.typeface = Typeface.createFromAsset(
          requireActivity().assets,
          "iranyekan_medium.ttf"
        )
      }.backgroundConfig {
        it.setBackgroundResource(R.drawable.ok_snackbar_background)
      }.show()

      requireActivity().onBackPressed()
    }
  }

  private fun startGoogleSignInFlow() {
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .build()

    mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), options);

    val signInIntent = mGoogleSignInClient.getSignInIntent()
    startActivityForResult(signInIntent, GOOGLE_SIGNIN_CODE)
  }

  private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
    try {
      val account = completedTask.getResult(ApiException::class.java)
      if (account.email != null) {

        val model = GoogleLoginModel(
          account.givenName,
          account.familyName,
          account.photoUrl.toString(),
          account.email!!
        )
        launch {
          activityViewModel.googleLogin(model)
        }
      }
      // Signed in successfully, show authenticated UI.
    } catch (e: Exception) {
      // The ApiException status code indicates the detailed failure reason.
      // Please refer to the GoogleSignInStatusCodes class reference for more information.
      TookaSnackBar(
        binding.root,
        getString(R.string.google_login_failed_unknown),
        Snackbar.LENGTH_LONG
      ).textConfig {
        it.typeface = Typeface.createFromAsset(
          requireActivity().assets,
          "iranyekan_medium.ttf"
        )
      }.backgroundConfig {
        it.setBackgroundResource(R.drawable.error_snackbar_background)
      }.show()
    }
  }

  override fun initErrors() = activityViewModel.error.observe(viewLifecycleOwner) {
    val message: String
    @DrawableRes val imageRes: Int = R.drawable.ic_sentiment_dissatisfied
    when (it) {
      DATABASE_EXCEPTION -> message = getString(R.string.user_database_error)
      SMS_EXCEPTION -> message = getString(R.string.sms_not_sent)
      INVALID_ARGS_EXCEPTION -> message = getString(R.string.unknown_error_desc)
      NETWORK_EXCEPTION -> message = getString(R.string.network_error_desc)
      INVALID_CODE -> message = getString(R.string.invalid_verification_code)
      UNKNOWN_EXCEPTION -> message = getString(R.string.unknown_error_desc)
      NOT_FOUND -> message = getString(R.string.user_database_error)
      CODE_TOO_EARLY_TO_SEND -> TODO()
    }

    val toastBinding = ToastLayoutBinding.inflate(layoutInflater).apply {
      this.message.text = message
      image.setImageResource(imageRes)
    }
    customToast(toastBinding.root)
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentLoginBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.submit -> {
        isLoading = true
        launch {
          activityViewModel.loginOrSignup(binding.edtPhone.text.toString())
          activityViewModel.setPhoneNumber(binding.edtPhone.text.toString())
          binding.edtPhone.isEnabled = false
        }
      }
      R.id.googleSubmit -> {
        startGoogleSignInFlow()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == RESOLVE_HINT) {
      when (resultCode) {
        Activity.RESULT_OK -> {
          val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)
          val correctPhone = credential?.id.let {
            it?.replace("+98", "0")
          }
          val dialog = ConfirmationDialog.Builder()
            .title(getString(R.string.do_you_confirm))
            .message(
              StringBuilder(getString(R.string.phone_number_confirm_1))
                .append(" ")
                .append(correctPhone)
                .append(" ")
                .append(getString(R.string.phone_number_confirm_2))
                .toString()
            )
            .positiveText(getString(R.string.user_confirms_phone))
            .negativeText(getString(R.string.user_doesnt_confirm_phone))
            .listener {
              when (it) {
                Positive -> {
                  binding.edtPhone.setText(correctPhone)
                  binding.edtPhone.isEnabled = false
                  launch {
                    activityViewModel.loginOrSignup(correctPhone!!)
                    activityViewModel.setPhoneNumber(binding.edtPhone.text.toString())
                  }
                }
                Negative -> {}
              }
            }.build()

          dialog.show(requireActivity().supportFragmentManager, ConfirmationDialog.TAG)

        }
        Activity.RESULT_CANCELED -> log("canceled")
        CredentialsApi.ACTIVITY_RESULT_OTHER_ACCOUNT -> log("other account")
        CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE -> log("no numbers")
      }
    }

    if (requestCode == GOOGLE_SIGNIN_CODE) {
      val task: Task<GoogleSignInAccount> =
        GoogleSignIn.getSignedInAccountFromIntent(data)
      handleSignInResult(task)
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("LoginFragmentJob")
}