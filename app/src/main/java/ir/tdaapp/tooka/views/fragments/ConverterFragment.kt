package ir.tdaapp.tooka.views.fragments

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentConverterBinding
import ir.tdaapp.tooka.models.Coin
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.toEnglishNumbers
import ir.tdaapp.tooka.util.toPersianNumbers
import ir.tdaapp.tooka.viewmodels.ConverterViewModel
import ir.tdaapp.tooka.views.dialogs.CoinsListBottomSheetDialog
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import org.koin.android.ext.android.inject
import java.lang.StringBuilder

class ConverterFragment: BaseFragment(), View.OnClickListener {

  private lateinit var binding: FragmentConverterBinding

  private val viewModel: ConverterViewModel by inject()

  private lateinit var coinList: List<Coin>
  private lateinit var firstCoin: Coin
  private lateinit var secondCoin: Coin
  private lateinit var numbersClick: View.OnClickListener

  private lateinit var converter: Converter

  private var isFirstSelected: Boolean = true

  override fun init() {
    setSelected(1)
    viewModel.getCoinList()
  }

  fun convert(place: Int) {
    when (place) {
      1 -> {
        val result = converter.convertFrom1(toEnglishNumbers(binding.txtCoin1Price.text.toString()))
        binding.txtCoin2Price.setText(toPersianNumbers(result.toString()))
      }
      2 -> {
        val result = converter.convertFrom2(toEnglishNumbers(binding.txtCoin2Price.text.toString()))
        binding.txtCoin1Price.setText(toPersianNumbers(result.toString()))
      }
    }
  }

