package ir.tdaapp.tooka.di

import ir.tdaapp.tooka.viewmodels.*
import org.koin.dsl.module

val viewModelModule = module {
  single { HomeViewModel(get()) }
  single { MarketsViewModel(get()) }
  single { MainActivityViewModel() }
  single { LoginActivityViewModel(get()) }
  single { PortfolioViewModel(get()) }
  single { NewsViewModel(get()) }
  single { RelatedNewsViewModel(get()) }
  single { RelatedCoinsViewModel(get()) }
  single { SharedViewModel(get()) }
  factory { NotificationsViewModel(get()) }
  factory { ManualBottomSheetViewModel(get()) }
  factory { AutomaticBottomSheetViewModel(get()) }
  factory { CoinDetailsViewModel(get()) }
  factory { PriceAlertViewModel(get()) }
  factory { PriceAlertListViewModel(get()) }
  factory { CoinsListViewModel(get()) }
  factory { AllNewsViewModel(get()) }
  factory { NewsDetailsViewModel(get()) }
  factory { SearchViewModel(get()) }
}