package ir.tdaapp.tooka.di

import android.content.Context
import ir.tdaapp.tooka.datasource.local.HomeLocalDataSource
import ir.tdaapp.tooka.datasource.local.MarketsLocalDataSource
import ir.tdaapp.tooka.datasource.remote.HomeRemoteDataSource
import ir.tdaapp.tooka.datasource.remote.MarketsRemoteDataSource
import ir.tdaapp.tooka.models.TookaDatabase
import ir.tdaapp.tooka.models.dao.HomeDao
import ir.tdaapp.tooka.models.dao.MarketsDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSourceModule = module {
  single { provideDatabase(androidContext()) }
  factory { provideHomeDao(get()) }
  factory { provideMarketsDao(get()) }

  factory { HomeRemoteDataSource(get()) }
  factory { HomeLocalDataSource(get()) }

  factory { MarketsLocalDataSource(get()) }
  factory { MarketsRemoteDataSource(get()) }
}

private fun provideDatabase(context: Context): TookaDatabase =
  TookaDatabase.instance(context)

private fun provideHomeDao(db: TookaDatabase): HomeDao = db.homeDao()
private fun provideMarketsDao(db: TookaDatabase): MarketsDao = db.marketsDao()
