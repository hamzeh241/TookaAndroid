package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.MarketCoinsViewHolder
import ir.tdaapp.tooka.databinding.FragmentRelatedCoinsBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.viewmodels.RelatedCoinsViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

class RelatedCoinsFragment: BaseFragment() {

  lateinit var binding: FragmentRelatedCoinsBinding

  lateinit var adapter: TookaAdapter<Coin>
  val viewModel: RelatedCoinsViewModel by inject()

  override fun init() {
    initList()

    viewModel.getData(RelatedCoinsFragmentArgs.fromBundle(requireArguments()).coinId)
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = getString(R.string.other_coins)
  }

  fun initList() {
    adapter = object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MarketCoinsViewHolder(
          ItemMarketCoinsFlatBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
    }

    binding.list.layoutManager = LinearLayoutManager(requireContext())
    binding.list.adapter = adapter
  }

  override fun initListeners(view: View) {
    adapter.callback = TookaAdapter.Callback { vm, pos ->
    }
  }

  override fun initObservables() {
    viewModel.coins.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        binding.loading.pauseAnimation()
        binding.loading.visibility = View.GONE
        adapter.models = it as ArrayList<Coin>
        binding.list.visibility = View.VISIBLE
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentRelatedCoinsBinding.inflate(inflater, container, false)
    return binding
  }
}