package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.BreakingAndCryptoNewsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.FragmentRelatedNewsBinding
import ir.tdaapp.tooka.databinding.ItemBreakingCryptoNewsBinding
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.viewmodels.RelatedNewsViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject
import java.lang.StringBuilder

class RelatedNewsFragment: BaseFragment() {

  lateinit var binding: FragmentRelatedNewsBinding

  val viewModel: RelatedNewsViewModel by inject()
  lateinit var adapter: TookaAdapter<News>

  override fun init() {
    initList()
    viewModel.getData(RelatedNewsFragmentArgs.fromBundle(requireArguments()).coinId)
  }

  fun initList() {
    adapter = object: TookaAdapter<News>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        BreakingAndCryptoNewsViewHolder(
          ItemBreakingCryptoNewsBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
    }

    binding.list.layoutManager = LinearLayoutManager(requireContext())
    binding.list.adapter = adapter
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = StringBuilder(getString(R.string.news_related_to))
      .append(" ").append(
        RelatedNewsFragmentArgs.fromBundle(requireArguments()).coinName
      ).toString()
  }

  override fun initListeners(view: View) {
    adapter.callback = TookaAdapter.Callback { vm, pos ->
    }
  }

  override fun initObservables() {
    viewModel.news.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        binding.loading.pauseAnimation()
        binding.loading.visibility = View.GONE
        adapter.models = it as ArrayList<News>
        binding.list.visibility = View.VISIBLE
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentRelatedNewsBinding.inflate(inflater, container, false)
    return binding
  }
}