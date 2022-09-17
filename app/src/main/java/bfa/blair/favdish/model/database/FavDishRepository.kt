package bfa.blair.favdish.model.database

import androidx.annotation.WorkerThread
import bfa.blair.favdish.model.entities.FavDish

class FavDishRepository(private val favDishDao: FavDishDao) {
    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insertFavDishDetails(favDish)
    }
}