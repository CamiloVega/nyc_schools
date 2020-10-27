package cvdevelopers.takehome.ui.schoollist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cvdevelopers.githubstalker.R
import kotlinx.android.synthetic.main.school_item.view.*


class SchoolListAdapter (private val onClick: (String) -> Unit) : PagedListAdapter<SchoolDisplayData, SchoolListAdapter.SchoolItemViewHolder>(DiffCallback()) {

  class SchoolItemViewHolder(view: View, val schoolOnClick: (String) -> Unit) : RecyclerView.ViewHolder(view){
    fun  bind(school: SchoolDisplayData) {
      itemView.school_name.text = school.schoolName
      itemView.school_borough.text = school.schoolBorough
      itemView.school_summary.text = school.schoolSummary
      itemView.setOnClickListener { schoolOnClick(school.schoolDbn) }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolItemViewHolder {
      return SchoolItemViewHolder(LayoutInflater.from(parent.context)
          .inflate(R.layout.school_item, parent, false), schoolOnClick = onClick)
  }

  override fun onBindViewHolder(holder: SchoolItemViewHolder, position: Int) {
    getItem(position)?.let { holder.bind(it) }
  }
}

class DiffCallback : DiffUtil.ItemCallback<SchoolDisplayData>() {
  override fun areItemsTheSame(oldItem: SchoolDisplayData, newItem: SchoolDisplayData): Boolean {
    return oldItem.getUniqueKey() == newItem.getUniqueKey()
  }

  override fun areContentsTheSame(oldItem: SchoolDisplayData, newItem: SchoolDisplayData): Boolean {
    return oldItem == newItem
  }
}

data class SchoolDisplayData(val schoolName: String, val schoolBorough:String, val schoolSummary: String, val schoolDbn: String) {
   fun getUniqueKey() = this.hashCode()
}