package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentBreakingNewsBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.adapters.NewsAdapter
import ir.tdaapp.tooka.models.dataclasses.News
import ir.tdaapp.tooka.models.util.NetworkErrors
import ir.tdaapp.tooka.models.util.openWebpage
import ir.tdaapp.tooka.models.viewmodels.BreakingNewsViewModel
import ir.tdaapp.tooka.ui.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

class BreakingNewsFragment: BaseFragment() {

  companion object {
    const val TAG = "BreakingNewsFragment"
  }

  private lateinit var binding: FragmentBreakingNewsBinding
  private lateinit var adapter: NewsAdapter
  private val viewModel: BreakingNewsViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onResume() {
    super.onResume()
    (requireActivity() as MainActivity).bottomNavVisibility = false
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    binding.toolbar.title = getString(R.string.breaking_news)

    adapter = NewsAdapter { item, pos ->
      newsClicked(item)
    }

    binding.list.apply {
      adapter = this@BreakingNewsFragment.adapter
      layoutManager = LinearLayoutManager(requireContext())
    }

    viewModel.news.observe(viewLifecycleOwner) {
      adapter.differ.submitList(it)
    }

    viewModel.error.observe(viewLifecycleOwner) {
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

  private fun newsClicked(clicked: News) {
    when (clicked.newsKind) {
      News.EXTERNAL_NEWS -> {
        openWebpage(requireActivity(), clicked.url!!)
      }
      News.INTERNAL_NEWS -> {
        findNavController().navigate(
          BreakingNewsFragmentDirections.actionBreakingNewsFragmentToNewsDetailsFragment2(
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
}