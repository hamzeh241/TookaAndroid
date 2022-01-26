package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentSettingsBinding
import ir.tdaapp.tooka.views.dialogs.AppThemeBottomSheetDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

class SettingsFragment: BaseFragment(), View.OnClickListener,
  AppThemeBottomSheetDialog.AppThemeBottomSheetCallback {

  lateinit var binding: FragmentSettingsBinding

  override fun init() {
    (requireActivity() as MainActivity).preferenceRepository!!.isDarkThemeLive.observe(activity) { isDarkTheme ->
      isDarkTheme?.let {
        isNightMode(it)
      }
    }
  }

  override fun initTransitions() {

  }

  override fun initToolbar() {
    binding.toolbar.title = getString(R.string.app_name_translatable)
  }

  override fun initListeners(view: View) {
    binding.includeAppSettings.vgAppTheme.setOnClickListener(this)
  }

  override fun initObservables() {
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentSettingsBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.vg_app_theme -> {
        val dialog = AppThemeBottomSheetDialog()
        dialog.callback = this
        dialog.show(requireActivity().supportFragmentManager, AppThemeBottomSheetDialog.TAG)
      }
    }
  }

  override fun onLightModeClicked() {
    (requireActivity() as MainActivity).preferenceRepository!!.isDarkTheme = false
  }

  override fun onDarkModeClicked() {
    (requireActivity() as MainActivity).preferenceRepository!!.isDarkTheme = true
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