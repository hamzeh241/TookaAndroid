package ir.tdaapp.tooka.models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.databinding.ItemWalletBinding

typealias WalletsCallback = (index: Int, item: String)->Unit

class WalletsAdapter(val callback: WalletsCallback):
  RecyclerView.Adapter<WalletsAdapter.ViewHolder>() {

  companion object {
    const val TAG = "WalletsAdapter"
  }

  class ViewHolder private constructor(val binding: ItemWalletBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemWalletBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  val differ = AsyncListDiffer(this, object: DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }
  })

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = differ.currentList[position]
    holder.binding.txtWallet.text = item
    holder.binding.delete.setOnClickListener {
      callback(position, item)
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}