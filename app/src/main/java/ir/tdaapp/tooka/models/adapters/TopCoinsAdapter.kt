package ir.tdaapp.tooka.models.adapters

import ContextUtils
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemSecondTopCoinBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.*
import ir.tdaapp.tooka.models.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class TopCoinsAdapter(val action: CoinCallback):
  RecyclerView.Adapter<TopCoinsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemSecondTopCoinBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSecondTopCoinBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, DiffCallback())

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val data = differ.currentList[position]

    holder.binding.root.addSpringAnimation()
    holder.binding.root.setOnClickListener {
      action.invoke(data, position)
    }

    setCorrectMargins(
      holder.binding.cardTopCoin,
      holder.binding.root.context,
      position,
      differ.currentList.size - 1
    )

    holder.binding.topCoinRoot.setOnClickListener {
      action.invoke(data, position)
    }

    holder.binding.txtCoinName.text =
      when (ContextUtils.getLocale(holder.binding.root.context).toString()) {
        "fa" -> data.persianName ?: data.name
        else -> data.name
      }

    holder.binding.txtPriceTMN.text =
      formatPrice(toPersianNumbers(separatePrice(data.priceTMN.toInt())), currency = holder.binding.root.context.getString(R.string.toomans))
    holder.binding.txtPriceUSD.text =
      formatPrice(toPersianNumbers(separatePrice(data.priceUSD)),currency = holder.binding.root.context.getString(R.string.dollars))
    holder.binding.txtCoinPercentage.text =
      StringBuilder(data.percentage.toString())
        .append(" ")
        .append("%")
        .toString()

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
    holder.binding.imgCoin glideUrl imageUrl

    setMiniChart(holder.binding.chart, data.ohlc.reversed())
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

    if (payloads.any { it is PriceChange }) {
      val item = differ.currentList[position]


      if ((payloads.last() as PriceChange).ascend) {
        holder.binding.txtPriceUSD.text =
          formatPrice(toPersianNumbers(separatePrice(item.priceUSD)), currency = holder.binding.root.context.getString(R.string.dollars))
        holder.binding.txtPriceUSD.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.GREEN,
          1000L
        )

        holder.binding.txtPriceTMN.text =
          formatPrice(toPersianNumbers(separatePrice(item.priceTMN.toInt())), currency = holder.binding.root.context.getString(R.string.toomans))
        holder.binding.txtPriceTMN.animateColor(
          holder.binding.root.resources.getColor(R.color.dark_blue_900),
          Color.GREEN,
          1000L
        )
      } else {
        holder.binding.txtPriceUSD.text =
          formatPrice(toPersianNumbers(separatePrice(item.priceUSD)), currency = holder.binding.root.context.getString(R.string.dollars))
        holder.binding.txtPriceUSD.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.RED,
          1000L
        )

        holder.binding.txtPriceTMN.text =
          formatPrice(toPersianNumbers(separatePrice(item.priceTMN.toInt())), currency = holder.binding.root.context.getString(R.string.toomans))
        holder.binding.txtPriceTMN.animateColor(
          holder.binding.root.resources.getColor(R.color.dark_blue_900),
          Color.RED,
          1000L
        )
      }
    }
  }

  /**
   * Inja hengami ke gheimate arz taghir mikonad, bar asase id, arz ra peida mikonim
   * va taghirat ra e'mal mikonim
   */
  suspend fun notifyChanges(livePrice: LivePriceListResponse) =
    withContext(Dispatchers.IO) {
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

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class DiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
