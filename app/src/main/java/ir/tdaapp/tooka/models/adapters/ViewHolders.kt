package ir.tdaapp.tooka.models.adapters

import android.view.View
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.*
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.*
import java.math.BigDecimal
import java.text.DecimalFormat

class AlternateCoinsViewHolder(val binding: ItemAlternateCoinsBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Coin> {

  override fun bind(
    data: Coin,
    callback: TookaAdapter.Callback<Coin>,
    longCallback: TookaAdapter.LongCallback<Coin>?,
    models: ArrayList<Coin>,
    position: Int
  ) {
    setCorrectMargins(binding.root as CardView, binding.root.context, position, models.size - 1)

    binding.txtCoinName.text = when (ContextUtils.getLocale(binding.root.context).toString()) {
      "en" -> data.name
      "fa" -> data.persianName
      else -> data.name
    }

    binding.txtPriceTMN.text = StringBuilder(separatePrice(data.priceTMN.toFloat())).toString()
    binding.txtPriceUSD.text = StringBuilder(separatePrice(data.priceUSD.toFloat())).toString()
    binding.txtCoinPercentage.text =
      StringBuilder(data.percentage.toString())
        .append(" ")
        .append("%")
        .toString()

    if (data.percentage > 0) {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.green_400))
    } else if (data.percentage < 0) {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.red_600))
    } else {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.white_900))
    }

    val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

    Glide.with(binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_baseline_circle_24)
      .into(binding.imageView)

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }
    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class ImportantNewsViewHolder(val binding: ItemImportantNewsBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<News> {

  override fun bind(
    data: News,
    callback: TookaAdapter.Callback<News>,
    longCallback: TookaAdapter.LongCallback<News>?,
    models: ArrayList<News>,
    position: Int
  ) {
    binding.txtNewsTitle.text = when (ContextUtils.getLocale(binding.root.context).toString()) {
      "en" -> data.titleEn
      "fa" -> data.titleFa
      else -> data.titleEn
    }
    var author = when (getCurrentLocale(binding.root.context)) {
      "fa" -> {
        if (data.authorNameFa != null)
          data.authorNameFa
        else data.authorNameEn
      }
      "en" -> data.authorNameEn
      else -> data.authorNameEn
    }

    binding.txtNewsSubtitle.text = StringBuilder(author)
      .append(" ")
      .append(binding.root.context.getString(R.string.bullet))
      .append(" ")
      .append(data.writeDate).toString()

    binding.importantNewsRoot.setOnClickListener {
      callback.onClick(data, position)
    }
  }
}

class TopCoinViewHolder(val binding: ItemSecondTopCoinBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Coin> {

  override fun bind(
    data: Coin,
    callback: TookaAdapter.Callback<Coin>,
    longCallback: TookaAdapter.LongCallback<Coin>?,
    models: ArrayList<Coin>,
    position: Int
  ) {
    setCorrectMargins(binding.cardTopCoin, binding.root.context, position, models.size - 1)

    binding.topCoinRoot.setOnClickListener {
      callback.onClick(data, position)
    }
    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }

    binding.txtCoinName.text = when (ContextUtils.getLocale(binding.root.context).toString()) {
      "en" -> data.name
      "fa" -> data.persianName
      else -> data.name
    }

    binding.txtPriceTMN.text = StringBuilder(separatePrice(data.priceTMN.toFloat())).toString()
    binding.txtPriceUSD.text =
      StringBuilder(separatePrice(data.priceUSD.toInt().toFloat())).toString()
    binding.txtCoinPercentage.text =
      StringBuilder(data.percentage.toString())
        .append(" ")
        .append("%")
        .toString()

    if (data.percentage > 0) {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.green_400))
      binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.green_400))
      binding.imgAscend.setImageResource(R.drawable.ic_ascend)
    } else if (data.percentage < 0) {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.red_600))
      binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.red_600))
      binding.imgAscend.setImageResource(R.drawable.ic_descend)
    } else {
      binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.white_900))
      binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.white_900))
      binding.imgAscend.setImageResource(R.drawable.ic_remove)
    }

    val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

    Glide.with(binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_baseline_circle_24)
      .into(binding.imgCoin)

    setMiniChart(binding.chart, data.ohlc.reversed())
  }
}

class PlatformsViewHolder(val binding: ItemPlatformBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Platform> {

  override fun bind(
    data: Platform,
    callback: TookaAdapter.Callback<Platform>,
    longCallback: TookaAdapter.LongCallback<Platform>?,
    models: ArrayList<Platform>,
    position: Int
  ) {
    setCorrectMargins(binding.root, binding.root.context, position, models.size - 1)

    if (data.isSelected)
      binding.root.apply {
        setCardBackgroundColor(binding.root.context.resources.getColor(R.color.white_300))
        strokeWidth = 3
      }

    binding.txtPlatformName.text = data.name

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }

    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }

}

