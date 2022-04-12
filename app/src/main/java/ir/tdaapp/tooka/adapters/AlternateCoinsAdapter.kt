package ir.tdaapp.tooka.adapters

import ContextUtils
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemAlternateCoinsBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.LivePriceListResponse
import ir.tdaapp.tooka.models.PriceChange
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class AlternateCoinsAdapter(val action: (clicked: Coin, position: Int)->Unit):
  RecyclerView.Adapter<AlternateCoinsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemAlternateCoinsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlternateCoinsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  val differ = AsyncListDiffer(this, AlternateDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    setCorrectMargins(
      holder.binding.root as CardView,
      holder.binding.root.context,
      position,
      differ.currentList.size - 1
    )

    holder.binding.txtCoinName.text =
      when (ContextUtils.getLocale(holder.binding.root.context).toString()) {
        "en" -> data.name
        "fa" -> data.persianName
        else -> data.name
      }

    holder.binding.txtPriceTMN.text =
      StringBuilder(separatePrice(data.priceTMN.toInt())).toString()
    holder.binding.txtPriceUSD.text =
      StringBuilder(separatePrice(data.priceUSD.toFloat())).toString()
    holder.binding.txtCoinPercentage.text =
      StringBuilder(data.percentage.toString())
        .append(" ")
        .append("%")
        .toString()

    if (data.percentage > 0) {
      holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.green_400))
    } else if (data.percentage < 0) {
      holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.red_600))
    } else {
      holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.white_900))
    }

    val imageUrl = RetrofitClient.COIN_IMAGES + data.icon
    holder.binding.imageView glideUrl imageUrl
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isNullOrEmpty())
      super.onBindViewHolder(holder, position, payloads)

    if (payloads.any { it is PriceChange }) {
      val item = differ.currentList[position]

      if ((payloads.last() as PriceChange).ascend) {
        holder.binding.txtPriceUSD.setPrice(item.priceUSD)
        holder.binding.txtPriceUSD.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.GREEN,
          1000L
        )

        holder.binding.txtPriceTMN.setPrice(item.priceTMN)
        holder.binding.txtPriceTMN.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.GREEN,
          1000L
        )
      } else {
        holder.binding.txtPriceUSD.setPrice(item.priceUSD)
        holder.binding.txtPriceUSD.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.RED,
          1000L
        )

        holder.binding.txtPriceTMN.setPrice(item.priceTMN)
        holder.binding.txtPriceTMN.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.RED,
          1000L
        )
      }
    }
  }

  suspend fun notifyChanges(livePrice: LivePriceListResponse) {
    val position = GlobalScope.async {
      differ.currentList.singleOrNull { it.id == livePrice.id }.let { coin ->
        differ.currentList.indexOf(coin)
      }
    }

    with(position.await()) {
      if (this >= 0) {
        val ascend = differ.currentList[this].priceUSD < livePrice.priceUSD

        also {
          differ.currentList[it].priceUSD = livePrice.priceUSD
          differ.currentList[it].priceTMN = livePrice.priceTMN!!
          withContext(Dispatchers.Main) {
            notifyItemChanged(this@with, PriceChange(ascend))
          }
        }
      }
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

private class AlternateDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
