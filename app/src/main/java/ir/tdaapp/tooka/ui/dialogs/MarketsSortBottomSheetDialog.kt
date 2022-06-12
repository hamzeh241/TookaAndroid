package ir.tdaapp.tooka.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.databinding.DialogSortBottomSheetBinding
import ir.tdaapp.tooka.models.adapters.SortAdapter
import ir.tdaapp.tooka.models.dataclasses.SortModel

typealias SortCallback = (List<SortModel>)->Unit

class MarketsSortBottomSheetDialog(private val models: List<SortModel>, val callback: SortCallback):
  BottomSheetDialogFragment() {

  companion object {
    const val TAG = "MarketsSortBottomSheetDialog"
  }

  private lateinit var binding: DialogSortBottomSheetBinding
  private lateinit var adapter: SortAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DialogSortBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    adapter = SortAdapter { model, position ->
      callback(adapter.differ.currentList)
      dismiss()
    }

    adapter.differ.submitList(models)

    binding.sortList.apply {
      layoutManager = LinearLayoutManager(requireContext())
      adapter = this@MarketsSortBottomSheetDialog.adapter
    }
  }
}