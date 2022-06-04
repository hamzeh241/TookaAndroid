package ir.tdaapp.tooka.ui.fragments.base

import androidx.fragment.app.Fragment
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.models.preference.UserPreferences
import ir.tdaapp.tooka.ui.activities.LoginActivity

open class BaseFragment: Fragment() {

  companion object {
    const val TAG = "BaseFragment"
  }

  fun getUserId(): Int =
    when (requireActivity()) {
      is MainActivity -> (requireActivity() as MainActivity).userPrefs.getUserId()
      is LoginActivity -> (requireActivity() as LoginActivity).userPrefs.getUserId()
      else -> UserPreferences(requireActivity()).getUserId()
    }
}