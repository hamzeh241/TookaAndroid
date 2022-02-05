package ir.tdaapp.tooka.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsGridBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.models.LivePriceListResponse
import ir.tdaapp.tooka.models.PriceChange
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import kotlinx.coroutines.*
import java.lang.StringBuilder

class MarketsAdapter(val action: (clicked: Coin, position: Int)->Unit):
  RecyclerView.Adapter<MarketsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ViewBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      fun from(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_LINEAR)
          return ViewHolder(ItemMarketCoinsFlatBinding.inflate(layoutInflater, parent, false))
        else return ViewHolder(ItemMarketCoinsGridBinding.inflate(layoutInflater, parent, false))
      }
    }
  }

  val differ = AsyncListDiffer(this, MarketsDiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent, viewType)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    if (data.viewType == VIEW_TYPE_LINEAR) {
      holder.binding as ItemMarketCoinsFlatBinding

      holder.binding.watchlistIndicator.visibility = when (data.isWatchlist) {
        true -> View.VISIBLE
        false -> View.GONE
      }
      holder.binding.txtCoinName.text = when (getCurrentLocale(holder.binding.root.context)) {
        "en" -> data.name
        "fa" -> {
          if (data.persianName != null) data.persianName
          else data.name
        }
        else -> data.name
      }
      holder.binding.txtCoinSymbol.text = data.symbol
      holder.binding.txtCoinPriceTMN.text =
        StringBuilder(separatePrice(data.priceTMN.toFloat())).append(" ")
          .append(holder.binding.root.context.getString(R.string.toomans)).toString()

      holder.binding.txtCoinPriceUSD.text =
        StringBuilder(separatePrice(data.priceUSD.toFloat())).append(" ")
          .append(holder.binding.root.context.getString(R.string.dollars)).toString()

      holder.binding.txtCoinPercentage.text =
        StringBuilder(data.percentage.toString()).append(" %").toString()

      if (data.percentage > 0) {
        holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.green_400))
        holder.binding.imgAscend.setColorFilter(holder.binding.root.resources.getColor(R.color.green_400))
        holder.binding.imgAscend.setImageResource(R.drawable.ic_ascend)
      } else if (data.percentage < 0) {
        holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.red_600))
        holder.binding.imgAscend.setColorFilter(holder.binding.root.resources.getColor(R.color.red_600))
        holder.binding.imgAscend.setImageResource(R.drawable.ic_descend)
      } else {
        holder.binding.txtCoinPercentage.setTextColor(holder.binding.root.resources.getColor(R.color.white_900))
        holder.binding.imgAscend.setColorFilter(holder.binding.root.resources.getColor(R.color.white_900))
        holder.binding.imgAscend.setImageResource(R.drawable.ic_remove)
      }

      val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

      Glide.with(holder.binding.root.context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_circle_24)
        .into(holder.binding.imgCoin)

    } else {
      holder.binding as ItemMarketCoinsGridBinding

      holder.binding.txtCoinName.text = when (getCurrentLocale(holder.binding.root.context)) {
        "en" -> data.name
        "fa" -> {
          if (data.persianName != null) data.persianName
          else data.name
        }
        else -> data.name
      }

      holder.binding.txtPriceTMN.text =
        StringBuilder(separatePrice(data.priceTMN.toFloat())).toString()
      holder.binding.txtPriceUSD.text =
        StringBuilder(separatePrice(data.priceUSD.toFloat())).toString()

      holder.binding.txtCoinPercentage.text = data.percentage.toString()

      val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

      Glide.with(holder.binding.root.context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_circle_24)
        .into(holder.binding.imgCoin)
    }
  }

  override fun getItemViewType(position: Int): Int = differ.currentList[position].viewType

  @SuppressLint("NotifyDataSetChanged")
  suspend fun changeViewType(viewType: Int) = withContext(Dispatchers.IO) {

    differ.currentList.forEach { coin ->
      coin.viewType = viewType
    }

    withContext(Dispatchers.Main) {
      notifyDataSetChanged()
    }
  }

  @DelicateCoroutinesApi
  override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isNullOrEmpty())
      super.onBindViewHolder(holder, position, payloads)

    if (payloads.any { it is PriceChange }) {
      if (holder.binding is ItemMarketCoinsFlatBinding) {
        val item = differ.currentList[position]

        if ((payloads.last() as PriceChange).ascend) {
          holder.binding.txtCoinPriceUSD.setPrice(item.priceUSD)
          holder.binding.txtCoinPriceUSD.animateColor(
            holder.binding.root.resources.getColor(R.color.gray_400),
            Color.GREEN,
            1000L
          )

          holder.binding.txtCoinPriceTMN.setPrice(item.priceTMN)
          holder.binding.txtCoinPriceTMN.animateColor(
            holder.binding.root.resources.getColor(R.color.dark_blue_900),
            Color.GREEN,
            1000L
          )
        } else {
          holder.binding.txtCoinPriceUSD.setPrice(item.priceUSD)
          holder.binding.txtCoinPriceUSD.animateColor(
            holder.binding.root.resources.getColor(R.color.gray_400),
            Color.RED,
            1000L
          )

          holder.binding.txtCoinPriceTMN.setPrice(item.priceTMN)
          holder.binding.txtCoinPriceTMN.animateColor(
            holder.binding.root.resources.getColor(R.color.dark_blue_900),
            Color.RED,
            1000L
          )
        }
      } else {
        holder.binding as ItemMarketCoinsGridBinding
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
            holder.binding.root.resources.getColor(R.color.dark_blue_900),
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
            holder.binding.root.resources.getColor(R.color.dark_blue_900),
            Color.RED,
            1000L
          )
        }
      }
    }
  }

  suspend fun notifyChanges(livePrice: LivePriceListResponse) = withContext(Dispatchers.IO) {
    val position = async {
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

private class MarketsDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
