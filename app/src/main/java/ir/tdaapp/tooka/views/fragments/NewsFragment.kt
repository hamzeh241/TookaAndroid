package ir.tdaapp.tooka.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.NewsAdapter
import ir.tdaapp.tooka.adapters.SliderNewsAdapter
import ir.tdaapp.tooka.databinding.FragmentNewsBinding
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.openWebpage
import ir.tdaapp.tooka.viewmodels.NewsViewModel
import ir.tdaapp.tooka.views.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class NewsFragment: BaseFragment(), CoroutineScope, View.OnClickListener {

  lateinit var binding: FragmentNewsBinding

  lateinit var sliderAdapter: SliderNewsAdapter
  lateinit var breakingAdapter: NewsAdapter
  lateinit var cryptoNewsAdapter: NewsAdapter

  private val viewModel: NewsViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View =
    if (this::binding.isInitialized)
      binding.root
    else {
      binding = FragmentNewsBinding.inflate(inflater, container, false)
      binding.root
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.toolbar.title = getString(R.string.news)
    initAdapters()
    initViewPager()
    initBreakingList()
    initCryptoNews()
    initObservables()

    lifecycleScope.launchWhenCreated {
      viewModel.getData()
    }
  }

  private fun initAdapters() {
    sliderAdapter = SliderNewsAdapter { clicked, position ->
      when (clicked.newsKind) {
        News.EXTERNAL_NEWS -> {
          openWebpage(requireActivity(), clicked.newsUrl)
        }
        News.INTERNAL_NEWS -> {
          findNavController().navigate(
            NewsFragmentDirections.actionNewsFragmentToNewsDetailsFragment2(
              clicked.newsId
            )
          )
        }
        News.SHORT_NEWS -> {
          NewsDetailsDialog(clicked.newsId).show(
            requireActivity().supportFragmentManager,
            NewsDetailsDialog.TAG
          )
        }
      }
    }
    breakingAdapter = NewsAdapter { clicked, position ->
      newsClicked(clicked)
    }
    cryptoNewsAdapter = NewsAdapter { clicked, position ->
      newsClicked(clicked)
    }
  }

  private fun newsClicked(clicked: News) {
    when (clicked.newsKind) {
      News.EXTERNAL_NEWS -> {
        openWebpage(requireActivity(), clicked.url)
      }
      News.INTERNAL_NEWS -> {
        findNavController().navigate(
          NewsFragmentDirections.actionNewsFragmentToNewsDetailsFragment2(
            clicked.id
          )
        )
      }
      News.SHORT_NEWS -> {
        NewsDetailsDialog(clicked.id).show(
          requireActivity().supportFragmentManager,
          NewsDetailsDialog.TAG
        )
      }
    }
  }

  fun initObservables() {
    viewModel.sliderNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        sliderAdapter.differ.submitList(it)
        binding.includeSlider.loading.pauseAnimation()
        binding.includeSlider.newsViewPager.visibility = View.VISIBLE
        binding.includeSlider.dotsIndicator.visibility = View.VISIBLE
        binding.includeSlider.loading.visibility = View.GONE
      }
    })

    viewModel.breakingNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        breakingAdapter.differ.submitList(it)
        binding.includeBreakingNews.loading.pauseAnimation()
        binding.includeBreakingNews.breakingNewsList.visibility = View.VISIBLE
        binding.includeBreakingNews.loading.visibility = View.GONE
      }
    })

    viewModel.allNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        cryptoNewsAdapter.differ.submitList(it)
        binding.includeCryptoNews.loading.pauseAnimation()
        binding.includeCryptoNews.cryptoNewsList.visibility = View.VISIBLE
        binding.includeCryptoNews.loading.visibility = View.GONE
      }
    })
  }

  private fun initViewPager() =
    binding.includeSlider.newsViewPager.apply {
      adapter = sliderAdapter
      binding.includeSlider.dotsIndicator.setViewPager2(this)
    }

  private val breakingManager by lazy {
    GridLayoutManager(
      requireContext(), 2,
      GridLayoutManager.HORIZONTAL, false
    )
  }

  private fun initBreakingList() =
    binding.includeBreakingNews.breakingNewsList.apply {
      layoutManager = breakingManager
      adapter = breakingAdapter
    }

  private val cryptoManager by lazy {
    object: LinearLayoutManager(requireContext()) {
      override fun canScrollVertically(): Boolean = false
    }
  }

  private fun initCryptoNews() =
    binding.includeCryptoNews.cryptoNewsList.apply {
      layoutManager = cryptoManager
      adapter = cryptoNewsAdapter
    }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("NewsFragmentJob")

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.txtBreakingSeeMore -> {

      }
      R.id.txtCryptoNewsSeeMore -> {

      }
    }
  }
}