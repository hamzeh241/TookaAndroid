package ir.tdaapp.tooka.models.dataclasses

data class Stats(
    val coin_id: Int,
    val create_date: Int,
    val stat_24h_volume: Double,
    val stat_circulating_supply: Double,
    val stat_market_cap: Double,
    val stat_max_supply: Double,
    val stat_rank: Double,
    val stat_roi: Double,
    val stat_total_supply: Double
)