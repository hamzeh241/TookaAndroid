package ir.tdaapp.tooka.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogWalletsBottomSheetBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.adapters.WalletsAdapter
import ir.tdaapp.tooka.models.util.CompoundPosition
import ir.tdaapp.tooka.models.util.getAttributeColor
import ir.tdaapp.tooka.models.util.isLoading
import ir.tdaapp.tooka.models.viewmodels.WalletsBottomSheetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class WalletsBottomSheetDialog: BottomSheetDialogFragment(),
  CoroutineScope {

  companion object {
    const val TAG = "WalletsBottomSheetDialog"
  }

  private lateinit var binding: DialogWalletsBottomSheetBinding
  private lateinit var adapter: WalletsAdapter

  private val viewModel: WalletsBottomSheetViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DialogWalletsBottomSheetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.textView.isLoading(
      true,
      getAttributeColor(requireContext(), R.attr.colorOnSurface),
      CompoundPosition.END
    )
    lifecycleScope.launchWhenCreated {
      viewModel.getWallets((requireActivity() as MainActivity).userPrefs.getUserId())
    }

    adapter = WalletsAdapter { index, item ->
      binding.textView.isLoading(
        true,
        getAttributeColor(requireContext(), R.attr.colorOnSurface),
        CompoundPosition.END
      )
      launch {
        viewModel.deleteWallet((requireActivity() as MainActivity).userPrefs.getUserId(), item)
      }
    }

    binding.walletsList.apply {
      adapter = this@WalletsBottomSheetDialog.adapter
      layoutManager = LinearLayoutManager(context)
    }

    viewModel.wallets.observe(viewLifecycleOwner) {
      if (it.isNotEmpty()) {
        binding.textView.isLoading(
          false,
          getAttributeColor(requireContext(), R.attr.colorOnSurface),
          CompoundPosition.END
        )
        adapter.differ.submitList(it)
      } else {
        Toast(requireContext()).apply {
          setDuration(Toast.LENGTH_LONG)
          setView(ToastLayoutBinding.inflate(layoutInflater).apply {
            this.message.text = getString(R.string.no_wallets)
            image.setImageResource(R.drawable.ic_white_sentiment_very_dissatisfied_24)
          }.root)
          show()
        }
        dismiss()
      }
    }

    viewModel.result.observe(viewLifecycleOwner) {
      binding.textView.isLoading(
        false,
        position = CompoundPosition.END
      )

      dismiss()
    }

  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO
}