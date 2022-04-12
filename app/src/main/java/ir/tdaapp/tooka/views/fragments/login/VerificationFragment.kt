package ir.tdaapp.tooka.views.fragments.login

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.components.TookaSnackBar
import ir.tdaapp.tooka.databinding.FragmentVerificationBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.util.UserErrors
import ir.tdaapp.tooka.util.toPx
import ir.tdaapp.tooka.viewmodels.LoginActivityViewModel
import ir.tdaapp.tooka.views.activities.LoginActivity
import ir.tdaapp.tooka.views.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class VerificationFragment: BaseFragmentSecond(), CoroutineScope {

  private lateinit var binding: FragmentVerificationBinding

  private val activityViewModel: LoginActivityViewModel by inject()

  private lateinit var progressDrawable: Drawable
  private var submitLoading: Boolean = false
    set(value) {
      if (submitLoading == value) return
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
  private var resendLoading: Boolean = false
    set(value) {
      if (resendLoading == value) return
      field = value

      binding.txtResend.apply {
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
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
  }

  override fun initListeners(view: View) {
    binding.submit.setOnClickListener {
      launch {
        activityViewModel.verify(
          activityViewModel.phoneNumber.value!!,
          binding.edtCode.text.toString().toInt()
        )
      }
    }

    binding.resend.setOnClickListener {
      resendLoading = true
      launch {
        activityViewModel.resendSms(activityViewModel.phoneNumber.value!!)
      }
    }
  }

  override fun initObservables() {
    activityViewModel.verificationCode.observe(viewLifecycleOwner) {
      submitLoading = true
      binding.edtCode.setText(it.toString())
      binding.edtCode.isEnabled = false

      launch {
        activityViewModel.verify(
          activityViewModel.phoneNumber.value!!,
          binding.edtCode.text.toString().toInt()
        )
      }
    }

    activityViewModel.resendResponse.observe(viewLifecycleOwner) {
      resendLoading = false

      TookaSnackBar(
        binding.root,
        getString(R.string.code_was_resent),
        Snackbar.LENGTH_SHORT
      ).textConfig {
        it.typeface = Typeface.createFromAsset(
          requireActivity().assets,
          "iranyekan_medium.ttf"
        )
      }.backgroundConfig {
        it.setBackgroundResource(R.drawable.ok_snackbar_background)
      }.show()
    }

    activityViewModel.verification.observe(viewLifecycleOwner) {
      launch(Dispatchers.IO) {
        (requireActivity() as LoginActivity).userPrefs.add(requireContext(), it.id)
        withContext(Dispatchers.Main) {
          submitLoading = true
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

          requireActivity().finish()
        }
      }
    }
  }

  override fun initErrors() = activityViewModel.error.observe(viewLifecycleOwner) {
    val message: String
    @DrawableRes val imageRes: Int = R.drawable.ic_sentiment_dissatisfied
    when (it) {
      UserErrors.DATABASE_EXCEPTION -> message = getString(R.string.user_database_error)
      UserErrors.SMS_EXCEPTION -> message = getString(R.string.sms_not_sent)
      UserErrors.INVALID_ARGS_EXCEPTION -> message = getString(R.string.unknown_error_desc)
      UserErrors.NETWORK_EXCEPTION -> message = getString(R.string.network_error_desc)
      UserErrors.INVALID_CODE -> message = getString(R.string.invalid_verification_code)
      UserErrors.UNKNOWN_EXCEPTION -> message = getString(R.string.unknown_error_desc)
      UserErrors.NOT_FOUND -> message = getString(R.string.user_database_error)
      UserErrors.CODE_TOO_EARLY_TO_SEND -> message = getString(R.string.code_too_early_to_send)
    }

    val toastBinding = ToastLayoutBinding.inflate(layoutInflater).apply {
      this.message.text = message
      image.setImageResource(imageRes)
    }

    customToast(toastBinding.root)
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentVerificationBinding.inflate(inflater, container, false)
    return binding
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}