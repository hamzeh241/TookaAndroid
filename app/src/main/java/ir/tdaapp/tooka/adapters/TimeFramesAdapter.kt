package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemTimeFrameBinding
import ir.tdaapp.tooka.models.TimeFrameModel
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.setCorrectMargins

class TimeFramesAdapter(val action: (clicked: TimeFrameModel, position: Int)->Unit):
  RecyclerView.Adapter<TimeFramesAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemTimeFrameBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTimeFrameBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

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

private class TimeFrameDiffCallback: DiffUtil.ItemCallback<TimeFrameModel>() {

  override fun areItemsTheSame(oldItem: TimeFrameModel, newItem: TimeFrameModel): Boolean =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: TimeFrameModel, newItem: TimeFrameModel): Boolean =
    oldItem == newItem
}
