package bfa.blair.favdish.application

import android.app.Application
import bfa.blair.favdish.model.database.FavDishRepository
import bfa.blair.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {
    private val database by lazy { FavDishRoomDatabase.getDataBase((this@FavDishApplication)) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}