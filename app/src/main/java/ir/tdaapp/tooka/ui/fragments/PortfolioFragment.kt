package ir.tdaapp.tooka.ui.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.snackbar.Snackbar
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentPortfolioBinding
import ir.tdaapp.tooka.databinding.ItemPortfolioBalanceBinding
import ir.tdaapp.tooka.databinding.ToastLayoutBinding
import ir.tdaapp.tooka.models.adapters.PortfolioBalanceViewHolder
import ir.tdaapp.tooka.models.adapters.TookaAdapter
import ir.tdaapp.tooka.models.components.TookaSnackBar
import ir.tdaapp.tooka.models.dataclasses.PortfolioInfo
import ir.tdaapp.tooka.models.enums.ManualPortfolioErrors
import ir.tdaapp.tooka.models.util.*
import ir.tdaapp.tooka.models.viewmodels.AutomaticBottomSheetViewModel
import ir.tdaapp.tooka.models.viewmodels.AutomaticBottomSheetViewModel.PortfolioErrors.*
import ir.tdaapp.tooka.models.viewmodels.PortfolioViewModel
import ir.tdaapp.tooka.ui.dialogs.AutomaticPortfolioBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.ManualPortfolioBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.PortfolioChoiceBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.WalletsBottomSheetDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext


class PortfolioFragment: BaseFragment(), View.OnClickListener,
  PortfolioChoiceBottomSheetDialog.PortfolioChoiceBottomSheetCallback,
  Toolbar.OnMenuItemClickListener, CoroutineScope {

  lateinit var binding: FragmentPortfolioBinding
  lateinit var adapter: TookaAdapter<PortfolioInfo.Balance>

  private var isAlreadyCreated = false

  private val viewModel: PortfolioViewModel by inject()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View =
    if (this::binding.isInitialized) {
      isAlreadyCreated = true
      binding.root
    } else {
      binding = FragmentPortfolioBinding.inflate(inflater, container, false)
      binding.root
    }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (!isAlreadyCreated) {
      initPortfolioCoins()
      initToolbar()
      initListeners()
      initObservables()
      binding.toolbar.setOnMenuItemClickListener(this)
    }

    lifecycleScope.launchWhenResumed {
      viewModel.getAllBalances(
        (requireActivity() as MainActivity).userPrefs.getUserId()
      )
    }
  }

  private fun showPieChart(models: PortfolioInfo) {
    val pieEntries: ArrayList<PieEntry> = ArrayList()

    //initializing data
    val typeAmountMap: MutableMap<String, Int> = HashMap()
    val intColors: ArrayList<Int> = ArrayList()
    for (i in models.balances) {
      typeAmountMap.put(i.coin_symbol, i.price_dollar.toInt())
      intColors.add(Color.parseColor(i.thumb_color))
    }

    for (type in typeAmountMap.keys) {
      pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), type))
    }

    val pieDataSet = PieDataSet(pieEntries, "").apply {

      //setting text size of the value
      label = ""
      //providing color list for coloring different entries
      this.colors = intColors
      //grouping the data set from entry to chart
      //showing the value of the entries, default true if not set
      setDrawValues(false)
      setDrawIcons(false)
    }
    val pieData = PieData(pieDataSet)

    binding.lineChart.description = Description().apply {
      text = ""
      isEnabled = false
    }
    binding.lineChart.apply {
      setDragDecelerationFrictionCoef(0.9f)
      setRotationAngle(0f)
      setHighlightPerTapEnabled(true)
      animateY(1400)
      setUsePercentValues(true)
      setEntryLabelTextSize(10f)
      setDrawEntryLabels(true)
      setData(pieData)
      invalidate()
    }
  }

  private fun initToolbar() {
    binding.toolbar.title = getString(R.string.portfolio)
  }

  private fun initListeners() {
    adapter.callback = TookaAdapter.Callback { vm, pos ->

    }
  }

  private fun initObservables() {
    viewModel.capitals.observe(viewLifecycleOwner) {
      if (it.balances.size > 0)
        binding.noPortfolio.visibility = View.GONE
      adapter.models = it.balances as ArrayList<PortfolioInfo.Balance>

      binding.portfolioCoinsList.run {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = this@PortfolioFragment.adapter
      }

      showPieChart(it)

      binding.txtCapitalTMN.text =
        StringBuilder(
          formatPrice(
            getCorrectNumberFormat(
              separatePrice(
                it.total_balance_tooman.toBigDecimal().toFloat()
              ), requireContext()
            ), getString(R.string.toomans)
          )
        ).toString()
      binding.txtCapitalUSD.text = StringBuilder(
        formatPrice(
          getCorrectNumberFormat(
            separatePrice(
              it.total_balance_dollar.toBigDecimal().toFloat()
            ), requireContext()
          ), getString(R.string.dollars)
        )
      ).toString()
    }

    viewModel.error.observe(viewLifecycleOwner) {
      val message: String
      @DrawableRes val icon: Int
      when (it) {
        NetworkErrors.NETWORK_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.CLIENT_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
        NetworkErrors.NOT_FOUND_ERROR -> {
          message = getString(R.string.coin_not_found)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          requireActivity().onBackPressed()
        }
        NetworkErrors.SERVER_ERROR -> {
          message = getString(R.string.server_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNAUTHORIZED_ERROR -> {
          message = getString(R.string.network_error_desc)
          icon = R.drawable.ic_dns_white_24dp
        }
        NetworkErrors.UNKNOWN_ERROR -> {
          message = getString(R.string.unknown_error_desc)
          icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
        }
      }

      Toast(requireContext()).apply {
        setDuration(Toast.LENGTH_LONG)
        setView(ToastLayoutBinding.inflate(layoutInflater).apply {
          this.message.text = message
          image.setImageResource(icon)
        }.root)
        show()
      }
    }
  }

  fun initPortfolioCoins() {
    adapter = object: TookaAdapter<PortfolioInfo.Balance>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PortfolioBalanceViewHolder(
          ItemPortfolioBalanceBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
    }
  }

  override fun onAutomaticSelected() {
    val dialog = AutomaticPortfolioBottomSheetDialog()
    dialog.callback = object: AutomaticPortfolioBottomSheetDialog.AutomaticPortfolioCallback {
      override fun onResult() {
        launch {
          viewModel.getAllBalances(
            (requireActivity() as MainActivity).userPrefs.getUserId()
          )
        }
      }

      override fun onError(error: AutomaticBottomSheetViewModel.PortfolioErrors) {
        val message: String
        @DrawableRes val icon: Int
        when (error) {
          NETWORK_ERROR -> {
            message = getString(R.string.network_error_desc)
            icon = R.drawable.ic_dns_white_24dp
          }
          INVALID_ARGS -> {
            message = getString(R.string.invalid_args)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          NO_ARGS -> {
            message = getString(R.string.unknown_error_desc)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          NOT_FOUND -> {
            message = getString(R.string.wallet_not_found)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          DATABASE_ERROR -> {
            message = getString(R.string.user_database_error)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          SERVER_ERROR -> {
            message = getString(R.string.server_error_desc)
            icon = R.drawable.ic_dns_white_24dp
          }
          UNKNOWN_ERROR -> {
            message = getString(R.string.unknown_error_desc)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          WALLET_EMPTY -> {
            message = getString(R.string.wallet_empty_or_hard)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
        }

        Toast(requireContext()).apply {
          setDuration(Toast.LENGTH_LONG)
          setView(ToastLayoutBinding.inflate(layoutInflater).apply {
            this.message.text = message
            image.setImageResource(icon)
          }.root)
          show()
        }
      }
    }
    dialog.show(requireActivity().supportFragmentManager, AutomaticPortfolioBottomSheetDialog.TAG)
  }

  override fun onManualSelected() {
    val dialog = ManualPortfolioBottomSheetDialog()
    dialog.callback = object: ManualPortfolioBottomSheetDialog.ManualPortfolioDialogCallback {
      override fun onResult() {
        launch {
          viewModel.getAllBalances(
            (requireActivity() as MainActivity).userPrefs.getUserId()
          )
        }
      }

      override fun onError(error: ManualPortfolioErrors) {
        val message: String
        @DrawableRes val icon: Int
        when (error) {
          ManualPortfolioErrors.NO_ARGS -> {
            message = getString(R.string.unknown_error_desc)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          ManualPortfolioErrors.INVALID_ARGS -> {
            message = getString(R.string.invalid_args)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          ManualPortfolioErrors.NOT_SAVED -> {
            message = getString(R.string.user_database_error)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
          ManualPortfolioErrors.NETWORK_ERROR -> {
            message = getString(R.string.network_error_desc)
            icon = R.drawable.ic_dns_white_24dp
          }
          ManualPortfolioErrors.SERVER_ERROR -> {
            message = getString(R.string.server_error_desc)
            icon = R.drawable.ic_dns_white_24dp
          }
          ManualPortfolioErrors.UNKNOWN_ERROR -> {
            message = getString(R.string.unknown_error_desc)
            icon = R.drawable.ic_white_sentiment_very_dissatisfied_24
          }
        }

        Toast(requireContext()).apply {
          setDuration(Toast.LENGTH_LONG)
          setView(ToastLayoutBinding.inflate(layoutInflater).apply {
            this.message.text = message
            image.setImageResource(icon)
          }.root)
          show()
        }
      }

    }
    dialog.show(requireActivity().supportFragmentManager, ManualPortfolioBottomSheetDialog.TAG)
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.wallets -> {
        val dialog = WalletsBottomSheetDialog()
        dialog.show(requireActivity().supportFragmentManager, WalletsBottomSheetDialog.TAG)
      }
      R.id.add -> {
        if (getUserId() > 0) {
          val dialog = PortfolioChoiceBottomSheetDialog()
          dialog.callback = this
          dialog.show(
            requireActivity().supportFragmentManager,
            PortfolioChoiceBottomSheetDialog.TAG
          )
        } else {
          @ColorInt val colorOnError = getAttributeColor(binding.root.context, R.attr.colorOnError)
          TookaSnackBar(
            binding.root,
            getString(R.string.not_logged_in),
            Snackbar.LENGTH_LONG
          ).textConfig { textView ->
            textView.typeface = Typeface.createFromAsset(
              requireActivity().assets,
              "iranyekan_medium.ttf"
            )
            textView.setTextColor(colorOnError)
          }.backgroundConfig {
            it.setBackgroundColor(resources.getColor(R.color.red_200))
          }.show()
        }
      }
    }
    return true
  }

  override fun onClick(v: View?) {

  }
}