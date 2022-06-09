package ir.tdaapp.tooka.models.util

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
  single { ContextUtils(androidContext()) }
}
//
//val fragmentModule = module {
//  single { HomeFragment() }
//  single { MarketsFragment() }
//  single { PortfolioFragment() }
//  single { NewsFragment() }
//  single { LoginFragment() }
//  single { MainActivity() }
//}
//
