package ir.tdaapp.tooka.models.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.ItemAlertBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.glideUrl
import ir.tdaapp.tooka.util.toPersianNumbers

/**
 * Adaptere marbut be liste price alert ha
 */
class PriceAlertAdapter(
  val removeClick: (PriceAlert, Int)->Unit,
  val switchClick: (PriceAlert, Int)->Unit
):
  RecyclerView.Adapter<PriceAlertAdapter.ViewHolder>() {

  /**
   * Fielde AsyncListDiffer baraie mohasebe asynce taghirate adapter
   */
  val differ = AsyncListDiffer(this, AlertDiffCallback())

  class ViewHolder private constructor(val binding: ItemAlertBinding):
    RecyclerView.ViewHolder(binding.root) {
    companion object {
      /**
       * Gereftane instance ViewHolder
       */
      fun from(parent: ViewGroup): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlertBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
    ViewHolder.from(parent)

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = differ.currentList[position]
    val imageUrl = RetrofitClient.COIN_IMAGES + item.coinIcon

    if (position == 0)
      holder.binding.root.apply {
        val params = layoutParams as RecyclerView.LayoutParams
        params.topMargin = 8
        layoutParams = params
      }

    holder.binding.imgCoin glideUrl imageUrl
    holder.binding.txtCoinName.setText(item.coinName)
    holder.binding.txtCoinSymbol.setText(item.coinSymbol)
    holder.binding.txtAlertDate.setText(item.date)
    holder.binding.txtAlertPrice.text =
      StringBuilder(item.isAscend.let {
        if (it)
          holder.binding.root.context.getString(R.string.above)
        else
          holder.binding.root.context.getString(R.string.below)
      })
        .append(" ")
        .append(toPersianNumbers(item.price.toInt().toString()))
        .append(" ")
        .append(item.isUsd.let {
          if (it)
            holder.binding.root.context.getString(R.string.dollars)
          else
            holder.binding.root.context.getString(R.string.toomans)
        })
        .toString()
    holder.binding.switchAlert.isChecked = item.isEnabled
    holder.binding.imgRemoveAlert.setOnClickListener {
      removeClick(item, position)
    }
    holder.binding.switchAlert.setOnCheckedChangeListener { buttonView, isChecked ->
      switchClick(item, position)
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

/**
 * Classe mohasebe konandeie taghirate adapter
 */
private class AlertDiffCallback: DiffUtil.ItemCallback<PriceAlert>() {

  override fun areItemsTheSame(oldItem: PriceAlert, newItem: PriceAlert): Boolean =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: PriceAlert, newItem: PriceAlert): Boolean =
    oldItem == newItem
}
