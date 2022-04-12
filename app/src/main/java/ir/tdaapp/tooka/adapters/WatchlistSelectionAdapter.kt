package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.databinding.ItemWatchlistSelectionBinding
import ir.tdaapp.tooka.models.Coin

class WatchlistSelectionAdapter: RecyclerView.Adapter<WatchlistSelectionAdapter.ViewHolder>() {

  companion object {
    const val TAG = "WatchlistSelectionAdapter"
  }

  init {
    setHasStableIds(true)
  }

  val differ = AsyncListDiffer(this, WatchlistDiffCallback())

  class ViewHolder(binding: ItemWatchlistSelectionBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemWatchlistSelectionBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
      object : ItemDetailsLookup.ItemDetails<String>() {
        override fun getPosition(): Int = adapterPosition
        override fun getSelectionKey(): String? = /* getItem(adapterPosition).id */ ""
      }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    TODO("Not yet implemented")
  }

  override fun getItemCount(): Int {
    TODO("Not yet implemented")
  }

  override fun getItemId(position: Int): Long = differ.currentList[position].id.toLong()
}

private class WatchlistDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}

class MyItemKeyProvider(private val adapter: WatchlistSelectionAdapter):
  ItemKeyProvider<Int>(SCOPE_CACHED) {

  override fun getPosition(key: Int): Int =
    adapter.differ.currentList.indexOfFirst { it.id == key }

  override fun getKey(position: Int): Int? =
    adapter.differ.currentList[position].id
}
