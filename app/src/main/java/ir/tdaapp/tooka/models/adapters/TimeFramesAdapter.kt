package ir.tdaapp.tooka.models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemTimeFrameBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.getCurrentLocale
import ir.tdaapp.tooka.models.util.setCorrectMargins

typealias TimeFramesCallback = (clicked: TimeFrameModel, position: Int)->Unit

class TimeFramesAdapter(val action: TimeFramesCallback):
  RecyclerView.Adapter<TimeFramesAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemTimeFrameBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTimeFrameBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, TimeFrameDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    setCorrectMargins(
      holder.binding.root,
      holder.binding.root.context,
      position,
      differ.currentList.size - 1
    )

    if (differ.currentList[position].isSelected) {
      holder.binding.root.setCardBackgroundColor(holder.binding.root.context.getColor(R.color.blue_200))
      holder.binding.txtTimeFrameCaption.setTextColor(holder.binding.root.context.getColor(R.color.white))
      holder.binding.txtTimeFrameNum.setTextColor(holder.binding.root.context.getColor(R.color.white))
    } else {
      holder.binding.root.setCardBackgroundColor(holder.binding.root.context.getColor(R.color.white))
      holder.binding.txtTimeFrameCaption.setTextColor(holder.binding.root.context.getColor(R.color.black))
      holder.binding.txtTimeFrameNum.setTextColor(holder.binding.root.context.getColor(R.color.black))
    }

    holder.binding.txtTimeFrameNum.text = data.num
    holder.binding.txtTimeFrameCaption.text = when (getCurrentLocale(holder.binding.root.context)) {
      "en" -> {
        data.captionEN
      }
      "fa" -> {
        data.captionFa
      }
      else -> {
        data.captionEN
      }
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class TimeFrameDiffCallback: DiffUtil.ItemCallback<TimeFrameModel>() {

  override fun areItemsTheSame(oldItem: TimeFrameModel, newItem: TimeFrameModel): Boolean =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: TimeFrameModel, newItem: TimeFrameModel): Boolean =
    oldItem == newItem
}
