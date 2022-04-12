package ir.tdaapp.tooka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.databinding.ItemAlertBinding
import ir.tdaapp.tooka.models.PriceAlert
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.glideUrl
import ir.tdaapp.tooka.util.toPersianNumbers

class PriceAlertAdapter(
  val removeClick: (PriceAlert, Int)->Unit,
  val switchClick: (PriceAlert, Int)->Unit
):
  RecyclerView.Adapter<PriceAlertAdapter.ViewHolder>() {

  val differ = AsyncListDiffer(this, AlertDiffCallback())

  class ViewHolder private constructor(val binding: ItemAlertBinding):
    RecyclerView.ViewHolder(binding.root) {
    companion object {
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
      StringBuilder(item.isAscend.let { if (it) "بالای" else "زیر" })
        .append(" ")
        .append(toPersianNumbers(item.price.toInt().toString()))
        .append(" ")
        .append(item.isUsd.let { if (it) "دلار" else "تومان" })
        .toString()
    holder.binding.switchAlert.isChecked = item.isEnabled
    holder.binding.imgRemoveAlert.setOnClickListener {
      removeClick(item, position)
    }
    holder.binding.switchAlert.setOnCheckedChangeListener { buttonView, isChecked ->
      switchClick(item,position)
    }
  }

  override fun getItemCount(): Int = differ.currentList.size
}

private class AlertDiffCallback: DiffUtil.ItemCallback<PriceAlert>() {

  override fun areItemsTheSame(oldItem: PriceAlert, newItem: PriceAlert): Boolean =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: PriceAlert, newItem: PriceAlert): Boolean =
    oldItem == newItem
}
