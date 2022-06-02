package ir.tdaapp.tooka.models.util

import ir.tdaapp.tooka.ui.fragments.HomeFragment
import ir.tdaapp.tooka.ui.fragments.MarketsFragment
import ir.tdaapp.tooka.ui.fragments.NewsFragment
import ir.tdaapp.tooka.ui.fragments.PortfolioFragment
import ir.tdaapp.tooka.ui.fragments.login.LoginFragment
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
  single { ContextUtils(androidContext()) }
}

val fragmentModule = module {
  single { HomeFragment() }
  single { MarketsFragment() }
  single { PortfolioFragment() }
  single { NewsFragment() }
  single { LoginFragment() }
}

