package bfa.blair.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import bfa.blair.favdish.R
import bfa.blair.favdish.application.FavDishApplication
import bfa.blair.favdish.databinding.FragmentAllDishesBinding
import bfa.blair.favdish.view.activities.AddUpdateDishActivity
import bfa.blair.favdish.viewmodel.FavDishViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModelFactory
import bfa.blair.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private var _binding: FragmentAllDishesBinding? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) {
            dishes ->
                dishes.let{
                    for (item in it) {
                        Log.i("Dish Title", "${item.id} ${item.title}")
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}