package ir.tdaapp.tooka.di

import android.content.Context
import ir.tdaapp.tooka.datasource.local.HomeLocalDataSource
import ir.tdaapp.tooka.datasource.remote.HomeRemoteDataSource
import ir.tdaapp.tooka.models.TookaDatabase
import ir.tdaapp.tooka.models.dao.HomeDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSourceModule = module {
  single { provideDatabase(androidContext()) }
  factory { provideHomeDao(get()) }

  factory { HomeRemoteDataSource(get()) }
  factory { HomeLocalDataSource(get()) }
}

private fun provideDatabase(context: Context): TookaDatabase =
  TookaDatabase.instance(context)

private fun provideHomeDao(db: TookaDatabase): HomeDao = db.homeDao()
