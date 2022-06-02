package ir.tdaapp.tooka.di

import ir.tdaapp.tooka.models.repositories.HomeRepository
import org.koin.dsl.module

val repositoryModule = module {
  factory { HomeRepository(get(), get()) }
}