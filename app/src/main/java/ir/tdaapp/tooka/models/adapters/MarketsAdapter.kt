package ir.tdaapp.tooka.models.adapters

import android.annotation.SuppressLint
import android.content.res.Resources.Theme
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemMarketCoinsFlatBinding
import ir.tdaapp.tooka.databinding.ItemMarketCoinsGridBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * Adaptere marbut be liste arz ha dar MarketsFragment.kt
 */
class MarketsAdapter(val action: CoinCallback):
  RecyclerView.Adapter<MarketsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ViewBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == VIEW_TYPE_LINEAR)
          return ViewHolder(ItemMarketCoinsFlatBinding.inflate(layoutInflater, parent, false))
        else return ViewHolder(ItemMarketCoinsGridBinding.inflate(layoutInflater, parent, false))
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
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

      holder.binding.txtCoinName.text = formatSymbol(
        when (getCurrentLocale(holder.binding.root.context)) {
          "fa" -> data.persianName ?: data.name
          else -> data.name
        }, data.symbol
      )

      holder.binding.txtCoinPriceTMN.text =
        formatPrice(
          toPersianNumbers(separatePrice(data.priceTMN.toInt())),
          currency = holder.binding.root.context.getString(R.string.toomans)
        )

      holder.binding.txtCoinPriceUSD.text =
        formatPrice(
          toPersianNumbers(separatePrice(data.priceUSD)),
          holder.binding.root.context.getString(R.string.dollars)
        )

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
        "fa" -> data.persianName ?: data.name
        else -> data.name
      }

      holder.binding.txtPriceTMN.text =
        StringBuilder(separatePrice(data.priceTMN.toInt())).toString()
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

  /**
   * Dar inja viewType arz ha az flat be grid taghir mikonand
   */
  @SuppressLint("NotifyDataSetChanged")
  suspend fun changeViewType(viewType: Int) = withContext(Dispatchers.IO) {

    differ.currentList.forEach { coin ->
      coin.viewType = viewType
    }

    withContext(Dispatchers.Main) {
      notifyDataSetChanged()
    }
  }

  /**
   * Dar inja agar taghirate ma be gunei ast ke nabaiad kole
   * item ReDraw shavad mitavanim ba estefade az parametre
   * payloads taghirate morede nazare khod ra baraie
   * adapter ersal konim va mostaghiman be view dastresi dashte bashim
   * @param payloads taghirati ke ijad mikonim
   */
  override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.isNullOrEmpty())
      super.onBindViewHolder(holder, position, payloads)

    val typedValueUsd = TypedValue()
    val themeUsd: Theme = holder.binding.root.context.theme
    themeUsd.resolveAttribute(R.attr.colorOnSurface, typedValueUsd, true)
    @ColorInt val colorUsd = typedValueUsd.data

    val typedValue = TypedValue()
    val theme: Theme = holder.binding.root.context.theme
    theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
    @ColorInt val colorTmn = typedValue.data

    if (payloads.any { it is PriceChange }) {
      if (holder.binding is ItemMarketCoinsFlatBinding) {
        val item = differ.currentList[position]

        if ((payloads.last() as PriceChange).ascend) {
          holder.binding.txtCoinPriceUSD.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceUSD.toFloat())),
              holder.binding.root.context.getString(R.string.dollars)
            )
          holder.binding.txtCoinPriceUSD.animateColor(
            colorUsd,
            Color.GREEN,
            1000L
          )

          holder.binding.txtCoinPriceTMN.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceTMN.toInt())),
              currency = holder.binding.root.context.getString(R.string.toomans)
            )
          holder.binding.txtCoinPriceTMN.animateColor(
            colorTmn,
            Color.GREEN,
            1000L
          )
        } else {
          holder.binding.txtCoinPriceUSD.text = formatPrice(
            toPersianNumbers(separatePrice(item.priceUSD)),
            holder.binding.root.context.getString(R.string.dollars)
          )
          holder.binding.txtCoinPriceUSD.animateColor(
            colorUsd,
            Color.RED,
            1000L
          )

          holder.binding.txtCoinPriceTMN.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceTMN.toInt())),
              currency = holder.binding.root.context.getString(R.string.toomans)
            )
          holder.binding.txtCoinPriceTMN.animateColor(
            colorTmn,
            Color.RED,
            1000L
          )
        }
      } else {
        holder.binding as ItemMarketCoinsGridBinding
        val item = differ.currentList[position]

        if ((payloads.last() as PriceChange).ascend) {
          holder.binding.txtPriceUSD.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceUSD)),
              currency = holder.binding.root.context.getString(R.string.dollars)
            )
          holder.binding.txtPriceUSD.animateColor(
            colorUsd,
            Color.GREEN,
            1000L
          )

          holder.binding.txtPriceTMN.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceTMN.toInt())),
              currency = holder.binding.root.context.getString(R.string.toomans)
            )
          holder.binding.txtPriceTMN.animateColor(
            colorTmn,
            Color.GREEN,
            1000L
          )
        } else {
          holder.binding.txtPriceUSD.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceTMN)),
              currency = holder.binding.root.context.getString(R.string.dollars)
            )
          holder.binding.txtPriceUSD.animateColor(
            colorUsd,
            Color.RED,
            1000L
          )

          holder.binding.txtPriceTMN.text =
            formatPrice(
              toPersianNumbers(separatePrice(item.priceTMN.toInt())),
              currency = holder.binding.root.context.getString(R.string.toomans)
            )
          holder.binding.txtPriceTMN.animateColor(
            colorTmn,
            Color.RED,
            1000L
          )
        }
      }
    }
  }

  /**
   * Inja hengami ke gheimate arz taghir mikonad, bar asase id, arz ra peida mikonim
   * va taghirat ra e'mal mikonim
   */
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
          differ.currentList[it].priceTMN = livePrice.priceTMN
          withContext(Dispatchers.Main) {
            notifyItemChanged(this@with, PriceChange(ascend))
          }
        }
      }
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class MarketsDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
