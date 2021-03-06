package ir.tdaapp.tooka.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentConverterBinding
import ir.tdaapp.tooka.models.dataclasses.Coin
import ir.tdaapp.tooka.models.network.RetrofitClient
import ir.tdaapp.tooka.models.util.*
import ir.tdaapp.tooka.models.viewmodels.ConverterViewModel
import ir.tdaapp.tooka.ui.dialogs.CoinsListBottomSheetDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class ConverterFragment: BaseFragmentSecond(), View.OnClickListener, View.OnLongClickListener,
  CoroutineScope {

  private lateinit var binding: FragmentConverterBinding

  private val viewModel:ConverterViewModel by inject()

  private lateinit var coinList: List<Coin>
  private lateinit var firstCoin: Coin
  private lateinit var secondCoin: Coin
  private lateinit var numbersClick: View.OnClickListener

  private lateinit var converter: Converter

  private var isFirstSelected: Boolean = true

  private var layoutVisibility = false
    get() = binding.subRoot.visibility == View.VISIBLE
    set(value) {
      if (value) binding.subRoot.visibility = View.VISIBLE
      else binding.subRoot.visibility = View.GONE
      field = value
    }

  override fun init() {
    layoutVisibility = false
    setSelected(1)

    lifecycleScope.launchWhenCreated {
      viewModel.getCoinList()
    }
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
                binding.txtCoin1Price.setText(StringBuilder(binding.txtCoin1Price.text).append(v.text))
          } else
            binding.txtCoin1Price.setText(StringBuilder(binding.txtCoin1Price.text).append(v.text))
        } else
          if ((v as TextView).text == ".") {
            if (!binding.txtCoin2Price.text.contains("."))
              binding.txtCoin2Price.setText(StringBuilder(binding.txtCoin2Price.text).append(v.text))
          } else
            binding.txtCoin2Price.setText(StringBuilder(binding.txtCoin2Price.text).append(v.text))

        convert(
          when (isFirstSelected) {
            true -> 1
            false -> 2
          }
        )
      } catch (ignored: Exception) {
      }
    }

    setNumbersClickListener()
    setNumbersText()

    binding.firstCoin.setOnClickListener(this)
    binding.secondCoin.setOnClickListener(this)
    binding.firstCoinLayout.setOnClickListener(this)
    binding.secondCoinLayout.setOnClickListener(this)
    binding.imgDelete.setOnClickListener(this)
    binding.imgDelete.setOnLongClickListener(this)
  }

  private fun setNumbersClickListener() {
    for (i in 0..binding.numbersLayout.childCount) {
      val a = binding.numbersLayout.getChildAt(i)
      if (a is LinearLayout) {
        for (j in 0..a.childCount) {
          val b = a.getChildAt(j)
          if (b is TextView)
            b.setOnClickListener(numbersClick)
        }
      }
    }
  }

  private fun setNumbersText() = with(binding) {
    txt0.text = getCorrectNumberFormat("0",requireContext())
    txt1.text = getCorrectNumberFormat("1",requireContext())
    txt2.text = getCorrectNumberFormat("2",requireContext())
    txt3.text = getCorrectNumberFormat("3",requireContext())
    txt4.text = getCorrectNumberFormat("4",requireContext())
    txt5.text = getCorrectNumberFormat("5",requireContext())
    txt6.text = getCorrectNumberFormat("6",requireContext())
    txt7.text = getCorrectNumberFormat("7",requireContext())
    txt8.text = getCorrectNumberFormat("8",requireContext())
    txt9.text = getCorrectNumberFormat("9",requireContext())
  }

  override fun initObservables() {
    viewModel.coinList.observe(viewLifecycleOwner, {
      if (it != null)
        coinList = it
    })
    viewModel.data.observe(viewLifecycleOwner) {
      if (it != null) {
        layoutVisibility = true
        firstCoin = it.first
        secondCoin = it.second
        converter = Converter(firstCoin, secondCoin)
      }
    }
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentConverterBinding.inflate(inflater, container, false)
    return binding
  }

  override fun onDestroy() {
    super.onDestroy()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  fun setSelected(position: Int) {
    if (position == 1) {
      binding.firstCoinLayout.setBackgroundColor(getAttributeColor(requireContext(),R.attr.colorSecondaryVariant))
      binding.secondCoinLayout.setBackgroundColor(getAttributeColor(requireContext(),R.attr.colorSecondary))
      isFirstSelected = true
    } else {
      binding.secondCoinLayout.setBackgroundColor(getAttributeColor(requireContext(),R.attr.colorSecondaryVariant))
      binding.firstCoinLayout.setBackgroundColor(getAttributeColor(requireContext(),R.attr.colorSecondary))
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
              val result = converter.convertFrom1(toEnglishNumbers(binding.txtCoin1Price.text.toString()))
              binding.txtCoin2Price.text = toPersianNumbers(result.toString())
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
              val result = converter.convertFrom2(toEnglishNumbers(binding.txtCoin2Price.text.toString()))
              binding.txtCoin1Price.text = toPersianNumbers(result.toString())
            }
          }
        }
        dialog.show(requireActivity().supportFragmentManager, CoinsListBottomSheetDialog.TAG)
      }
      R.id.imgDelete -> {
        if (isFirstSelected) {
          val amount = binding.txtCoin1Price.text.toString()
          val new = amount.dropLast(1)
          if (!new.isEmpty()) {
            val result = converter.convertFrom1(toEnglishNumbers(new))
            binding.txtCoin2Price.text = toPersianNumbers(result.toString())
          } else binding.txtCoin2Price.text = toPersianNumbers(new)

          binding.txtCoin1Price.text = toPersianNumbers(new)
        } else {
          val amount = binding.txtCoin2Price.text.toString()
          val new = amount.dropLast(1)
          if (!new.isEmpty()) {
            val result = converter.convertFrom2(toEnglishNumbers(new))
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

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO + CoroutineName("ConverterFragmentJob")

  override fun onLongClick(v: View?): Boolean {
    when (v?.id) {
      R.id.imgDelete -> {
        binding.txtCoin2Price.text = ""
        binding.txtCoin1Price.text = ""
      }
      else -> false
    }

    return true
  }

}