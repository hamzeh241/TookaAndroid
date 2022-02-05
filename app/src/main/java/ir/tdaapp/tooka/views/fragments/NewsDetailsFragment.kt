package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.databinding.FragmentNewsDetailsBinding
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import java.lang.StringBuilder

class NewsDetailsFragment: BaseFragment() {

  private lateinit var binding: FragmentNewsDetailsBinding

  override fun init() {
    val newsId = NewsDetailsFragmentArgs.fromBundle(requireArguments()).newsId
    if (newsId > 0) {
      val url = StringBuilder(RetrofitClient.NEWS_URL).append(newsId)
      binding.webView.loadUrl(url.toString())
    }
  }

  override fun initTransitions() = Unit

  override fun initToolbar() = Unit

  override fun initListeners(view: View) = Unit

  override fun initObservables() = Unit

  override fun initErrors() = Unit

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentNewsDetailsBinding.inflate(inflater, container, false)
    return binding
  }
}