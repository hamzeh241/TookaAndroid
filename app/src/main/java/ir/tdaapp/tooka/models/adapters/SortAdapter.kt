package ir.tdaapp.tooka.models.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemSortBottomSheetBinding
import ir.tdaapp.tooka.models.dataclasses.SortModel
import ir.tdaapp.tooka.models.util.getCurrentLocale

typealias SortCallback = (clicked: SortModel, position: Int)->Unit

class SortAdapter(private val click: SortCallback):
  RecyclerView.Adapter<SortAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSortBottomSheetBinding):
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSortBottomSheetBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, SortDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]
    holder.binding.txtSort.text = when (getCurrentLocale(holder.binding.root.context)) {
      "fa" -> data.nameFa
      else -> data.nameEn
    }

    @DrawableRes val image = if (!data.isSelected) R.drawable.ic_remove else {
      if (data.isAscend) R.drawable.ic_arrow_ascend else
        R.drawable.ic_arrow_descend
    }

    if (data.isSelected) {
      holder.binding.imgSort.visibility = View.VISIBLE
      holder.binding.imgSort.setImageResource(image)
    } else {
      holder.binding.imgSort.visibility = View.GONE
    }
    holder.binding.root.setOnClickListener {
      if (data.isSelected) {
        data.isAscend = !data.isAscend
      } else {
        differ.currentList.forEach { it.isSelected = false }
        data.isSelected = true
        data.isAscend = false
      }
      click.invoke(data, position)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    super.onBindViewHolder(holder, position, payloads)
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
class SortDiffCallback: DiffUtil.ItemCallback<SortModel>() {
  override fun areItemsTheSame(oldItem: SortModel, newItem: SortModel): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: SortModel, newItem: SortModel): Boolean {
    return oldItem.equals(newItem)
  }

}