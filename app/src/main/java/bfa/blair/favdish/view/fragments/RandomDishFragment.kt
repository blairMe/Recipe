package bfa.blair.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import bfa.blair.favdish.R
import bfa.blair.favdish.application.FavDishApplication
import bfa.blair.favdish.databinding.FragmentRandomDishBinding
import bfa.blair.favdish.model.entities.FavDish
import bfa.blair.favdish.model.entities.RandomDish
import bfa.blair.favdish.utils.Constants
import bfa.blair.favdish.viewmodel.FavDishViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModelFactory
import bfa.blair.favdish.viewmodel.RandomDishViewModel
import com.bumptech.glide.Glide

class RandomDishFragment : Fragment() {

    private var _binding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var _progressDialog : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    private fun showCustomProgressDialog() {
        _progressDialog = Dialog(requireActivity())
        _progressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog() {
        _progressDialog?.let {
            it.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandomDishViewModel = ViewModelProvider(this)[RandomDishViewModel::class.java]
        mRandomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()

        _binding!!.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromAPI()
        }
    }

    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishRespose.observe(viewLifecycleOwner) { randomDishResponse ->
            randomDishResponse?.let {
                Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")
                if (_binding!!.srlRandomDish.isRefreshing) {
                    _binding!!.srlRandomDish.isRefreshing = false
                }
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        }

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner
        ) { dataError ->
            dataError?.let {
                if (_binding!!.srlRandomDish.isRefreshing) {
                    _binding!!.srlRandomDish.isRefreshing = false
                }
                Log.e("Random dish API Error", "$dataError")
            }
        }

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner
        ) { loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Load Random Dish", "$loadRandomDish")

                // Showing the loading dialog
                if (loadRandomDish && !_binding!!.srlRandomDish.isRefreshing) {
                    showCustomProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
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

        var ingredients = ""
        for(value in recipe.extendedIngredients) {
            ingredients = if(ingredients.isEmpty()) {
                value.original
            } else {
                ingredients + ", \n" + value.original
            }
        }
        _binding!!.tvIngredients.text = ingredients

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _binding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            _binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        _binding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavorites = false

        _binding!!.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )

        _binding!!.ivFavoriteDish.setOnClickListener {

            if(addedToFavorites) {
                Toast.makeText(requireActivity(), resources.getString(R.string.msg_already_added_to_favorites),
                    Toast.LENGTH_SHORT).show()
            } else {
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val mFavDishViewModel : FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }
                mFavDishViewModel.insert(randomDishDetails)

                addedToFavorites = true

                _binding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(requireActivity(), resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}