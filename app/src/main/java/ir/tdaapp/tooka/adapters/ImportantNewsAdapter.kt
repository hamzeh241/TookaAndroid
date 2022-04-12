package ir.tdaapp.tooka.adapters

import ContextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemImportantNewsBinding
import ir.tdaapp.tooka.models.News
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.setNewsMargin

class ImportantNewsAdapter(val action: (clicked: News, position: Int)->Unit):
  RecyclerView.Adapter<ImportantNewsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemImportantNewsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImportantNewsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  val differ = AsyncListDiffer(this, ImportantNewsDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    setNewsMargin(holder.binding, position)

    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    holder.binding.txtNewsTitle.text =
      when (ContextUtils.getLocale(holder.binding.root.context).toString()) {
        "en" -> data.titleEn
        "fa" -> data.titleFa
        else -> data.titleEn
      }

    val author = when (getCurrentLocale(holder.binding.root.context)) {
      "fa" -> {
        if (data.authorNameFa != null)
          data.authorNameFa
        else data.authorNameEn
      }
      "en" -> data.authorNameEn
      else -> data.authorNameEn
    }

    holder.binding.txtNewsSubtitle.text = StringBuilder(author)
      .append(" ")
      .append(holder.binding.root.context.getString(R.string.bullet))
      .append(" ")
      .append(data.writeDate).toString()

    val imageUrl = StringBuilder(RetrofitClient.NEWS_IMAGES).append(data.imageUrl).toString()
    Glide.with(holder.binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_placeholder)
      .error(R.drawable.ic_broken_image)
      .into(holder.binding.imgNews)
  }

  override fun getItemCount(): Int = differ.currentList.size
}

private class ImportantNewsDiffCallback: DiffUtil.ItemCallback<News>() {

  override fun areItemsTheSame(oldItem: News, newItem: News): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: News, newItem: News): Boolean = oldItem == newItem
}
