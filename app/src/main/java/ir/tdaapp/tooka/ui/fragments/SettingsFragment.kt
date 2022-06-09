package ir.tdaapp.tooka.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import io.github.achmadhafid.lottie_dialog.core.*
import io.github.achmadhafid.lottie_dialog.dismissLottieDialog
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.model.onClick
import ir.tdaapp.tooka.App
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentSettingsBinding
import ir.tdaapp.tooka.models.preference.LanguagePreferences
import ir.tdaapp.tooka.models.util.toast
import ir.tdaapp.tooka.ui.dialogs.AppLanguageBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.AppLanguageBottomSheetDialog.Language.ENGLISH
import ir.tdaapp.tooka.ui.dialogs.AppLanguageBottomSheetDialog.Language.PERSIAN
import ir.tdaapp.tooka.ui.dialogs.AppThemeBottomSheetDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingsFragment: BaseFragment(), View.OnClickListener,
  AppThemeBottomSheetDialog.AppThemeBottomSheetCallback, CoroutineScope {

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

  override fun onResume() {
    super.onResume()
    if ((requireActivity() as MainActivity).userPrefs.hasAccount()) {
      // user is signed in
      binding.includeUserSettings.txtUserSettings.text = getString(R.string.user_settings)
    } else {
      // user is not signed in
      binding.includeUserSettings.txtUserSettings.text = getString(R.string.login_or_signup)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.toolbar.title = getString(R.string.app_name_translatable)

    langPrefs = LanguagePreferences(requireContext())
    initListeners()
    initObservables()
    if ((requireActivity() as MainActivity).userPrefs.hasAccount()) {
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
//    (requireActivity().application as App).preferenceHelper.isDarkThemeLive.observe(
//      viewLifecycleOwner
//    ) { isDarkTheme ->
//      isDarkTheme?.let {
//        isNightMode(it)
//      }
//    }
    isNightMode((requireActivity().application as App).themePreference.isNightMode())
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.vg_app_theme -> {
        val dialog = AppThemeBottomSheetDialog()
        dialog.callback = this
        dialog.show(requireActivity().supportFragmentManager, AppThemeBottomSheetDialog.TAG)
      }
      R.id.cardUserSettings -> {
        if ((requireActivity() as MainActivity).userPrefs.hasAccount()) {
          toast(requireContext(), "user is signed in")
        } else {
          findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToLoginActivity()
          )
        }
      }
      R.id.vg_change_lang -> {
        val en: (Dialog)->Unit = {
          launch(Dispatchers.Main) {
            (requireActivity().application as App).langPreferences.add("en")
            delay(300)
            requireActivity().finishAffinity()
            System.exit(0)
          }
        }
        val fa: (Dialog)->Unit = {
          launch(Dispatchers.Main) {
            (requireActivity().application as App).langPreferences.add("fa")
            delay(300)
            requireActivity().finishAffinity()
            System.exit(0)
          }
        }
        val dialog = AppLanguageBottomSheetDialog {
          var onClick: (Dialog)->Unit = {}
          when (it) {
            ENGLISH -> {
              onClick = en
            }

            PERSIAN -> {
              onClick = fa
            }
          }

          lottieConfirmationDialog {
            type = LottieDialogType.DIALOG
            withTitle {
              textRes = R.string.are_you_sure
              styleRes = R.style.TextAppearance_MyTheme_Body1
            }
            withAnimation {
              fileRes = R.raw.btc_eth
            }
            withContent {
              textRes = R.string.lang_change_restart
              styleRes = R.style.TextAppearance_MyTheme_Body2
            }
            withPositiveButton {
              textRes = R.string.yes
              iconRes = R.drawable.ic_baseline_check_24
              onClick(onClick)
            }
            withNegativeButton {
              textRes = R.string.no
              iconRes = R.drawable.ic_baseline_close_24
              onClick {
                dismissLottieDialog()
              }
            }
          }
        }
        dialog.show(requireActivity().supportFragmentManager, AppLanguageBottomSheetDialog.TAG)
      }
    }
  }

  override fun onLightModeClicked() {
//    (requireActivity().application as App).preferenceHelper.isDarkTheme = false
//    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//    (requireActivity() as MainActivity).delegate.applyDayNight()
    lottieConfirmationDialog {
      type = LottieDialogType.DIALOG
      withTitle {
        textRes = R.string.are_you_sure
        styleRes = R.style.TextAppearance_MyTheme_Body1
      }
      withAnimation {
        fileRes = R.raw.btc_eth
      }
      withContent {
        textRes = R.string.theme_change_content
        styleRes = R.style.TextAppearance_MyTheme_Body2
      }
      withPositiveButton {
        textRes = R.string.yes
        iconRes = R.drawable.ic_baseline_check_24
        onClick {
          launch(Dispatchers.Main) {
            (requireActivity().application as App).themePreference.add(false)
            delay(300)
            requireActivity().finishAffinity()
            System.exit(0)
          }
        }
      }
      withNegativeButton {
        textRes = R.string.no
        iconRes = R.drawable.ic_baseline_close_24
        onClick {
          dismissLottieDialog()
        }
      }
    }
  }

  override fun onDarkModeClicked() {
//    (requireActivity().application as App).preferenceHelper.isDarkTheme = true

    lottieConfirmationDialog {
      type = LottieDialogType.DIALOG
      withTitle {
        textRes = R.string.are_you_sure
        styleRes = R.style.TextAppearance_MyTheme_Body1
      }
      withAnimation {
        fileRes = R.raw.btc_eth
      }
      withContent {
        textRes = R.string.theme_change_content
        styleRes = R.style.TextAppearance_MyTheme_Body2
      }
      withPositiveButton {
        textRes = R.string.yes
        iconRes = R.drawable.ic_baseline_check_24
        onClick {
          launch(Dispatchers.Main) {
            (requireActivity().application as App).themePreference.add(true)
            delay(300)
            requireActivity().finishAffinity()
            System.exit(0)
          }
        }
      }
      withNegativeButton {
        textRes = R.string.no
        iconRes = R.drawable.ic_baseline_close_24
        onClick {
          dismissLottieDialog()
        }
      }
    }
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

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}