package bfa.blair.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import bfa.blair.favdish.application.FavDishApplication
import bfa.blair.favdish.databinding.FragmentFavoriteDishesBinding
import bfa.blair.favdish.model.entities.FavDish
import bfa.blair.favdish.view.activities.MainActivity
import bfa.blair.favdish.view.adapters.FavDishAdapter
import bfa.blair.favdish.viewmodel.AllDishesViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var _binding: FragmentFavoriteDishesBinding? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val allDishesViewModel =
            ViewModelProvider(this).get(AllDishesViewModel::class.java)

        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        //val root: View = binding.root
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) {
            dishes ->
            dishes.let {
                _binding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
                var adapter = FavDishAdapter(this)
                _binding!!.rvFavoriteDishesList.adapter = adapter

                if(it.isNotEmpty()) {
                    _binding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    _binding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE
                    adapter.dishesList(it)
                } else {
                    _binding!!.rvFavoriteDishesList.visibility = View.GONE
                    _binding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(FavoriteDishesFragmentDirections
            .actionNavigationFavoriteDishesToDishDetailsFragment(favDish))

        if(requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}