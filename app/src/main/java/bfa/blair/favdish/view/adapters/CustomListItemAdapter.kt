package bfa.blair.favdish.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bfa.blair.favdish.databinding.ItemCustomListBinding
import bfa.blair.favdish.view.activities.AddUpdateDishActivity

class CustomListItemAdapter(private val activity: Activity,
                            private val listItems : List<String>,
                            private val selection: String)
    : RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {

        class ViewHolder(view: ItemCustomListBinding) : RecyclerView.ViewHolder(view.root) {
            val tvText = view.tvText
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemCustomListBinding = ItemCustomListBinding
            .inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = listItems[position]
        holder.tvText.text = items

        holder.itemView.setOnClickListener {
            if(activity is AddUpdateDishActivity) {
                activity.selectedListItem(items, selection)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}