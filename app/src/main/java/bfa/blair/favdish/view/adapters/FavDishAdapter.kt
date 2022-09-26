package bfa.blair.favdish.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import bfa.blair.favdish.R
import bfa.blair.favdish.databinding.ItemDishLayoutBinding
import bfa.blair.favdish.model.entities.FavDish
import bfa.blair.favdish.view.fragments.AllDishesFragment
import bfa.blair.favdish.view.fragments.FavoriteDishesFragment
import com.bumptech.glide.Glide

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root) {
        //Holds the Textview that will add each item to
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent,false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener {
            if(fragment is AllDishesFragment) {
                fragment.dishDetails(dish)
            }
            if(fragment is FavoriteDishesFragment) {
                fragment.dishDetails(dish)
            }
        }

        holder.ibMore.setOnClickListener {
            val popUp = PopupMenu(fragment.context, holder.ibMore)
            popUp.menuInflater.inflate(R.menu.menu_adapter, popUp.menu)

            popUp.setOnMenuItemClickListener {
                if(it.itemId == R.id.action_edit_dish) {
                    Log.i("Click", "You clicked on the edit button")
                } else if(it.itemId == R.id.action_delete_dish) {
                    Log.i("Click", "You clicked on delete dish")
                }
                true
            }

            popUp.show()
        }

        if(fragment is AllDishesFragment) {
            holder.ibMore.visibility = View.VISIBLE
        } else if(fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }

}