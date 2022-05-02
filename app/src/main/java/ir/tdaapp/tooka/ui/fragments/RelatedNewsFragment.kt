package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.NewsAdapter
import ir.tdaapp.tooka.databinding.FragmentRelatedNewsBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.util.openWebpage
import ir.tdaapp.tooka.viewmodels.RelatedNewsViewModel
import ir.tdaapp.tooka.ui.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class RelatedNewsFragment: BaseFragment(), CoroutineScope {

  private lateinit var binding: FragmentRelatedNewsBinding

  private val viewModel: RelatedNewsViewModel by inject()
  private lateinit var adapter: NewsAdapter
  private val args: RelatedNewsFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized)
      return binding.root

    binding = FragmentRelatedNewsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.swipeRefreshLayout.isRefreshing = true
    initToolbar()
    initAdapter()
    initList()
    initSwipeRefresh()
    initObservables()

    lifecycleScope.launchWhenCreated {
      viewModel.getData(args.coinId)
    }
  }

  private fun initAdapter() {
    adapter = NewsAdapter { clicked, position ->
      newsClicked(clicked)
    }
  }

  private fun initSwipeRefresh() = binding.swipeRefreshLayout.setOnRefreshListener {
    launch(Dispatchers.IO) {
      viewModel.getData(args.coinId, true)
    }
  }

  private fun newsClicked(clicked: News) {
    when (clicked.newsKind) {
      News.EXTERNAL_NEWS -> {
        openWebpage(requireActivity(), clicked.url)
      }
      News.INTERNAL_NEWS -> {
        findNavController().navigate(
          RelatedNewsFragmentDirections.actionRelatedNewsFragmentToNewsDetailsFragment(
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

  private fun initList() = with(binding.list) {
    layoutManager = LinearLayoutManager(requireContext())
    adapter = this@RelatedNewsFragment.adapter
  }


  fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = StringBuilder(getString(R.string.news_related_to))
      .append(" ").append(
        RelatedNewsFragmentArgs.fromBundle(requireArguments()).coinName
      ).toString()
  }

  private fun initObservables() {
    viewModel.news.observe(viewLifecycleOwner, {
      binding.swipeRefreshLayout.isRefreshing = false
      adapter.differ.submitList(it as ArrayList)
    })

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

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("RelatedNewsFragmentJob")
}