class MarketCoinsViewHolder(val binding: ViewBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Coin> {

  override fun bind(
    data: Coin,
    callback: TookaAdapter.Callback<Coin>,
    longCallback: TookaAdapter.LongCallback<Coin>?,
    models: ArrayList<Coin>,
    position: Int
  ) {
    val binding: ViewBinding

    if (data.viewType == VIEW_TYPE_LINEAR) {
      binding = this.binding as ItemMarketCoinsFlatBinding

      binding.watchlistIndicator.visibility = when (data.isWatchlist) {
        true -> View.VISIBLE
        false -> View.GONE
      }
      binding.txtCoinName.text = when (getCurrentLocale(binding.root.context)) {
        "en" -> data.name
        "fa" -> {
          if (data.persianName != null) data.persianName
          else data.name
        }
        else -> data.name
      }
      binding.txtCoinPriceTMN.text =
        StringBuilder(separatePrice(data.priceTMN.toFloat())).append(" ")
          .append(binding.root.context.getString(R.string.toomans)).toString()

      binding.txtCoinPriceUSD.text =
        StringBuilder(separatePrice(data.priceUSD.toFloat())).append(" ")
          .append(binding.root.context.getString(R.string.dollars)).toString()

      binding.txtCoinPercentage.text =
        StringBuilder(data.percentage.toString()).append(" %").toString()

      if (data.percentage > 0) {
        binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.green_400))
        binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.green_400))
        binding.imgAscend.setImageResource(R.drawable.ic_ascend)
      } else if (data.percentage < 0) {
        binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.red_600))
        binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.red_600))
        binding.imgAscend.setImageResource(R.drawable.ic_descend)
      } else {
        binding.txtCoinPercentage.setTextColor(binding.root.resources.getColor(R.color.white_900))
        binding.imgAscend.setColorFilter(binding.root.resources.getColor(R.color.white_900))
        binding.imgAscend.setImageResource(R.drawable.ic_remove)
      }

      val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

      Glide.with(binding.root.context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_circle_24)
        .into(binding.imgCoin)

    } else {
      binding = this.binding as ItemMarketCoinsGridBinding

      binding.txtCoinName.text = when (getCurrentLocale(binding.root.context)) {
        "en" -> data.name
        "fa" -> {
          if (data.persianName != null) data.persianName
          else data.name
        }
        else -> data.name
      }

      binding.txtPriceTMN.text = StringBuilder(separatePrice(data.priceTMN.toFloat())).toString()
      binding.txtPriceUSD.text = StringBuilder(separatePrice(data.priceUSD.toFloat())).toString()

      binding.txtCoinPercentage.text = data.percentage.toString()

      val imageUrl = RetrofitClient.COIN_IMAGES + data.icon

      Glide.with(binding.root.context)
        .load(imageUrl)
        .placeholder(R.drawable.ic_baseline_circle_24)
        .into(binding.imgCoin)
    }

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }

    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class NotificationsViewHolder(val binding: ItemNotificationBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Notification> {
  override fun bind(
    data: Notification,
    callback: TookaAdapter.Callback<Notification>,
    longCallback: TookaAdapter.LongCallback<Notification>?,
    models: ArrayList<Notification>,
    position: Int
  ) {
    binding.txtTitle.text = when (getCurrentLocale(binding.root.context)) {
      "fa" -> {
        if (data.titleFa != null)
          data.titleFa
        else data.titleEn
      }
      else -> data.titleEn
    }
    binding.txtSubtitle.text = when (getCurrentLocale(binding.root.context)) {
      "fa" -> {
        if (data.descFa != null)
          data.descFa
        else data.descEn
      }
      else -> data.descEn
    }

    binding.root.setOnClickListener({
      callback.onClick(data, position)
    })
    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class PortfolioBalanceViewHolder(val binding: ItemPortfolioBalanceBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<PortfolioInfo.Balance> {
  override fun bind(
    data: PortfolioInfo.Balance,
    callback: TookaAdapter.Callback<PortfolioInfo.Balance>,
    longCallback: TookaAdapter.LongCallback<PortfolioInfo.Balance>?,
    models: ArrayList<PortfolioInfo.Balance>,
    position: Int
  ) {
    val locale = getCurrentLocale(binding.root.context)
    binding.txtCoinName.text = locale.let {
      when (it) {
        "fa" -> {
          if (data.coin_persian_name != null) data.coin_persian_name
          else data.coin_name
        }
        else -> data.coin_name
      }
    }
    val dec = DecimalFormat("#,###.000000")
    binding.txtCapital.text =
      StringBuilder(dec.format(data.balance).toString())
        .append(" ")
        .append(data.coin_symbol)

    val a = BigDecimal(data.price_toman)
    val b = BigDecimal(data.price_dollar)

    binding.root.context.run {
      binding.txtPriceTMN.text =
        formatPrice(
          getCorrectNumberFormat(separatePrice(a.toInt().toFloat()), this),
          getString(R.string.toomans)
        )

      binding.txtPriceUSD.text =
        formatPrice(
          getCorrectNumberFormat(b.toFloat(), this),
          getString(R.string.dollars)
        )

      val imageUrl = RetrofitClient.COIN_IMAGES + data.coin_icon
      Glide.with(this)
        .load(imageUrl)
        .into(binding.imgCoin)
    }

  }

}

class PortfolioCoinsViewHolder(val binding: ItemPortfolioBalanceCoinBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<PortfolioInfo.Balance> {

  override fun bind(
    data: PortfolioInfo.Balance,
    callback: TookaAdapter.Callback<PortfolioInfo.Balance>,
    longCallback: TookaAdapter.LongCallback<PortfolioInfo.Balance>?,
    models: ArrayList<PortfolioInfo.Balance>,
    position: Int
  ) {

    setCorrectMargins(binding.root, binding.root.context, position, models.size - 1)
    binding.txtCoinName.text = when (getCurrentLocale(binding.root.context)) {
      "fa" -> {
        if (data.coin_persian_name != null)
          data.coin_persian_name
        else data.coin_name
      }
      else -> data.coin_name
    }

    val dec = DecimalFormat("#,###.000000")
    binding.txtCapital.text =
      StringBuilder(dec.format(data.balance).toString())
        .append(" ")
        .append(data.coin_symbol)
    binding.txtCoinSymbol.text = data.coin_symbol
    var a = BigDecimal(data.price_toman)
    binding.txtPriceTMN.text =
      StringBuilder(separatePrice(a.toInt().toFloat()))
        .append(" تومان")
        .toString()

    val imageUrl = RetrofitClient.COIN_IMAGES + data.coin_icon
    Glide.with(binding.root.context)
      .load(imageUrl)
      .into(binding.imgCoin)

    setMiniChart(binding.chart, data.ohlc.reversed())
  }
}

class SliderNewsViewHolder(val binding: ItemSliderNewsBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<SliderNews> {
  override fun bind(
    data: SliderNews,
    callback: TookaAdapter.Callback<SliderNews>,
    longCallback: TookaAdapter.LongCallback<SliderNews>?,
    models: ArrayList<SliderNews>,
    position: Int
  ) {

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }

    val imageUrl = StringBuilder(RetrofitClient.NEWS_IMAGES).append(data.imageUrl)
    Glide.with(binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_placeholder)
      .error(R.drawable.ic_broken_image)
      .into(binding.image)

    binding.txtNewsTitle.text = when (getCurrentLocale(binding.root.context)) {
      "en" -> data.titleEn
      "fa" -> data.titleFa
      else -> data.titleEn
    }
  }
}

class BreakingAndCryptoNewsViewHolder(val binding: ItemBreakingCryptoNewsBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<News> {
  override fun bind(
    data: News,
    callback: TookaAdapter.Callback<News>,
    longCallback: TookaAdapter.LongCallback<News>?,
    models: ArrayList<News>,
    position: Int
  ) {

    binding.txtNewsTitle.text = when (ContextUtils.getLocale(binding.root.context).toString()) {
      "en" -> data.titleEn
      "fa" -> data.titleFa
      else -> data.titleEn
    }
    binding.txtNewsSubtitle.text = when (ContextUtils.getLocale(binding.root.context).toString()) {
      "en" -> StringBuilder(data.authorNameEn)
        .append(" ")
        .append(binding.root.context.getString(R.string.bullet))
        .append(" ")
        .append(data.writeDate).toString()

      "fa" -> StringBuilder(data.authorNameFa)
        .append(" ")
        .append(binding.root.context.getString(R.string.bullet))
        .append(" ")
        .append(data.writeDate).toString()

      else -> StringBuilder(data.authorNameEn)
        .append(" ")
        .append(binding.root.context.getString(R.string.bullet))
        .append(" ")
        .append(data.writeDate).toString()
    }

    val imageUrl = RetrofitClient.NEWS_IMAGES + data.imageUrl
    Glide.with(binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_placeholder)
      .error(R.drawable.ic_broken_image)
      .into(binding.image)

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }
    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class WalletsViewHolder(val binding: ItemWalletAddressBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<String> {
  override fun bind(
    data: String,
    callback: TookaAdapter.Callback<String>,
    longCallback: TookaAdapter.LongCallback<String>?,
    models: ArrayList<String>,
    position: Int
  ) {

    binding.txtWalletAddress.text = data

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }

    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class TimeFramesViewHolder(val binding: ItemTimeFrameBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<TimeFrameModel> {
  override fun bind(
    data: TimeFrameModel,
    callback: TookaAdapter.Callback<TimeFrameModel>,
    longCallback: TookaAdapter.LongCallback<TimeFrameModel>?,
    models: ArrayList<TimeFrameModel>,
    position: Int
  ) {

    setCorrectMargins(binding.root, binding.root.context, position, models.size - 1)

    if (models.get(position).isSelected) {
      binding.root.setCardBackgroundColor(binding.root.context.getColor(R.color.blue_200))
      binding.txtTimeFrameCaption.setTextColor(binding.root.context.getColor(R.color.white))
      binding.txtTimeFrameNum.setTextColor(binding.root.context.getColor(R.color.white))
    } else {
      binding.root.setCardBackgroundColor(binding.root.context.getColor(R.color.white))
      binding.txtTimeFrameCaption.setTextColor(binding.root.context.getColor(R.color.black))
      binding.txtTimeFrameNum.setTextColor(binding.root.context.getColor(R.color.black))
    }

    binding.txtTimeFrameNum.text = data.num
    binding.txtTimeFrameCaption.text = when (getCurrentLocale(binding.root.context)) {
      "en" -> {
        data.captionEN
      }
      "fa" -> {
        data.captionFa
      }
      else -> {
        data.captionEN
      }
    }

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }
  }

  fun selectPosition(model: TimeFrameModel) {
    model.isSelected = true
  }

}

class CoinsViewHolder(val binding: ItemCoinsListBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<Coin> {
  override fun bind(
    data: Coin,
    callback: TookaAdapter.Callback<Coin>,
    longCallback: TookaAdapter.LongCallback<Coin>?,
    models: ArrayList<Coin>,
    position: Int
  ) {

    binding.txtCoinName.text = when (getCurrentLocale(binding.root.context)) {
      "fa" -> {
        if (data.persianName != null)
          data.persianName
        else data.name
      }
      "en" -> data.name
      else -> data.name
    }
    binding.txtCoinSymbol.text = data.symbol

    val imageUrl = RetrofitClient.COIN_IMAGES + data.icon
    Glide.with(binding.root.context)
      .load(imageUrl)
      .placeholder(R.drawable.ic_baseline_circle_24)
      .into(binding.imgCoin)

    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }
    binding.root.setOnLongClickListener {
      longCallback?.onLongClick(data, position)
      true
    }
  }
}

class SortOptionsViewHolder(val binding: ItemSortOptionBinding):
  RecyclerView.ViewHolder(binding.root), TookaAdapter.Binder<SortModel> {
  override fun bind(
    data: SortModel,
    callback: TookaAdapter.Callback<SortModel>,
    longCallback: TookaAdapter.LongCallback<SortModel>?,
    models: ArrayList<SortModel>,
    position: Int
  ) {
    binding.txtSortOptionsTitle.text = when (getCurrentLocale(binding.root.context)) {
      "fa" -> if (data.nameFa != null) data.nameFa else data.nameEn
      else -> data.nameEn
    }

    @DrawableRes val image = if (!data.isSelected) R.drawable.ic_remove else {
      if (data.isAscend) R.drawable.ic_arrow_ascend else
        R.drawable.ic_arrow_descend
    }

    val drawable = ResourcesCompat.getDrawable(binding.root.resources, image, null)
    drawable.let {
      val dimen16 = binding.root.resources.getDimension(R.dimen.size16)
      it?.setBounds(0, 0, dimen16.toInt(), dimen16.toInt())
    }

    when (getCurrentLocale(binding.root.context)) {
      "fa" ->
        binding.txtSortOptionsTitle.setCompoundDrawables(
          drawable,
          null,
          null,
          null
        )
      else ->
        binding.txtSortOptionsTitle.setCompoundDrawablesWithIntrinsicBounds(
          null,
          null,
          drawable,
          null
        )
    }



    binding.root.setOnClickListener {
      callback.onClick(data, position)
    }
  }
}

