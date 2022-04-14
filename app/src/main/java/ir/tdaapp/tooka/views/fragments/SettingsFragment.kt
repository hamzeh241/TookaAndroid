package ir.tdaapp.tooka.views.fragments

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.application.App
import ir.tdaapp.tooka.components.TookaSnackBar
import ir.tdaapp.tooka.databinding.FragmentSettingsBinding
import ir.tdaapp.tooka.util.preference.LanguagePreferences
import ir.tdaapp.tooka.util.toast
import ir.tdaapp.tooka.views.dialogs.AppLanguageBottomSheetDialog
import ir.tdaapp.tooka.views.dialogs.AppLanguageBottomSheetDialog.Language.ENGLISH
import ir.tdaapp.tooka.views.dialogs.AppLanguageBottomSheetDialog.Language.PERSIAN
import ir.tdaapp.tooka.views.dialogs.AppThemeBottomSheetDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

class SettingsFragment: BaseFragment(), View.OnClickListener,
  AppThemeBottomSheetDialog.AppThemeBottomSheetCallback {

  private lateinit var binding: FragmentSettingsBinding
  private lateinit var langPrefs: LanguagePreferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View =
    if (this::binding.isInitialized)
      binding.root
    else {
      binding = FragmentSettingsBinding.inflate(inflater, container, false)
      binding.root
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.toolbar.title = getString(R.string.app_name_translatable)

    langPrefs = LanguagePreferences()
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
    binding.includeAppSettings.vgChangeLang.setOnClickListener(this)
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
          toast(requireContext(), "user is signed in")
        } else {
          findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToLoginActivity()
          )
        }
      }
      R.id.vg_change_lang -> {
        val dialog = AppLanguageBottomSheetDialog {
          when (it) {
            ENGLISH -> {
              langPrefs.add(requireContext(), "en")
            }

            PERSIAN -> {
              langPrefs.add(requireContext(), "fa")
            }
          }

          val typedValue = TypedValue()
          val theme: Resources.Theme = binding.root.context.theme
          theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
          @ColorInt val colorOnSurface = typedValue.data
          TookaSnackBar(
            binding.root,
            getString(R.string.lang_change_message),
            Snackbar.LENGTH_LONG
          ).textConfig { textView ->
            textView.typeface = Typeface.createFromAsset(
              requireActivity().assets,
              "iranyekan_medium.ttf"
            )
//            textView.setTextColor(colorOnSurface)
          }.show()
        }
        dialog.show(requireActivity().supportFragmentManager, AppLanguageBottomSheetDialog.TAG)

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