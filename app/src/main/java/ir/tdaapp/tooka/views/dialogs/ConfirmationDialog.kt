package ir.tdaapp.tooka.views.dialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ir.tdaapp.tooka.R
import ir.tdaapp.tooka.databinding.DialogConfirmationBinding

class ConfirmationDialog private constructor(
  val message: String?,
  val title: String?,
  val negativeText: String?,
  val positiveText: String?,
  val choice: (ConfirmationChoice)->Unit
):
  DialogFragment() {

  companion object {
    const val TAG = "ConfirmationDialog"
  }

  enum class ConfirmationChoice {
    Positive,
    Negative
  }

  private lateinit var binding: DialogConfirmationBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DialogConfirmationBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.txtTitle.text = title
    binding.txtDesc.text = message
    binding.txtNo.text = negativeText
    binding.txtYes.text = positiveText

    binding.cardYes.setOnClickListener {
      choice(ConfirmationChoice.Positive)
      dismiss()
    }
    binding.txtNo.setOnClickListener {
      choice(ConfirmationChoice.Negative)
      dismiss()
    }
  }

  override fun getTheme(): Int {
    return R.style.RoundedCornersDialog
  }

  internal class Builder {
    private var message: String? = null
      get() = field
      set(value) {
        field = value
      }
    private var title: String? = null
      get() = field
      set(value) {
        field = value
      }
    private var positiveText: String? = null
      get() = field
      set(value) {
        field = value
      }
    private var negativeText: String? = null
      get() = field
      set(value) {
        field = value
      }
    private var choice: (ConfirmationChoice)->Unit = {}
      get() = field
      set(value) {
        field = value
      }

    fun message(message: String) = apply { this.message = message }
    fun title(title: String) = apply { this.title = title }
    fun positiveText(positiveText: String) = apply { this.positiveText = positiveText }
    fun negativeText(negativeText: String) = apply { this.negativeText = negativeText }
    fun listener(choice: (ConfirmationChoice)->Unit) = apply { this.choice = choice }
    fun build() = ConfirmationDialog(message, title, negativeText, positiveText, choice)
  }
}