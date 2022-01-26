package ir.tdaapp.tooka.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.tdaapp.tooka.MainActivity
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogNewsDetailsBinding
import ir.tdaapp.tooka.models.NewsDetailsModel
import ir.tdaapp.tooka.util.getCurrentLocale
import ir.tdaapp.tooka.util.share
import ir.tdaapp.tooka.viewmodels.NewsDetailsViewModel
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import java.lang.StringBuilder

class NewsDetailsDialog(val newsId: Int): DialogFragment() {

  lateinit var binding: DialogNewsDetailsBinding

  val viewModel: NewsDetailsViewModel by inject()

  companion object {
    const val TAG = "NewsDetailsDialog"
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DialogNewsDetailsBinding.inflate(inflater, container, false)

    for(a in 0..6 step 3){



    }
    return binding.root
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.getData(newsId)

    viewModel.newsDetails.observe(viewLifecycleOwner, {
      if (it != null) {
        binding.loading.pauseAnimation()
        binding.loading.visibility = View.GONE
        binding.dialogRoot.visibility = View.VISIBLE
        setData(it)
      }
    })

    binding.btnShare.setOnClickListener {
      val message =
        StringBuilder(binding.txtNewsTitle.text)
          .append("\n").append("\n")
          .append(binding.txtNewsContent.text)
          .append("\n").append("\n")
          .append(binding.txtWriter.text).append(" ")
          .append(requireContext().getString(R.string.bullet))
          .append(" ").append(binding.txtWriteDate.text)
          .append("\n\n- ")
          .append("ضمیمه شده توسط توکا").toString()

      share(requireContext(), message);

    }
    binding.btnDismiss.setOnClickListener {
      dismiss()
    }
  }

  fun setData(model: NewsDetailsModel) {

    binding.txtNewsTitle.text = when (getCurrentLocale(requireContext())) {
      "en" -> model.titleEn
      "fa" -> model.titleFa
      else -> model.titleEn
    }
    binding.txtNewsContent.text = when (getCurrentLocale(requireContext())) {
      "en" -> StringBuilder(model.shortContentEn)
        .append("\n\n")
        .append(model.contentEn).toString()
      "fa" -> StringBuilder(model.shortContentFa)
        .append("\n\n")
        .append(model.contentFa).toString()
      else -> StringBuilder(model.shortContentEn)
        .append("\n\n")
        .append(model.contentEn).toString()
    }
    binding.txtWriter.text = when(getCurrentLocale(requireContext())){
      "en" -> model.author.name
      "fa" -> model.author.persianName
      else -> model.author.name
    }
    binding.txtWriteDate.text = model.writeDate

  }

  override fun getTheme(): Int = R.style.Base_AlertDialog
}