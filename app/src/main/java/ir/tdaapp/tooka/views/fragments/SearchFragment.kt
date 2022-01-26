package ir.tdaapp.tooka.views.fragments

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.adapters.BreakingAndCryptoNewsViewHolder
import ir.tdaapp.tooka.adapters.MarketCoinsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.components.TookaSearchView
import ir.tdaapp.tooka.databinding.FragmentSearchBinding
import ir.tdaapp.tooka.databinding.ItemBreakingCryptoNewsBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.toast
import ir.tdaapp.tooka.viewmodels.SearchViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

class SearchFragment: BaseFragment() {

  private lateinit var binding: FragmentSearchBinding

  private lateinit var coinAdapter: TookaAdapter<Coin>
  private lateinit var newsAdapter: TookaAdapter<News>

  private val viewModel: SearchViewModel by inject()

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initSearchBar()
    initAdapters()
    initRecyclerViews()

    binding.edtSearch.requestFocus()
  }

  private fun initSearchBar() = binding.edtSearch.setOnKeyListener(object: View.OnKeyListener {
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
      if ((event?.getAction() == KeyEvent.ACTION_DOWN) &&
        (keyCode == KeyEvent.KEYCODE_ENTER)
      ) {
        viewModel.getData(binding.edtSearch.text.toString())
        return true
      }
      return false
    }
  })

  private fun initAdapters() {
    coinAdapter = object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MarketCoinsViewHolder(ItemMarketCoinsFlatBinding.inflate(layoutInflater, parent, false))
    }
    newsAdapter = object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BreakingAndCryptoNewsViewHolder(
          ItemBreakingCryptoNewsBinding.inflate(layoutInflater, parent, false)
        )
    }
  }

  private fun initRecyclerViews() {
    binding.coinsList.layoutManager = LinearLayoutManager(requireContext())
    binding.newsList.layoutManager = LinearLayoutManager(requireContext())

    binding.coinsList.adapter = coinAdapter
    binding.newsList.adapter = newsAdapter
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
  }

  override fun initListeners(view: View) {
    coinAdapter.callback = TookaAdapter.Callback { vm, pos -> }

    newsAdapter.callback = TookaAdapter.Callback { vm, pos -> }
  }

  override fun initObservables() {
    viewModel.result.observe(viewLifecycleOwner, {
      if (it != null) {
        if (it.news.size <= 0)
          binding.newsSection.visibility = View.GONE
        else {
          binding.newsSection.visibility = View.VISIBLE
          newsAdapter.models = it.news as ArrayList<News>
          newsAdapter.notifyDataSetChanged()
        }

        if (it.coins.size <= 0)
          binding.coinsSection.visibility = View.GONE
        else {
          binding.coinsSection.visibility = View.VISIBLE
          coinAdapter.models = it.coins as ArrayList<Coin>
          coinAdapter.notifyDataSetChanged()
        }
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentSearchBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onDestroy() {
    (requireActivity() as MainActivity).bottomNavVisibility = true
    super.onDestroy()
  }
}