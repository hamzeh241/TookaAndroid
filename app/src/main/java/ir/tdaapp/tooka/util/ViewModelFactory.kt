package ir.tdaapp.tooka.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.viewmodels.*

class ViewModelFactory: ViewModelProvider.Factory {
  override fun <T: ViewModel?> create(modelClass: Class<T>): T {
//    if (modelClass.isAssignableFrom(HomeViewModel::class.java))
//      return HomeViewModel() as T
//    if (modelClass.isAssignableFrom(MarketsViewModel::class.java))
//      return MarketsViewModel() as T
    if (modelClass.isAssignableFrom(CompareViewModel::class.java))
      return CompareViewModel() as T
    else if (modelClass.isAssignableFrom(ConverterViewModel::class.java))
      return ConverterViewModel() as T
//    else if (modelClass.isAssignableFrom(CoinDetailsViewModel::class.java))
//      return CoinDetailsViewModel() as T
    else if (modelClass.isAssignableFrom(MainActivityViewModel::class.java))
      return MainActivityViewModel() as T

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}