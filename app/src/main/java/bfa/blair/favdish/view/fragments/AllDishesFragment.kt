package bfa.blair.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import bfa.blair.favdish.R
import bfa.blair.favdish.application.FavDishApplication
import bfa.blair.favdish.databinding.CustomDialogListBinding
import bfa.blair.favdish.databinding.FragmentAllDishesBinding
import bfa.blair.favdish.model.entities.FavDish
import bfa.blair.favdish.utils.Constants
import bfa.blair.favdish.view.activities.AddUpdateDishActivity
import bfa.blair.favdish.view.activities.MainActivity
import bfa.blair.favdish.view.adapters.CustomListItemAdapter
import bfa.blair.favdish.view.adapters.FavDishAdapter
import bfa.blair.favdish.viewmodel.FavDishViewModel
import bfa.blair.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private var _binding : FragmentAllDishesBinding? = null

    private lateinit var mFavDishAdapter : FavDishAdapter

    private lateinit var mCustomListDialog: Dialog

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
        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        _binding!!.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) {
            dishes ->
                dishes.let{
                    if(it.isNotEmpty()) {
                        _binding!!.rvDishesList.visibility = View.VISIBLE
                        _binding!!.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {
                        _binding!!.rvDishesList.visibility = View.GONE
                        _binding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(AllDishesFragmentDirections.actionNavigationAllDishesToDishDetailsFragment(favDish))

        // Hide the bottom navigation
        if(requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())
        val binding: CustomDialogListBinding = CustomDialogListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()

        // Show the bottom navigation
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
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
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filterSelection(filterItemSelection : String) {
        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if(filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) {
                    dishes ->
                dishes.let{
                    if(it.isNotEmpty()) {
                        _binding!!.rvDishesList.visibility = View.VISIBLE
                        _binding!!.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {
                        _binding!!.rvDishesList.visibility = View.GONE
                        _binding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner) {
                dishes ->
                    dishes.let {
                        if(it.isNotEmpty()) {
                            _binding!!.rvDishesList.visibility = View.VISIBLE
                            _binding!!.tvNoDishesAddedYet.visibility = View.GONE

                            mFavDishAdapter.dishesList(it)
                        } else {
                            _binding!!.rvDishesList.visibility = View.GONE
                            _binding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
                        }
                    }
            }
        }
    }
}