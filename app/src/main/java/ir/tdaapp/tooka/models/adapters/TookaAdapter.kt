package ir.tdaapp.tooka.models.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.tdaapp.tooka.models.dataclasses.Coin

abstract class TookaAdapter<VM>:
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var models = arrayListOf<VM>()
    get() = field

  var callback: Callback<VM>? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }
  var longCallback: LongCallback<VM>? = null
    get() {
      return field
    }
    set(value) {
      field = value
    }

  fun interface Callback<VM> {
    fun onClick(vm: VM, position: Int)
  }

  fun interface LongCallback<VM> {
    fun onLongClick(vm: VM, position: Int)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
    getViewHolder(parent, viewType)

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    (holder as Binder<VM>).bind(models.get(position), callback!!, longCallback, models, position)
  }

  fun add(model: VM) {
    models.add(model)
    notifyItemInserted(models.size)
  }

  fun clear() {
    models.clear()
    notifyDataSetChanged()
  }

  fun removeAtPosition(position: Int) {
    models.removeAt(position)
    notifyItemRemoved(position)
  }

  fun changeViewType(viewType: Int) {
    try {
      models.forEach {
        (it as Coin).viewType = viewType
      }
      notifyDataSetChanged()
    } catch (ignored: Exception) {
      ignored.printStackTrace()
    }
  }

  override fun getItemCount(): Int = models.size

  override fun getItemViewType(position: Int): Int {
    if (models.get(0) is Coin)
      return (models.get(0) as Coin).viewType
    else return super.getItemViewType(position)
  }

  abstract fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

  internal interface Binder<VM> {
    fun bind(
      data: VM,
      callback: Callback<VM>,
      longCallback: LongCallback<VM>?,
      models: ArrayList<VM>,
      position: Int
    )
  }
}