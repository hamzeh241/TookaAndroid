package ir.tdaapp.tooka.models

import java.lang.StringBuilder

data class PortfolioInfo(
    val balances: List<Balance>,
    val total_balance_dollar: Double,
    val total_balance_tooman: Double
){
  data class Balance(
    val balance: Double,
    val coin_icon: String,
    val coin_id: Int,
    val coin_name: String,
    val coin_persian_name: String,
    val coin_symbol: String,
    val ohlc: List<Double>,
    val percentage: Double,
    val price_dollar: Double,
    val thumb_color: String,
    val price_toman: Double
  )
}