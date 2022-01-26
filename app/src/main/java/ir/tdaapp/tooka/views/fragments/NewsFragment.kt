package ir.tdaapp.tooka.views.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.microsoft.signalr.Action1
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.BreakingAndCryptoNewsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.SliderNewsViewHolder
import ir.tdaapp.tooka.databinding.FragmentNewsBinding
import ir.tdaapp.tooka.databinding.ItemBreakingCryptoNewsBinding
import ir.tdaapp.tooka.databinding.ItemSliderNewsBinding
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.models.SliderNews
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.openWebpage
import ir.tdaapp.tooka.viewmodels.NewsViewModel
import ir.tdaapp.tooka.views.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

class NewsFragment: BaseFragment() {

  lateinit var binding: FragmentNewsBinding

  lateinit var sliderAdapter: TookaAdapter<SliderNews>
  lateinit var breakingAdapter: TookaAdapter<News>
  lateinit var cryptoNewsAdapter: TookaAdapter<News>

  val viewModel: NewsViewModel by inject()

  override fun init() {
    initSliderNews()
    initIndicator()
    initBreakingList()
    initCryptoNews()
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    binding.toolbar.title = getString(R.string.news)
  }

  override fun initListeners(view: View) {
    sliderAdapter.callback = TookaAdapter.Callback { vm, position -> }

    breakingAdapter.callback = TookaAdapter.Callback { vm, position -> }

    cryptoNewsAdapter.callback = TookaAdapter.Callback { vm, position ->
      when (vm.newsKind) {
        News.OPEN_URL -> {
          openWebpage(requireContext(), vm.url)
        }
        News.SHORT_NEWS -> {
          val dialog = NewsDetailsDialog(vm.id)
          dialog.show(requireActivity().supportFragmentManager, NewsDetailsDialog.TAG)
        }
      }
    }
  }

  override fun initObservables() {
    viewModel.sliderNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        sliderAdapter.models = it as ArrayList<SliderNews>
        binding.includeSlider.loading.pauseAnimation()
        binding.includeSlider.newsViewPager.visibility = View.VISIBLE
        binding.includeSlider.dotsIndicator.visibility = View.VISIBLE
        binding.includeSlider.loading.visibility = View.GONE
      }
    })

    viewModel.breakingNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        breakingAdapter.models = it as ArrayList<News>
        binding.includeBreakingNews.loading.pauseAnimation()
        binding.includeBreakingNews.breakingNewsList.visibility = View.VISIBLE
        binding.includeBreakingNews.loading.visibility = View.GONE
      }
    })

    viewModel.allNews.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        cryptoNewsAdapter.models = it as ArrayList<News>
        binding.includeCryptoNews.loading.pauseAnimation()
        binding.includeCryptoNews.cryptoNewsList.visibility = View.VISIBLE
        binding.includeCryptoNews.loading.visibility = View.GONE
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentNewsBinding.inflate(inflater, container, false)
    return binding
  }

  private fun initIndicator() {
    binding.includeSlider.dotsIndicator.setViewPager2(binding.includeSlider.newsViewPager)
  }

  private fun initSliderNews() {
    sliderAdapter = object: TookaAdapter<SliderNews>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SliderNewsViewHolder(
          ItemSliderNewsBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
    }

    viewModel.getData()



    binding.includeSlider.newsViewPager.adapter = sliderAdapter
  }

  private fun initBreakingList() {
    breakingAdapter = object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BreakingAndCryptoNewsViewHolder(
          ItemBreakingCryptoNewsBinding.inflate(layoutInflater, parent, false)
        )
    }

    binding.includeBreakingNews.breakingNewsList.layoutManager = GridLayoutManager(
      requireContext(), 2,
      GridLayoutManager.HORIZONTAL, false
    )

    binding.includeBreakingNews.breakingNewsList.adapter = breakingAdapter
  }

  private fun initCryptoNews() {
    cryptoNewsAdapter = object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BreakingAndCryptoNewsViewHolder(
          ItemBreakingCryptoNewsBinding.inflate(layoutInflater, parent, false)
        )
    }


    binding.includeCryptoNews.cryptoNewsList.layoutManager =
      object: LinearLayoutManager(requireContext()) {
        override fun canScrollVertically(): Boolean = false
      }

    binding.includeCryptoNews.cryptoNewsList.adapter = cryptoNewsAdapter
  }
}