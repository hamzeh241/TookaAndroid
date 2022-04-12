package ir.tdaapp.tooka.adapters

import ContextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemBreakingCryptoNewsBinding
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.addSpringAnimation
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.getCurrentLocale

class NewsAdapter(val action: (clicked: News, position: Int)->Unit):
  RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemBreakingCryptoNewsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBreakingCryptoNewsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  val differ = AsyncListDiffer(this, NewsDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.addSpringAnimation()
    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    holder.binding.txtNewsTitle.text =
      when (ContextUtils.getLocale(holder.binding.root.context).toString()) {
        "en" -> data.titleEn
        "fa" -> data.titleFa
        else -> data.titleEn
      }

    val authorName = when(getCurrentLocale(holder.binding.root.context)){
      "fa" -> data.authorNameFa ?: data.authorNameEn
      else -> data.authorNameEn
    }
    holder.binding.txtNewsSubtitle.text =
        StringBuilder(authorName)
          .append(" ")
          .append(holder.binding.root.context.getString(R.string.bullet))
          .append(" ")
          .append(data.writeDate).toString()

    val imageUrl = StringBuilder(RetrofitClient.NEWS_IMAGES).append(data.imageUrl).toString()
    Glide.with(holder.binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_placeholder)
      .error(R.drawable.ic_broken_image)
      .into(holder.binding.image)
  }

  override fun getItemCount(): Int = differ.currentList.size
}

private class NewsDiffCallback: DiffUtil.ItemCallback<News>() {

  override fun areItemsTheSame(oldItem: News, newItem: News): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: News, newItem: News): Boolean = oldItem == newItem
}
