package ir.tdaapp.tooka.models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemSliderNewsBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.getCurrentLocale

typealias SliderNewsCallback = (clicked: SliderNews, position: Int)->Unit

class SliderNewsAdapter(val action: SliderNewsCallback):
  RecyclerView.Adapter<SliderNewsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSliderNewsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSliderNewsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, SliderDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    val imageUrl = StringBuilder(RetrofitClient.NEWS_IMAGES).append(data.imageUrl).toString()
    Glide.with(holder.binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_placeholder)
      .error(R.drawable.ic_broken_image)
      .into(holder.binding.image)

    holder.binding.txtNewsTitle.text = when (getCurrentLocale(holder.binding.root.context)) {
      "en" -> data.titleEn
      "fa" -> data.titleFa
      else -> data.titleEn
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class SliderDiffCallback: DiffUtil.ItemCallback<SliderNews>() {

  override fun areItemsTheSame(oldItem: SliderNews, newItem: SliderNews): Boolean =
    oldItem.newsId == newItem.newsId

  override fun areContentsTheSame(oldItem: SliderNews, newItem: SliderNews): Boolean =
    oldItem == newItem
}
