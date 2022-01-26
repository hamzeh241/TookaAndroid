package ir.tdaapp.tooka.models

import com.google.gson.annotations.SerializedName

data class ConvertModel(
  @SerializedName("first_coin") var first: Coin,
  @SerializedName("second_coin") var second: Coin
)