  override fun initTransitions() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
  }

  override fun initToolbar() {
    (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
    (requireActivity() as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    binding.toolbar.title = getString(R.string.converter)
  }

  override fun initListeners(view: View) {
    numbersClick = View.OnClickListener { v ->
      try {
        if (isFirstSelected) {
          if ((v as TextView).text == ".") {
            if (!binding.txtCoin1Price.text.toString().isEmpty())
              if (!binding.txtCoin1Price.text.contains("."))
                binding.txtCoin1Price.setText(StringBuilder(binding.txtCoin1Price.text).append((v as TextView).text))
          } else
            binding.txtCoin1Price.setText(StringBuilder(binding.txtCoin1Price.text).append((v as TextView).text))
        } else
          if ((v as TextView).text == ".") {
            if (!binding.txtCoin2Price.text.contains("."))
              binding.txtCoin2Price.setText(StringBuilder(binding.txtCoin2Price.text).append((v as TextView).text))
          } else
            binding.txtCoin2Price.setText(StringBuilder(binding.txtCoin2Price.text).append((v as TextView).text))

        convert(
          when (isFirstSelected) {
            true -> 1
            false -> 2
          }
        )
      } catch (ignored: Exception) {
      }
    }
    for (i in 0..binding.numbersLayout.childCount) {
      var a = binding.numbersLayout.getChildAt(i)
      if (a is LinearLayout) {
        for (j in 0..a.childCount) {
          var b = a.getChildAt(j)
          if (b is TextView)
            b.setOnClickListener(numbersClick)
        }
      }
    }

    binding.firstCoin.setOnClickListener(this)
    binding.secondCoin.setOnClickListener(this)
    binding.firstCoinLayout.setOnClickListener(this)
    binding.secondCoinLayout.setOnClickListener(this)
    binding.imgDelete.setOnClickListener(this)
  }

  override fun initObservables() {
    viewModel.coinList.observe(viewLifecycleOwner, {
      if (it != null)
        coinList = it
    })
    viewModel.data.observe(viewLifecycleOwner, {
      if (it != null) {
        firstCoin = it.first
        secondCoin = it.second
        converter = Converter(firstCoin, secondCoin)
      }
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentConverterBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.cancel()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  fun setSelected(position: Int) {
    if (position == 1) {
      binding.firstCoinLayout.setBackgroundColor(resources.getColor(R.color.white_100))
      binding.secondCoinLayout.setBackgroundColor(resources.getColor(R.color.white))
      isFirstSelected = true
    } else {
      binding.secondCoinLayout.setBackgroundColor(resources.getColor(R.color.white_100))
      binding.firstCoinLayout.setBackgroundColor(resources.getColor(R.color.white))
      isFirstSelected = false
    }
  }

  fun setHeaderData(position: Int, coin: Coin) {
    val imageUrl = RetrofitClient.COIN_IMAGES + coin.icon
    when (position) {
      1 -> {
        binding.txtCoin1Name.text = when (getCurrentLocale(requireContext())) {
          "fa" -> {
            if (coin.persianName != null)
              coin.persianName
            else coin.name
          }
          else -> coin.name
        }
        Glide.with(requireContext())
          .load(imageUrl)
          .into(binding.imgCoin1)
        binding.txtCoin1Symbol.text = coin.symbol
      }
      2 -> {
        binding.txtCoin2Name.text = when (getCurrentLocale(requireContext())) {
          "fa" -> {
            if (coin.persianName != null)
              coin.persianName
            else coin.name
          }
          else -> coin.name
        }
        Glide.with(requireContext())
          .load(imageUrl)
          .into(binding.imgCoin)
        binding.txtCoin2Symbol.text = coin.symbol
      }
    }
  }


  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.firstCoinLayout -> {
        setSelected(1)
      }
      R.id.secondCoinLayout -> {
        setSelected(2)
      }
      R.id.firstCoin -> {
        val dialog = CoinsListBottomSheetDialog(coinList as ArrayList<Coin>)
        dialog.callback = object: CoinsListBottomSheetDialog.OnCoinSelected {
          override fun onCoinSelected(id: Int) {
            val coin = coinList.singleOrNull { c -> c.id == id }
            if (coin != null) {
              firstCoin = coin
              setHeaderData(1, coin)
              converter = Converter(firstCoin, secondCoin)
            }
          }
        }
        dialog.show(requireActivity().supportFragmentManager, CoinsListBottomSheetDialog.TAG)
      }
      R.id.secondCoin -> {
        val dialog = CoinsListBottomSheetDialog(coinList as ArrayList<Coin>)
        dialog.callback = object: CoinsListBottomSheetDialog.OnCoinSelected {
          override fun onCoinSelected(id: Int) {
            val coin = coinList.singleOrNull { c -> c.id == id }
            if (coin != null) {
              secondCoin = coin
              setHeaderData(2, coin)
              converter = Converter(firstCoin, secondCoin)
            }
          }
        }
        dialog.show(requireActivity().supportFragmentManager, CoinsListBottomSheetDialog.TAG)
      }
      R.id.imgDelete -> {
        if (isFirstSelected) {
          var amount = binding.txtCoin1Price.text.toString()
          var new = amount.dropLast(1)
          if (!new.isEmpty()) {
            var result = converter.convertFrom1(toEnglishNumbers(new))
            binding.txtCoin2Price.text = toPersianNumbers(result.toString())
          } else binding.txtCoin2Price.text = toPersianNumbers(new)

          binding.txtCoin1Price.text = toPersianNumbers(new)
        } else {
          var amount = binding.txtCoin2Price.text.toString()
          var new = amount.dropLast(1)
          if (!new.isEmpty()) {
            var result = converter.convertFrom2(toEnglishNumbers(new))
            binding.txtCoin1Price.text = toPersianNumbers(result.toString())
          } else binding.txtCoin1Price.text = toPersianNumbers(new)

          binding.txtCoin2Price.text = toPersianNumbers(new)
        }
      }
    }
  }

  class Converter(val first: Coin, val second: Coin) {

    fun convertFrom1(amount: String): Double {
      val result = amount.toDouble() * first.priceUSD
      return result / second.priceUSD
    }

    fun convertFrom2(amount: String): Double {
      val result = amount.toDouble() * second.priceUSD
      return result / first.priceUSD
    }
  }
}