package ir.tdaapp.tooka.views.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.MarketsAdapter
import ir.tdaapp.tooka.models.adapters.NewsAdapter
import ir.tdaapp.tooka.databinding.FragmentSearchBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.util.InputManagerHelper
import ir.tdaapp.tooka.util.NetworkErrors
import ir.tdaapp.tooka.viewmodels.SearchViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class SearchFragment: BaseFragment(), CoroutineScope {

  private lateinit var binding: FragmentSearchBinding

  private lateinit var coinAdapter: MarketsAdapter
  private lateinit var newsAdapter: NewsAdapter

  private lateinit var manager: InputMethodManager

  private val viewModel: SearchViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentSearchBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initToolbar()
    initAdapters()
    initRecyclerViews()
    initSearchBar()
    initSwipeRefresh()

    initObservables()

    binding.edtSearch.requestFocus()
    manager = InputManagerHelper.getManager(requireContext())
//    manager.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT);
    manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  private fun initSearchBar() = binding.edtSearch.setOnKeyListener(object: View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
      if ((event?.getAction() == KeyEvent.ACTION_DOWN) &&
        (keyCode == KeyEvent.KEYCODE_ENTER)
      ) {
        launch {
          withContext(Dispatchers.Main) {
            binding.swipeRefreshLayout.isRefreshing = true
          }
          viewModel.getData(binding.edtSearch.text.toString())
        }
        return true
      }
      return false
    }
  })

  private fun initSwipeRefresh() =
    binding.swipeRefreshLayout.setOnRefreshListener {
      launch {
        withContext(Dispatchers.Main) {
          binding.swipeRefreshLayout.isRefreshing = true
        }
        viewModel.getData(binding.edtSearch.text.toString())
      }
    }


  private fun initAdapters() {
    coinAdapter = MarketsAdapter { clicked, position ->

    }
    newsAdapter = NewsAdapter { clicked, position ->

    }
  }

  private fun initRecyclerViews() {
    binding.coinsList.layoutManager = LinearLayoutManager(requireContext())
    binding.newsList.layoutManager = LinearLayoutManager(requireContext())

    binding.coinsList.adapter = coinAdapter
    binding.newsList.adapter = newsAdapter
  }

  private fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
    binding.toolbar.setNavigationOnClickListener {
      findNavController().popBackStack()
    }
  }

  private fun initObservables() {
    viewModel.result.observe(viewLifecycleOwner) {
      binding.swipeRefreshLayout.isRefreshing = false
      if (it.news.size <= 0)
        binding.newsSection.visibility = View.GONE
      else {
        binding.newsSection.visibility = View.VISIBLE
        newsAdapter.differ.submitList(it.news)
      }

      if (it.coins.size <= 0)
        binding.coinsSection.visibility = View.GONE
      else {
        binding.coinsSection.visibility = View.VISIBLE
        coinAdapter.differ.submitList(it.coins)
      }
    }

    viewModel.error.observe(viewLifecycleOwner) {
      binding.swipeRefreshLayout.isRefreshing = false
      val message: String
      @DrawableRes val icon: Int
      when (it) {
        NetworkErrors.NETWORK_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.CLIENT_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        NetworkErrors.NOT_FOUND_ERROR -> {
          message = getString(R.string.coin_not_found)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          requireActivity().onBackPressed()
        }
        NetworkErrors.SERVER_ERROR -> {
          message = getString(R.string.server_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNAUTHORIZED_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNKNOWN_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
      }

      Toast(requireContext()).apply {
        setDuration(Toast.LENGTH_LONG)
        setView(ToastLayoutBinding.inflate(layoutInflater).apply {
          this.message.text = message
          image.setImageResource(icon)
        }.root)
        show()
      }
    }
  }

  override fun onDestroy() {
    (requireActivity() as MainActivity).bottomNavVisibility = true
    manager.hideSoftInputFromWindow(binding.coinsSection.getWindowToken(), 0);
    super.onDestroy()
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}