package ir.tdaapp.tooka.models.dataclasses

data class Platform(var id: Int, var name: String, var abbr: String, var isSelected: Boolean) {
  override fun toString(): String {
    if (abbr != null)
      return "${name} (${abbr})"
    else return name
  }
}