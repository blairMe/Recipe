package bfa.blair.favdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import bfa.blair.favdish.databinding.FragmentRandomDishBinding
import bfa.blair.favdish.viewmodel.RandomDishViewModel

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
        mRandomDishViewModel.randomDishRespose.observe(viewLifecycleOwner
        ) { randomDishResponse ->
            randomDishResponse?.let {
                Log.i("Random Dish Response", "$randomDishResponse.recipes[0]")
            }
        }

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner
        ) { dataError ->
            dataError?.let {
                Log.e("Random dish API Error", "$dataError")
            }
        }

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner
        ) { loadRandomDish ->
            loadRandomDish?.let {
                Log.i("Load Random Dish", "$loadRandomDish")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}