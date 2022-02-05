package ir.tdaapp.tooka.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ir.tdaapp.tooka.viewmodels.HomeViewModel
import ir.tdaapp.tooka.viewmodels.MarketsViewModel

class ViewModelFactory: ViewModelProvider.Factory {
  override fun <T: ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HomeViewModel::class.java))
      return HomeViewModel() as T
    else if (modelClass.isAssignableFrom(MarketsViewModel::class.java))
      return MarketsViewModel() as T

    throw IllegalArgumentException("Unknown ViewModel class")
  }
}