package ir.tdaapp.tooka.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.application.App
import ir.tdaapp.tooka.databinding.FragmentSettingsBinding
import ir.tdaapp.tooka.util.toast
import ir.tdaapp.tooka.views.dialogs.AppThemeBottomSheetDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

class SettingsFragment: BaseFragment(), View.OnClickListener,
  AppThemeBottomSheetDialog.AppThemeBottomSheetCallback {

  private lateinit var binding: FragmentSettingsBinding

  private lateinit var mGoogleSignInClient: GoogleSignInClient

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View =
    if (this::binding.isInitialized)
      binding.root
    else{
      binding = FragmentSettingsBinding.inflate(inflater,container,false)
      binding.root
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.toolbar.title = getString(R.string.app_name_translatable)
    initListeners()
    initObservables()
    if ((requireActivity() as MainActivity).userPrefs.hasAccount(requireContext())) {
      // user is signed in
    } else {
      // user is not signed in
      binding.includeUserSettings.txtUserSettings.text = getString(R.string.login_or_signup)
    }
  }


  private fun initListeners() {
    binding.includeAppSettings.vgAppTheme.setOnClickListener(this)
    binding.includeUserSettings.cardUserSettings.setOnClickListener(this)
  }

  private fun initObservables() {
    App.preferenceHelper!!.isDarkThemeLive.observe(viewLifecycleOwner) { isDarkTheme ->
      isDarkTheme?.let {
        isNightMode(it)
      }
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.vg_app_theme -> {
        val dialog = AppThemeBottomSheetDialog()
        dialog.callback = this
        dialog.show(requireActivity().supportFragmentManager, AppThemeBottomSheetDialog.TAG)
      }
      R.id.cardUserSettings -> {
        if ((requireActivity() as MainActivity).userPrefs.hasAccount(requireContext())) {
          toast(requireContext(),"user is signed in")
        } else {
          findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToLoginActivity()
          )
        }
      }
    }
  }

  override fun onLightModeClicked() {
    App.preferenceHelper!!.isDarkTheme = false
  }

  override fun onDarkModeClicked() {
    App.preferenceHelper!!.isDarkTheme = true
  }

  private fun isNightMode(boolean: Boolean) {
    if (boolean) {
      binding.includeAppSettings.imgTheme.setImageResource(R.drawable.ic_nightmode);
      binding.includeAppSettings.txtTheme.text = getString(R.string.dark)
    } else {
      binding.includeAppSettings.imgTheme.setImageResource(R.drawable.ic_lightmode);
      binding.includeAppSettings.txtTheme.text = getString(R.string.light)
    }
  }
}