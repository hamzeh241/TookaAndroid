package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemSortOptionBinding
import ir.tdaapp.tooka.models.SortModel
import ir.tdaapp.tooka.util.getCurrentLocale

typealias SortCallback = (clicked: SortModel, position: Int)->Unit

class SortAdapter(private val click: SortCallback):
  RecyclerView.Adapter<SortAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSortOptionBinding):
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSortOptionBinding.inflate(layoutInflater, parent, false)
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
    holder.binding.txtSortOptionsTitle.text = when (getCurrentLocale(holder.binding.root.context)) {
      "fa" -> data.nameFa
      else -> data.nameEn
    }

    @DrawableRes val image = if (!data.isSelected) R.drawable.ic_remove else {
      if (data.isAscend) R.drawable.ic_arrow_ascend else
        R.drawable.ic_arrow_descend
    }

    val drawable = ResourcesCompat.getDrawable(holder.binding.root.resources, image, null)
    drawable.let {
      val dimen16 = holder.binding.root.resources.getDimension(R.dimen.size16)
      it?.setBounds(0, 0, dimen16.toInt(), dimen16.toInt())
    }

    when (getCurrentLocale(holder.binding.root.context)) {
      "fa" ->
        holder.binding.txtSortOptionsTitle.setCompoundDrawables(
          drawable,
          null,
          null,
          null
        )
      else ->
        holder.binding.txtSortOptionsTitle.setCompoundDrawablesWithIntrinsicBounds(
          null,
          null,
          drawable,
          null
        )
    }

    holder.binding.root.setOnClickListener {
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