package ir.tdaapp.tooka.models

data class CompareModel(
    val first_coin: Coin,
    val second_coin: Coin,
    val time_frames:List<TimeFrameModel>
){
  data class Coin(
    val name:String,
    val symbol:String,
    val icon:String,
    val price_dollar: Double,
    val price_toman: Double,
    val stats: Stats
  )
}