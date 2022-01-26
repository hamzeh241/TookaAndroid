package ir.tdaapp.tooka.views.fragments.coindetails

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.FragmentCoinDetailsBinding
import ir.tdaapp.tooka.util.api.RetrofitClient
import ir.tdaapp.tooka.views.fragments.base.BaseFragment
import android.view.animation.Animation

import android.graphics.Bitmap
import android.view.animation.AnimationUtils
import android.widget.ImageView


class CoinFragment: BaseFragment(), View.OnClickListener {

  lateinit var binding: FragmentCoinDetailsBinding
  lateinit var adapter: FragmentStateAdapter

  var onChartChange: OnChartChange? = null
  var isLinearChart = true

  enum class ChartType {
    CANDLESTICK,
    LINEAR
  }

  interface OnChartChange {
    fun onChanged(chartType: ChartType)
  }

  override fun init() {
    (requireActivity() as MainActivity).bottomNavVisibility = false
    initViewPager()
  }

  override fun initTransitions() {
  }

  override fun initToolbar() {
    val coinName = CoinFragmentArgs.fromBundle(requireArguments()).coinName

    binding.title.text = coinName

    val imageUrl =
      RetrofitClient.COIN_IMAGES + CoinFragmentArgs.fromBundle(requireArguments()).coinIcon
    Glide.with(requireActivity())
      .load(imageUrl)
      .into(binding.icon)
  }

  override fun initListeners(view: View) {
    binding.imgToggleChart.setOnClickListener(this)
  }

  override fun initObservables() {
  }

  override fun initErrors() {
  }

  override fun getLayout(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
    binding = FragmentCoinDetailsBinding.inflate(inflater, container, false)

    return binding
  }

  fun initViewPager() {
    val params = binding.viewPager.layoutParams
    params.height = getScreenHeight()
    params.width = getScreenWidth()

    binding.viewPager.layoutParams = params

    adapter = object: FragmentStateAdapter(this) {
      override fun getItemCount(): Int = 2

      override fun createFragment(p0: Int): Fragment =
        when (p0) {
          0 -> ContentCoinDetailsFragment(
            CoinFragmentArgs.fromBundle(requireArguments()).coinId,
            CoinFragmentArgs.fromBundle(requireArguments()).coinName
          )

          else -> ContentPriceAlertFragment(CoinFragmentArgs.fromBundle(requireArguments()).coinId)
        }
    }
    binding.viewPager.adapter = adapter
    TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->

      tab.text = when (position) {
        0 -> getString(R.string.details)
        1 -> getString(R.string.price_alert)
        else -> getString(R.string.technical_analysis)
      }
    }.attach()
  }

  fun seeMoreNews() {
    findNavController().navigate(
      CoinFragmentDirections
        .actionCoinFragmentToRelatedNewsFragment(
          CoinFragmentArgs.fromBundle(requireArguments()).coinName,
          CoinFragmentArgs.fromBundle(requireArguments()).coinId
        )
    )
  }

  fun seeMoreCoins() {
    findNavController().navigate(
      CoinFragmentDirections
        .actionCoinFragmentToRelatedCoinsFragment(CoinFragmentArgs.fromBundle(requireArguments()).coinId)
    )
  }

  override fun onDestroy() {
    super.onDestroy()
    (requireActivity() as MainActivity).bottomNavVisibility = true
  }

  fun getScreenWidth(): Int {
    return Resources.getSystem().getDisplayMetrics().widthPixels
  }

  fun getScreenHeight(): Int {
    return Resources.getSystem().getDisplayMetrics().heightPixels
  }

  fun changeImageWithAnimation(c: Context?, v: ImageView, new_image: Int) {
    val anim_out: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_out)
    val anim_in: Animation = AnimationUtils.loadAnimation(c, android.R.anim.fade_in)
    anim_out.setAnimationListener(object: Animation.AnimationListener {
      override fun onAnimationStart(animation: Animation?) {}
      override fun onAnimationRepeat(animation: Animation?) {}
      override fun onAnimationEnd(animation: Animation?) {
        v.setImageResource(new_image)
        anim_in.setAnimationListener(object: Animation.AnimationListener {
          override fun onAnimationStart(animation: Animation?) {}
          override fun onAnimationRepeat(animation: Animation?) {}
          override fun onAnimationEnd(animation: Animation?) {}
        })
        v.startAnimation(anim_in)
      }
    })
    v.startAnimation(anim_out)
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.imgToggleChart -> {
        onChartChange?.onChanged(
          when (isLinearChart) {
            true -> {
              isLinearChart = false
              changeImageWithAnimation(requireContext(),binding.imgToggleChart,R.drawable.ic_area_chart)
              ChartType.CANDLESTICK
            }
            false -> {
              isLinearChart = true
              changeImageWithAnimation(requireContext(),binding.imgToggleChart,R.drawable.ic_candlestick_chart)
              ChartType.LINEAR
            }
          }
        )
      }
    }
  }
}