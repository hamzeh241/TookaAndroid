package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemSliderNewsBinding
import ir.tdaapp.tooka.models.SliderNews
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.getCurrentLocale
import java.lang.StringBuilder

class SliderNewsAdapter(val action: (clicked: SliderNews, position: Int)->Unit):
  RecyclerView.Adapter<SliderNewsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSliderNewsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSliderNewsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

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

private class SliderDiffCallback: DiffUtil.ItemCallback<SliderNews>() {

  override fun areItemsTheSame(oldItem: SliderNews, newItem: SliderNews): Boolean =
    oldItem.newsId == newItem.newsId

  override fun areContentsTheSame(oldItem: SliderNews, newItem: SliderNews): Boolean =
    oldItem == newItem
}
