package ir.tdaapp.tooka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.NewsAdapter
import ir.tdaapp.tooka.databinding.FragmentAllNewsBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.openWebpage
import ir.tdaapp.tooka.models.viewmodels.AllNewsViewModel
import ir.tdaapp.tooka.ui.dialogs.NewsDetailsDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class AllNewsFragment: BaseFragment(), CoroutineScope {

  companion object {
    const val TAG = "AllNewsFragment"
  }

  private lateinit var binding: FragmentAllNewsBinding
  private lateinit var adapter: NewsAdapter
  private val viewModel: AllNewsViewModel by inject()

  private var previousTotal = 0
  private var page = 0
  private var isRefreshing = false
  private var isLoading = true
    set(value) {
      binding.swipeRefreshLayout.isRefreshing = value
      field = value
    }
  private var visibleThreshold = 5
  private var firstVisibleItem = 0
  private var visibleItemCount: Int = 0
  private var totalItemCount: Int = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    if (this::binding.isInitialized)
      return binding.root
    else {
      binding = FragmentAllNewsBinding.inflate(inflater, container, false)
      return binding.root
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as MainActivity).bottomNavVisibility = false
    binding.swipeRefreshLayout.isRefreshing = true
    initToolbar()
    initAdapter()
    initList()
    initPagination()
    initObservables()
    initSwipeRefresh()
  }

  private fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    binding.toolbar.title = getString(R.string.crypto_news)
  }

  private fun initObservables() = with(viewModel) {
    news.observe(viewLifecycleOwner) {
      binding.swipeRefreshLayout.isRefreshing = false
      if (!isRefreshing)
        adapter.differ.submitList(adapter.differ.currentList + it)
      else
        adapter.differ.submitList(it)

      isRefreshing = false
    }

    error.observe(viewLifecycleOwner) {
      val message = getString(R.string.server_error_desc)
      val icon = R.drawable.ic_dns_white_24dp
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

  private fun initSwipeRefresh() = binding.swipeRefreshLayout.setOnRefreshListener {
    isRefreshing = true
    launch {
      viewModel.getData(0)
      page = 0
      firstVisibleItem = 0
      visibleItemCount = 0
      totalItemCount = 0
    }
  }

  private fun initPagination() {
    binding.list.addOnScrollListener(object: RecyclerView.OnScrollListener() {
      override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleThreshold = layoutManager.findLastVisibleItemPosition()
        visibleItemCount = binding.list.getChildCount()
        totalItemCount = layoutManager.getItemCount()
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        if (isLoading) {
          if (totalItemCount > previousTotal) {
            isLoading = false
            previousTotal = totalItemCount
          }
        }
        if (adapter.getItemCount() > 40) {
          if (!isLoading && totalItemCount - visibleItemCount
            <= firstVisibleItem + visibleThreshold
          ) {
            // End has been reached
            page++

            launch {
              viewModel.getData(page)
            }
            // Do something
            isLoading = true
          }
        }
      }
    })
  }

  private fun initAdapter() {
    adapter = NewsAdapter { clicked, position ->
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
          AllNewsFragmentDirections.actionAllNewsFragmentToNewsDetailsFragment2(
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

  private val layoutManager: LinearLayoutManager by lazy {
    LinearLayoutManager(requireContext())
  }

  private fun initList() = binding.list.apply {
    layoutManager = this@AllNewsFragment.layoutManager
    adapter = this@AllNewsFragment.adapter
  }

  override fun onDestroy() {
    super.onDestroy()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}