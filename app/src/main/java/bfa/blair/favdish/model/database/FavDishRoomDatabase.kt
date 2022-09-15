package bfa.blair.favdish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bfa.blair.favdish.model.entities.FavDish

@Database(entities = [FavDish::class], version = 1)
abstract class FavDishRoomDatabase: RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: FavDishRoomDatabase? = null

        fun getDataBase(context: Context) : FavDishRoomDatabase {
            // If INSTANCE is not null then return it
            // If it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDatabase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                //return instance
                instance
            }
        }
    }
}