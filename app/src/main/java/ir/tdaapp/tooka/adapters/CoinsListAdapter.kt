package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.databinding.ItemSecondCoinsListBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.glideUrl

/**
 * Adaptere marbut be liste arz ha
 */
class CoinsListAdapter(val clickAction: CoinCallback):
  RecyclerView.Adapter<CoinsListAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSecondCoinsListBinding):
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSecondCoinsListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, CoinListDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = differ.currentList[position]

    val imageUrl = RetrofitClient.COIN_IMAGES + item.icon
    holder.binding.imgCoin glideUrl imageUrl
    holder.binding.txtCoinName.text = when (getCurrentLocale(holder.binding.root.context)) {
      "fa" -> item.persianName ?: item.name
      else -> item.name
    }
    holder.binding.txtCoinSymbol.text = item.symbol

    holder.binding.root.setOnClickListener {
      clickAction(item, position)
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class CoinListDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
