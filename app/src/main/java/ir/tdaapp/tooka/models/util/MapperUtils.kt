package ir.tdaapp.tooka.models.util

import ir.tdaapp.tooka.models.dataclasses.*

fun HomeNews.toNews(): News =
  News(
    authorId = author_id,
    id = news_id,
    authorNameEn = author_name_en,
    authorNameFa = author_name_fa,
    contentEn = "",
    contentFa = "",
    imageUrl = news_image_url,
    newsKind = news_kind,
    shortContentEn = "",
    shortContentFa = "",
    titleEn = news_title_en,
    titleFa = news_title_fa,
    url = news_url,
    writeDate = news_write_date
  )

fun News.toHomeNews() =
  HomeNews(
     author_id = authorId,
     author_name_en = authorNameEn,
     author_name_fa = authorNameFa,
     news_id = id,
     news_image_url = imageUrl,
     news_kind = newsKind,
     news_title_en = titleEn,
     news_title_fa = titleFa,
     news_url = url,
     news_write_date = writeDate
  )

fun Coin.toTopCoin() =
  TopCoin(
    id,
    name,
    persianName,
    priceUSD,
    priceTMN,
    symbol,
    nobitexId,
    icon,
    ohlc,
    rank,
    percentage,
  )

fun Coin.toWatchlist() =
  WatchlistCoin(
    id,
    name,
    persianName,
    priceUSD,
    priceTMN,
    symbol,
    nobitexId,
    icon,
    ohlc,
    rank,
    percentage,
  )

fun Coin.toGainersLosers() =
  GainersLosers(
    id,
    name,
    persianName,
    priceUSD,
    priceTMN,
    symbol,
    nobitexId,
    icon,
    ohlc,
    rank,
    percentage,
  )

fun TopCoin.toCoin(): Coin =
  Coin(
    id,
    name,
    persianName,
    priceUSD,
    priceTMN,
    symbol,
    nobitexId,
    icon,
    ohlc,
    rank,
    percentage,
    false,
    VIEW_TYPE_LINEAR,
    false
  )

fun GainersLosers.toCoin(): Coin =
  Coin(
    this.id,
    this.name,
    this.persianName,
    this.priceUSD,
    this.priceTMN,
    this.symbol,
    this.nobitexId,
    this.icon,
    this.ohlc,
    this.rank,
    this.percentage,
    false,
    VIEW_TYPE_LINEAR,
    false
  )

fun WatchlistCoin.toCoin(): Coin =
  Coin(
    this.id,
    this.name,
    this.persianName,
    this.priceUSD,
    this.priceTMN,
    this.symbol,
    this.nobitexId,
    this.icon,
    this.ohlc,
    this.rank,
    this.percentage,
    false,
    VIEW_TYPE_LINEAR,
    false
  )