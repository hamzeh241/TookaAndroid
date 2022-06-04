package ir.tdaapp.tooka.models.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemAlternateCoinsBinding
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.dataclasses.LivePriceListResponse
import ir.tdaapp.tooka.models.dataclasses.PriceChange
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * Be manzure rahat tar motavajeh shodane barnamenevis
 */
typealias CoinCallback = (clicked: Coin, position: Int)->Unit

/**
 * Adaptere marbut be coin haii ke mamulan asli nistand wa dar jahaie omumi estefade mihavand
 */
class AlternateCoinsAdapter( /*  */val action: CoinCallback):
  RecyclerView.Adapter<AlternateCoinsAdapter.ViewHolder>() {

  class ViewHolder private constructor(val binding: ItemAlternateCoinsBinding):
    RecyclerView.ViewHolder(binding.root) {

    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlternateCoinsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
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

    /* Baraie modiriate zabane farsi o englisi */
    holder.binding.txtCoinName.text =
      when (getCurrentLocale(holder.binding.root.context)) {
        "fa" -> data.persianName ?: data.name
        else -> data.name
      }

    holder.binding.txtPriceTMN.text =
        getCorrectNumberFormat(
          separatePrice(data.priceTMN.toInt()),
          holder.binding.root.context
        )
    holder.binding.txtPriceUSD.text =
        getCorrectNumberFormat(
          separatePrice(data.priceUSD),
          holder.binding.root.context
        )
    holder.binding.txtCoinPercentage.text =
      getCorrectNumberFormat(
        StringBuilder(data.percentage.toString())
          .append(" ")
          .append("%")
          .toString(),
        holder.binding.root.context
      )


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
        holder.binding.txtPriceUSD.setPrice(item.priceUSD)
        holder.binding.txtPriceUSD.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
          Color.GREEN,
          1000L
        )

        holder.binding.txtPriceTMN.setPrice(item.priceTMN.toInt())
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

        holder.binding.txtPriceTMN.setPrice(item.priceTMN.toInt())
        holder.binding.txtPriceTMN.animateColor(
          holder.binding.root.resources.getColor(R.color.gray_400),
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
private class AlternateDiffCallback: DiffUtil.ItemCallback<Coin>() {

  override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean = oldItem == newItem
}
