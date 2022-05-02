package ir.tdaapp.tooka.ui.fragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.models.adapters.PortfolioCoinsViewHolder
import ir.tdaapp.tooka.models.adapters.TookaAdapter
import ir.tdaapp.tooka.databinding.FragmentPortfolioBinding
import ir.tdaapp.tooka.databinding.ItemPortfolioBalanceCoinBinding
import ir.tdaapp.tooka.models.dataclasses.*
import ir.tdaapp.tooka.models.util.separatePrice
import ir.tdaapp.tooka.models.viewmodels.AutomaticBottomSheetViewModel
import ir.tdaapp.tooka.models.viewmodels.ManualBottomSheetViewModel
import ir.tdaapp.tooka.models.viewmodels.PortfolioViewModel
import ir.tdaapp.tooka.ui.dialogs.AutomaticPortfolioBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.ManualPortfolioBottomSheetDialog
import ir.tdaapp.tooka.ui.dialogs.PortfolioChoiceBottomSheetDialog
import ir.tdaapp.tooka.ui.fragments.base.BaseFragmentSecond
import org.koin.android.ext.android.inject


class PortfolioFragment: BaseFragmentSecond(), View.OnClickListener,
  PortfolioChoiceBottomSheetDialog.PortfolioChoiceBottomSheetCallback {

  lateinit var binding: FragmentPortfolioBinding
  lateinit var adapter: TookaAdapter<PortfolioInfo.Balance>

  var isPortfolioActivated: Boolean = false

  private val viewModel: PortfolioViewModel by inject()

  override fun init() {
    initPortfolioCoins()

    lifecycleScope.launchWhenStarted {
      viewModel.getAllBalances(
        (requireActivity() as MainActivity).userPrefs.getUserId(
          requireContext()
        )
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

      setDragDecelerationFrictionCoef(0.9f);
      setRotationAngle(0f);
      setHighlightPerTapEnabled(true);
      animateY(1400)
      setUsePercentValues(true);

      setEntryLabelTextSize(10f)
      setDrawEntryLabels(true)
      setData(pieData)
      invalidate()
    }
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    binding.toolbar.title = getString(R.string.portfolio)
  }

  override fun initListeners(view: View) {
    binding.fabPortfolioAdd.setOnClickListener(this)
    binding.addToPortfolio.setOnClickListener(this)

    adapter.callback = TookaAdapter.Callback { vm, pos ->
    }
  }

  override fun initObservables() {
    viewModel.capitals.observe(viewLifecycleOwner, {
      adapter.models = it.balances as ArrayList<PortfolioInfo.Balance>

      val gridManager =
        GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
      binding.portfolioCoinsList.layoutManager = gridManager
      binding.portfolioCoinsList.adapter = adapter

      showPieChart(it)

      binding.txtCapitalTMN.text =
        StringBuilder(separatePrice(it.total_balance_tooman.toBigDecimal().toFloat()))
          .append(" تومان")
          .toString()
      binding.txtCapitalUSD.text = StringBuilder(separatePrice(it.total_balance_dollar))
        .append(" دلار")
        .toString()
    })

    viewModel.isPortfolio.observe(this, {
      isPortfolioActivated = it
    })
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentPortfolioBinding.inflate(inflater, container, false)
    return binding
  }

  fun initPortfolioCoins() {
    adapter = object: TookaAdapter<PortfolioInfo.Balance>() {
      override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PortfolioCoinsViewHolder(
          ItemPortfolioBalanceCoinBinding.inflate(
            layoutInflater,
            parent,
            false
          )
        )
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.fabPortfolioAdd -> {
        val dialog = PortfolioChoiceBottomSheetDialog()
        dialog.callback = this
        dialog.show(requireActivity().supportFragmentManager, PortfolioChoiceBottomSheetDialog.TAG)
      }
      R.id.addToPortfolio -> {
        binding.fabPortfolioAdd.performClick()
      }
    }
  }

  override fun onAutomaticSelected() {
    val dialog = AutomaticPortfolioBottomSheetDialog()
    dialog.callback = object: AutomaticPortfolioBottomSheetDialog.AutomaticPortfolioCallback {
      override fun onResult() {
        toast("onResult")
      }

      override fun onError(error: AutomaticBottomSheetViewModel.PortfolioErrors) {
        toast("onError")
      }
    }
    dialog.show(requireActivity().supportFragmentManager, AutomaticPortfolioBottomSheetDialog.TAG)
  }

  override fun onManualSelected() {
    val dialog = ManualPortfolioBottomSheetDialog()
    dialog.callback = object: ManualPortfolioBottomSheetDialog.ManualPortfolioDialogCallback {
      override fun onResult() {
      }

      override fun onError(error: ManualBottomSheetViewModel.ManualPortfolioErrors) {

      }

    }
    dialog.show(requireActivity().supportFragmentManager, ManualPortfolioBottomSheetDialog.TAG)
  }
}