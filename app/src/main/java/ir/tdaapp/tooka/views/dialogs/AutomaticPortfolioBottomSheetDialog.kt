package ir.tdaapp.tooka.views.dialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.adapters.PlatformsViewHolder
import ir.tdaapp.tooka.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.DialogAutomaticBottomSheetBinding
import ir.tdaapp.tooka.databinding.ItemPlatformBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.util.CompoundPosition
import ir.tdaapp.tooka.util.isLoading
import ir.tdaapp.tooka.viewmodels.AutomaticBottomSheetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class AutomaticPortfolioBottomSheetDialog: BottomSheetDialogFragment(), CoroutineScope {

  companion object {
    const val TAG = "AutomaticPortfolioBottomSheetDialog"
  }

  interface AutomaticPortfolioCallback {
    fun onResult()
    fun onError(error: AutomaticBottomSheetViewModel.PortfolioErrors)
  }

  var callback: AutomaticPortfolioCallback? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  lateinit var binding: DialogAutomaticBottomSheetBinding
  lateinit var platformAdapter: TookaAdapter<Platform>

  private var coins: ArrayList<Coin>? = null
  private var platforms: ArrayList<Platform>? = null

  private var selectedCoin: Coin? = null
  private var selectedPlatform: Platform? = null

  val viewModel: AutomaticBottomSheetViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogAutomaticBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    lifecycleScope.launchWhenCreated {
      withContext(Dispatchers.Main) {
        binding.textView3.isLoading(true, Color.BLACK, CompoundPosition.END)
      }
      viewModel.getData()
    }

    binding.imgQrCode.setOnClickListener {
      val dialog = QrCodeScannerDialog {
        binding.edtWallet.setText(it)
      }
      dialog.show(requireActivity().supportFragmentManager, "taf")
    }

    binding.edtWallet.doOnTextChanged { text, start, before, count ->
      if (text?.length!! > 0)
        binding.edtWallet.error = null
    }

    platformAdapter = object: TookaAdapter<Platform>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PlatformsViewHolder(ItemPlatformBinding.inflate(layoutInflater, parent, false))
    }
    platformAdapter.callback = TookaAdapter.Callback { vm, position ->
      val oldPos = with(platformAdapter.models) {
        val oldItem = platformAdapter.models.singleOrNull { it.isSelected }
        indexOf(oldItem)
      }

      platformAdapter.models.forEach {
        it.isSelected = false
      }
      vm.isSelected = true

      selectedPlatform = vm

      platformAdapter.notifyItemChanged(position)
      platformAdapter.notifyItemChanged(oldPos)
    }

    viewModel.coins.observe(viewLifecycleOwner) {
      binding.textView3.isLoading(false, Color.BLACK, CompoundPosition.END)
      coins = it as ArrayList<Coin>
      val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
      (binding.autoCompleteSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    }
    viewModel.platforms.observe(viewLifecycleOwner, {
      binding.textView9.isLoading(false, Color.BLACK, CompoundPosition.END)
      platforms = it as ArrayList<Platform>
      val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
      (binding.autoCompletePlatform as? AutoCompleteTextView)?.setAdapter(adapter)
    })
    viewModel.postResult.observe(viewLifecycleOwner) {
      binding.txtSubmit.isLoading(false, position = CompoundPosition.START)
      callback!!.onResult()
      dismiss()
    }
    viewModel.error.observe(viewLifecycleOwner) {
      binding.txtSubmit.isLoading(false, position = CompoundPosition.START)
      callback!!.onError(it)
      dismiss()
    }

    binding.autoCompleteSpinner.validator = object: AutoCompleteTextView.Validator {
      override fun isValid(text: CharSequence?): Boolean {
        selectedCoin = null
        for (coin in coins!!) {
          if (coin.toString().equals(text.toString(), true)) {
            selectedCoin = coin
            return true
          }
        }
        binding.autoCompletePlatform.clearListSelection()
        binding.autoCompletePlatform.setText("")
        return false
      }

      override fun fixText(invalidText: CharSequence?): CharSequence {
        val list = coins!!.filter {
          it.toString().contains(invalidText.toString(), true) ||
            it.symbol.contains(invalidText.toString(), true) ||
            it.name.contains(invalidText.toString(), true) ||
            it.persianName?.contains(invalidText.toString(), true) == true
        }.drop(0).take(10)

        var validText = getString(R.string.select_coin)
        selectedCoin = null
        if (list.size > 0) {
          selectedCoin = list.firstOrNull()

          launch {
            withContext(Dispatchers.Main) {
              binding.textView9.isLoading(true, Color.BLACK, CompoundPosition.END)
            }
            viewModel.getPlatforms(selectedCoin!!.id)
          }
          validText = list.firstOrNull()!!.toString()
        }
        return validText
      }

    }
    binding.autoCompleteSpinner.onItemClickListener =
      object: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val item = parent?.getItemAtPosition(position)
          if (item is Coin) {
            binding.textInputCoins.error = null
            launch {
              withContext(Dispatchers.Main) {
                binding.textView9.isLoading(true, Color.BLACK, CompoundPosition.END)
              }
              viewModel.getPlatforms(item.id)
              selectedCoin = item
            }
          }
        }
      }

    binding.autoCompletePlatform.validator = object: AutoCompleteTextView.Validator {
      override fun isValid(text: CharSequence?): Boolean {
        selectedPlatform = null
        for (platform in platforms!!) {
          if (platform.toString().equals(text.toString(), true)) {
            selectedPlatform = platform
            return true
          }
        }
        binding.autoCompletePlatform.clearListSelection()
        binding.autoCompletePlatform.setText("")
        return false
      }

      override fun fixText(invalidText: CharSequence?): CharSequence {
        val list = platforms!!.filter {
          it.toString().contains(invalidText.toString(), true) ||
            it.abbr.contains(invalidText.toString(), true) ||
            it.name.contains(invalidText.toString(), true)
        }.drop(0).take(10)

        var validText = getString(R.string.select_coin)
        selectedPlatform = null
        if (list.size > 0) {
          selectedPlatform = list.firstOrNull()
          validText = list.firstOrNull()!!.toString()
        }
        return validText
      }

    }
    binding.autoCompletePlatform.onItemClickListener =
      object: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          binding.textInputPlatform.error = null
          launch {
            selectedPlatform = platforms!![position]
          }
        }
      }

    binding.cardSubmit.setOnClickListener {
      if (selectedCoin == null)
        binding.textInputCoins.error = getString(R.string.select_coin)
      else if (selectedPlatform == null)
        binding.textInputPlatform.error = getString(R.string.specify_platform)
      else if (binding.edtWallet.text.toString().isBlank())
        binding.edtWallet.error = getString(R.string.wallet_address)
      else {
        binding.autoCompleteSpinner.performValidation()
        launch {
          withContext(Dispatchers.Main) {
            binding.txtSubmit.isLoading(true, position = CompoundPosition.START)
          }
          val model =
            AutoWalletModel(
              "",
              (requireActivity() as MainActivity).userPrefs.getUserId(requireContext()),
              binding.edtWallet.text.toString(),
              selectedPlatform!!.id,
              selectedCoin!!.id
            )
          viewModel.addPortfolio(model)
        }
      }
    }
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}