package ir.tdaapp.tooka.models.util

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController


fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions? = getNavOptions()) {
  findNavController().navigate(directions, navOptions)
}

fun getNavOptions(): NavOptions {
  return NavOptions.Builder()
    .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
    .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
    .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
    .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
    .build()
}