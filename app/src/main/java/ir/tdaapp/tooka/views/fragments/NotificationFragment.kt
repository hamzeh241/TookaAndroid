package ir.tdaapp.tooka.views.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.NotificationsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.FragmentNotificationBinding
import ir.tdaapp.tooka.databinding.ItemNotificationBinding
import ir.tdaapp.tooka.models.Notification
import ir.tdaapp.tooka.viewmodels.NotificationsViewModel
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject

class NotificationFragment: BaseFragment() {

  private lateinit var binding: FragmentNotificationBinding

  private lateinit var adapter: TookaAdapter<Notification>
  private val viewModel: NotificationsViewModel by inject()

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initAdapter()
    initList()

    viewModel.getData("samanapikey", 0)
  }

  override fun initTransitions() {
  }

  fun initAdapter() {
    adapter = object: TookaAdapter<Notification>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        NotificationsViewHolder(ItemNotificationBinding.inflate(layoutInflater, parent, false))
    }
  }

  fun initList() {
    binding.notifList.layoutManager = LinearLayoutManager(requireContext())
    binding.notifList.adapter = adapter
  }

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = getString(R.string.notifications)
  }

  override fun initListeners(view: View) {
    adapter.callback = TookaAdapter.Callback { vm, pos -> }
  }

  override fun initObservables() {
    viewModel.result.observe(viewLifecycleOwner, {
      if (it != null && it.isNotEmpty()) {
        adapter.models = it as ArrayList<Notification>
        binding.notifList.visibility = View.VISIBLE

        binding.notifLoading.visibility = View.GONE
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentNotificationBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onDestroy() {
    (requireActivity() as MainActivity).bottomNavVisibility = true
    super.onDestroy()
  }
}