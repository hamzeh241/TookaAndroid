package ir.tdaapp.tooka.views.fragments.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.uk.tastytoasty.TastyToasty
import ir.tdaapp.tooka.MainActivity

abstract class BaseFragmentSecond: Fragment() {

  var activity: MainActivity = MainActivity()
    get() {
      return requireActivity() as MainActivity
    }

  var viewBinding: ViewBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return getLayout(inflater, container).root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    init()
    initToolbar()
    initListeners(view)
    initTransitions()
    initObservables()
    initErrors()
  }

//  fun getPersistentView(inflater: LayoutInflater?, container: ViewGroup?): ViewBinding? {
//    if (viewBinding == null) {
//      // Inflate the layout for this fragment
//      viewBinding = getLayout(inflater!!, container)
//    } else {
//      // Do not inflate the layout again.
//      // The returned View of onCreateView will be added into the fragment.
//      // However it is not allowed to be added twice even if the parent is same.
//      // So we must remove rootView from the existing parent view group
//      // (it will be added back).
//      (viewBinding!!.root as? ViewGroup)?.removeView(viewBinding!!.root)
//    }
//
//    return viewBinding
//  }

  fun errorToast(pair: Pair<String, String>, callback: MainActivity.OnToastCallback) {
    (requireActivity() as MainActivity).errorToast(pair, callback)
  }

  abstract fun init()

  abstract fun initTransitions()

  abstract fun initToolbar()

  abstract fun initListeners(view: View)

  abstract fun initObservables()

  abstract fun initErrors()

  abstract fun getLayout(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): ViewBinding

  fun toast(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

  fun log(message: String) = Log.i("Tooka", message)

  fun customToast(view: View) {
    val toast = Toast(requireContext()).apply {
//      setGravity(Gravity.CENTER_VERTICAL, 0, 0)
      setDuration(Toast.LENGTH_LONG)
      setView(view)
      show()
    }
  }

  fun customToast(text: String, @ColorRes color: Int, @DrawableRes icon: Int) {
    TastyToasty.Builder(requireContext())
      .setText(text)
      .setBackgroundColor(color)
      .setIconId(icon)
      .show()
  }
}