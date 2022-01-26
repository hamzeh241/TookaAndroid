package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.PlatformsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.WalletsViewHolder
import ir.tdaapp.tooka.databinding.DialogAutomaticBottomSheetBinding
import ir.tdaapp.tooka.databinding.ItemPlatformBinding
import ir.tdaapp.tooka.databinding.ItemWalletAddressBinding
import ir.tdaapp.tooka.models.AutoWalletModel
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.Platform
import ir.tdaapp.tooka.models.PostPortfolioResponse
import ir.tdaapp.tooka.util.toast
import ir.tdaapp.tooka.viewmodels.AutomaticBottomSheetViewModel
import okhttp3.internal.waitMillis
import org.koin.android.ext.android.inject

class AutomaticPortfolioBottomSheetDialog: BottomSheetDialogFragment() {

  companion object {
    const val TAG = "AutomaticPortfolioBottomSheetDialog"
  }

  interface AutomaticPortfolioCallback {
    fun onResult(response: PostPortfolioResponse)
  }

  var callback: AutomaticPortfolioCallback? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  lateinit var binding: DialogAutomaticBottomSheetBinding
  lateinit var platformAdapter: TookaAdapter<Platform>

  lateinit var coins: ArrayList<Coin>

  lateinit var selectedCoin: Coin

  val viewModel: AutomaticBottomSheetViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogAutomaticBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    viewModel.getData()

    platformAdapter = object: TookaAdapter<Platform>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PlatformsViewHolder(ItemPlatformBinding.inflate(layoutInflater, parent, false))
    }
    platformAdapter.callback = TookaAdapter.Callback { vm, position ->
      val a = platformAdapter.models.singleOrNull { it.isSelected }
      val pos =
        platformAdapter.models.indexOf(a)

      platformAdapter.models.forEach {
        it.isSelected = false
      }
      platformAdapter.models[position].isSelected = true

      platformAdapter.notifyItemChanged(position)
      platformAdapter.notifyItemChanged(pos)
    }

    viewModel.coins.observe(viewLifecycleOwner, {
      coins = it as ArrayList<Coin>
      val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
      (binding.autoCompleteSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    })
    viewModel.platforms.observe(viewLifecycleOwner, {
      if (it.size > 0) {
        platformAdapter.models = it as ArrayList<Platform>
        platformAdapter.models[0].isSelected = true
        initPlatformList()
      }
    })
    viewModel.balance.observe(viewLifecycleOwner, {
      callback!!.onResult(it)
      dismiss()
    })

    binding.autoCompleteSpinner.onItemClickListener =
      object: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          viewModel.getPlatforms(coins[position].id)
          selectedCoin = coins[position]
        }
      }

    binding.cardSubmit.setOnClickListener {
      var a =
        AutoWalletModel("samanapikey", 1, binding.edtWallet.text.toString(), selectedCoin.id)
      viewModel.addPortfolio(a)
    }
  }

  fun initPlatformList() {

    val gridLayoutManager =
      GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)

    binding.platformList.layoutManager = gridLayoutManager
    binding.platformList.adapter = platformAdapter
  }
}