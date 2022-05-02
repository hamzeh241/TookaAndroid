package ir.tdaapp.tooka.models.dataclasses

data class PostPortfolioResponse(
  var platform: String,
  var status: Boolean,
  var balance: Double
)