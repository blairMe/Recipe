package bfa.blair.favdish.view.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import bfa.blair.favdish.R
import bfa.blair.favdish.databinding.FragmentRandomDishBinding
import bfa.blair.favdish.model.entities.RandomDish
import bfa.blair.favdish.viewmodel.RandomDishViewModel
import com.bumptech.glide.Glide

class RandomDishFragment : Fragment() {

    private var _binding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()
    }

    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishRespose.observe(viewLifecycleOwner, Observer { randomDishResponse ->
            randomDishResponse?.let {
                Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        } )

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner,
            Observer { dataError ->
            dataError?.let {
                Log.e("Random dish API Error", "$dataError")
            }
        } )

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner ,
            Observer { loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Load Random Dish", "$loadRandomDish")
            }
        } )
    }

    private fun setRandomDishResponseInUI(recipe : RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(_binding!!.ivDishImage)

        _binding!!.tvTitle.text = recipe.title

        var dishType : String = "other"
        if(recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            _binding!!.tvType.text = dishType
        }

        _binding!!.tvCategory.text = "Other"

        var ingredient = ""
        for(value in recipe.extendedIngredients) {
            if(ingredient.isEmpty()) {
                ingredient = value.original
            } else {
                ingredient = ingredient + ", \n" + value.original
            }
        }
        _binding!!.tvIngredients.text = ingredient

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _binding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            _binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        _binding!!.tvCookingTime.text = resources.getString(
            R.string.lbl_cooking_time_in_minutes,
            recipe.readyInMinutes.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}