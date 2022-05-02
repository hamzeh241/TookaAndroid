package ir.tdaapp.tooka.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.models.adapters.CoinsViewHolder
import ir.tdaapp.tooka.models.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.DialogCoinsListBottomSheetBinding
import ir.tdaapp.tooka.databinding.ItemCoinsListBinding
import ir.tdaapp.tooka.models.dataclasses.*
import java.util.ArrayList

class CoinsListBottomSheetDialog(val models: ArrayList<Coin>):
  BottomSheetDialogFragment() {

  companion object {
    const val TAG = "CoinsListBottomSheetDialog"
  }

  fun interface OnCoinSelected {
    fun onCoinSelected(id: Int)
  }

  var callback: OnCoinSelected? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  lateinit var binding: DialogCoinsListBottomSheetBinding
  lateinit var adapter: TookaAdapter<Coin>

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogCoinsListBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    initList()
  }

  fun initList() {
    adapter = object: TookaAdapter<Coin>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CoinsViewHolder(ItemCoinsListBinding.inflate(layoutInflater, parent, false))
    }
    adapter.models = models

    adapter.callback = TookaAdapter.Callback { vm, pos ->
      callback!!.onCoinSelected(vm.id)
      dismiss()
    }

    binding.coinsList.layoutManager = LinearLayoutManager(binding.root.context)
    binding.coinsList.adapter = adapter
  }
}