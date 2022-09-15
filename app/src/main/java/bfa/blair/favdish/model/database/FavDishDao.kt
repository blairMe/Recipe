package bfa.blair.favdish.model.database

import androidx.room.Dao
import androidx.room.Insert
import bfa.blair.favdish.model.entities.FavDish

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish) {

    }
}