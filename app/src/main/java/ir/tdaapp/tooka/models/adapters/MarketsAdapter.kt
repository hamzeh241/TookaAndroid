package ir.tdaapp.tooka.models.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
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
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.dataclasses.PriceChange
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.*
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

      holder.binding.run {
        watchlistIndicator.setImageResource(
          when (data.isWatchlist) {
            true -> R.drawable.ic_baseline_bookmark_24
            false -> R.drawable.ic_baseline_bookmark_border_24
          }
        )

        txtCoinName.text = formatSymbol(
          when (getCurrentLocale(root.context)) {
            "fa" -> data.persianName ?: data.name
            else -> data.name
          }, data.symbol
        )

        txtCoinPriceTMN.text =
          formatPrice(
            getCorrectNumberFormat(
              separatePrice(data.priceTMN.toInt()),
              root.context
            ),
            currency = root.context.getString(R.string.toomans)
          )

        txtCoinPriceUSD.text =
          formatPrice(
            getCorrectNumberFormat(separatePrice(data.priceUSD), root.context),
            root.context.getString(R.string.dollars)
          )

        txtCoinPercentage.text =
          StringBuilder(
            getCorrectNumberFormat(
              data.percentage.toString(),
              root.context
            )
          ).append(" %").toString()

        if (data.percentage > 0) {
          txtCoinPercentage.setTextColor(root.resources.getColor(R.color.green_400))
          imgAscend.setColorFilter(root.resources.getColor(R.color.green_400))
          imgAscend.setImageResource(R.drawable.ic_ascend)
        } else if (data.percentage < 0) {
          txtCoinPercentage.setTextColor(root.resources.getColor(R.color.red_600))
          imgAscend.setColorFilter(root.resources.getColor(R.color.red_600))
          imgAscend.setImageResource(R.drawable.ic_descend)
        } else {
          txtCoinPercentage.setTextColor(root.resources.getColor(R.color.white_900))
          imgAscend.setColorFilter(root.resources.getColor(R.color.white_900))
          imgAscend.setImageResource(R.drawable.ic_remove)
        }

        val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

        Glide.with(root.context)
          .load(imageUrl)
          .placeholder(R.drawable.ic_baseline_circle_24)
          .into(imgCoin)

      }
    } else {
      holder.binding as ItemMarketCoinsGridBinding

      holder.binding.run {
        txtCoinName.text = when (getCurrentLocale(root.context)) {
          "fa" -> data.persianName ?: data.name
          else -> data.name
        }

        txtPriceTMN.text =
          formatPrice(
            getCorrectNumberFormat(
              separatePrice(data.priceTMN.toInt()),
              root.context
            ),
            currency = root.context.getString(R.string.toomans)
          )

        txtPriceUSD.text =
          formatPrice(
            getCorrectNumberFormat(separatePrice(data.priceUSD), root.context),
            root.context.getString(R.string.dollars)
          )

        txtCoinPercentage.text = getCorrectNumberFormat(
          data.percentage.toString(),
          root.context
        )

        val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

        Glide.with(root.context)
          .load(imageUrl)
          .placeholder(R.drawable.ic_baseline_circle_24)
          .into(imgCoin)
      }
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

    @ColorInt val colorUsd = getAttributeColor(holder.binding.root.context, R.attr.colorOnSurface)
    @ColorInt val colorTmn = getAttributeColor(holder.binding.root.context, R.attr.colorOnSurface)

    if (payloads.any { it is PriceChange }) {
      if (holder.binding is ItemMarketCoinsFlatBinding) {
        val item = differ.currentList[position]

        holder.binding.run {
          txtCoinPriceUSD.text =
            formatPrice(
              getCorrectNumberFormat(
                separatePrice(item.priceUSD.toFloat()),
                root.context
              ),
              root.context.getString(R.string.dollars)
            )
          txtCoinPriceTMN.text =
            formatPrice(
              getCorrectNumberFormat(
                separatePrice(item.priceTMN.toInt()),
                root.context
              ),
              currency = root.context.getString(R.string.toomans)
            )
          if ((payloads.last() as PriceChange).ascend) {
            txtCoinPriceUSD.animateColor(
              colorUsd,
              Color.GREEN,
              1000L
            )

            txtCoinPriceTMN.animateColor(
              colorTmn,
              Color.GREEN,
              1000L
            )
          } else {
            txtCoinPriceUSD.animateColor(
              colorUsd,
              Color.RED,
              1000L
            )
            txtCoinPriceTMN.animateColor(
              colorTmn,
              Color.RED,
              1000L
            )
          }
        }
      } else {
        holder.binding as ItemMarketCoinsGridBinding
        val item = differ.currentList[position]

        holder.binding.run {
          txtPriceUSD.text =
            formatPrice(
              getCorrectNumberFormat(
                separatePrice(item.priceUSD.toFloat()),
                root.context
              ),
              root.context.getString(R.string.dollars)
            )
          txtPriceTMN.text =
            formatPrice(
              getCorrectNumberFormat(
                separatePrice(item.priceTMN.toInt()),
                root.context
              ),
              currency = root.context.getString(R.string.toomans)
            )
          if ((payloads.last() as PriceChange).ascend) {
            txtPriceUSD.animateColor(
              colorUsd,
              Color.GREEN,
              1000L
            )

            txtPriceTMN.animateColor(
              colorTmn,
              Color.GREEN,
              1000L
            )
          } else {
            txtPriceUSD.animateColor(
              colorUsd,
              Color.RED,
              1000L
            )

            txtPriceTMN.animateColor(
              colorTmn,
              Color.RED,
              1000L
            )
          }
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
