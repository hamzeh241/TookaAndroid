package ir.tdaapp.tooka.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.databinding.FragmentNewsDetailsBinding
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.getCurrentLocale
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond

class NewsDetailsFragment: BaseFragmentSecond() {

  private lateinit var binding: FragmentNewsDetailsBinding

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    val newsId = NewsDetailsFragmentArgs.fromBundle(requireArguments()).newsId
    val url = StringBuilder(RetrofitClient.NEWS_URL)
      .append("?newsId=")
      .append(newsId)
      .append("&lang=${getCurrentLocale(requireContext())}")
    binding.webView.webViewClient = object: WebViewClient() {
      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding.loading.apply {
          pauseAnimation()
          visibility = View.GONE
        }
        binding.webView.visibility = View.VISIBLE
      }
    }
    binding.webView.loadUrl(url.toString())
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

  override fun onDestroy() {
    (requireActivity() as MainActivity).bottomNavVisibility = true
    super.onDestroy()
  }
}