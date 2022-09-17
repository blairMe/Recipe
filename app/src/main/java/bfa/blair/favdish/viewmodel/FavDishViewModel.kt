package bfa.blair.favdish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import bfa.blair.favdish.model.database.FavDishRepository
import bfa.blair.favdish.model.entities.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {
    fun insert(dish: FavDish) = viewModelScope.launch{
        repository.insertFavDishData(dish)
    }
}

class FavDishViewModelFactory(private val repository: FavDishRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
         if(modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
             @Suppress("UNCHECKED_CAST")
             return FavDishViewModel(repository) as T
         }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}