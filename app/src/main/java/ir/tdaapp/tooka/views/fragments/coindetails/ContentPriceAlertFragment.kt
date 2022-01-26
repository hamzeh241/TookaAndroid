package ir.tdaapp.tooka.views.fragments.coindetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.R.drawable
import ir.tdaapp.tooka.databinding.FragmentContentPriceAlertBinding
import ir.tdaapp.tooka.views.fragments.base.BaseFragment

class ContentPriceAlertFragment(val coinId: Int): BaseFragment() {

  lateinit var binding: FragmentContentPriceAlertBinding

  override fun init() {
    initToggle()
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
  }

  override fun initListeners(view: View) {
  }

  override fun initObservables() {
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentContentPriceAlertBinding.inflate(inflater, container, false)
    return binding
  }

  fun initToggle(){
    binding.toggle.setFirstChoiceText(getString(R.string.higher))
    binding.toggle.setFirstChoiceIcon(drawable.ic_arrow_ascend)
    binding.toggle.setSecondChoiceText(getString(R.string.lower))
    binding.toggle.setSecondChoiceIcon(drawable.ic_arrow_descend)

    binding.toggle.setCallback {

    }
  }
}