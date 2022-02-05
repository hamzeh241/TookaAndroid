package ir.tdaapp.tooka.util

import ContextUtils
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.adapters.TopCoinViewHolder
import ir.tdaapp.tooka.databinding.ItemSecondTopCoinBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.viewmodels.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
  single { RetrofitClient() }
  single { ContextUtils(androidContext()) }
  single { NewsViewModel(get()) }
  factory { CoinDetailsViewModel(get()) }
  single { RelatedNewsViewModel(get()) }
  single { RelatedCoinsViewModel(get()) }
  single { MainActivityViewModel(get()) }
  single { PortfolioViewModel(get()) }
  single { ManualBottomSheetViewModel(get()) }
  single { AutomaticBottomSheetViewModel(get()) }
  single { NotificationsViewModel(get()) }
  single { SharedViewModel(get()) }
  factory { CompareViewModel(get()) }
  factory { ConverterViewModel(get()) }
  factory { NewsDetailsViewModel(get()) }
  factory { SearchViewModel(get()) }
}