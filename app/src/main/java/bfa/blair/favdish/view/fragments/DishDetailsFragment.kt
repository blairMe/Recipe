package bfa.blair.favdish.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import bfa.blair.favdish.R
import bfa.blair.favdish.application.FavDishApplication
import bfa.blair.favdish.databinding.FragmentDishDetailsBinding
import bfa.blair.favdish.model.database.FavDishRepository
import bfa.blair.favdish.model.entities.FavDish
import bfa.blair.favdish.utils.Constants
import bfa.blair.favdish.viewmodel.FavDishViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModelFactory
import com.bumptech.glide.Glide
import java.io.IOException
import java.util.*


class DishDetailsFragment : Fragment() {

    private var mBinding: FragmentDishDetailsBinding? = null

    private var mFavDishDetails : FavDish? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "check out this dish recipe"
                var extraText = ""
                val shareWith = "Share With"

                mFavDishDetails?.let {
                    var image = ""
                    if(it.imagePath == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                        image = it.image
                    }

                    var cookingInstructions = ""
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cookingInstructions = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    } else {
                        @Suppress("DEPRECATION")
                        cookingInstructions = Html.fromHtml(it.directionToCook).toString()
                    }

                    extraText = "$image \n" +
                            "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                            "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                            "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
            }
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()

        mFavDishDetails = args.dishDetails

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(mBinding!!.ivDishImage)
            } catch (e : IOException) {
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text = it.dishDetails.type.capitalize(Locale.ROOT)
            mBinding!!.tvCategory.text = it.dishDetails.category
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients
            // mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBinding!!.tvCookingDirection.text = Html.fromHtml(
                    it.dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                ).toString()
            } else {
                @Suppress("DEPRECATION")
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.directionToCook).toString()
            }
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)

            // Favorite dish icon
            if(args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_selected
                ))
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_unselected
                ))
            }
        }

        mBinding!!.ivFavoriteDish.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            mFavDishViewModel.update(args.dishDetails)

            // Changing the icon
            if(args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_selected
                ))
                Toast.makeText(requireActivity(), R.string.msg_added_to_favorites, Toast.LENGTH_SHORT).show()
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(), R.drawable.ic_favorite_unselected
                ))
                Toast.makeText(requireActivity(), R.string.msg_removed_from_favorites, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}