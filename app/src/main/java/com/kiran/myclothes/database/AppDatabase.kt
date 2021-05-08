package com.kiran.myclothes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kiran.myclothes.database.dao.ClothesDao
import com.kiran.myclothes.database.dao.FavouriteClothDao
import com.kiran.myclothes.models.Clothes
import com.kiran.myclothes.models.FavouriteCloth
import com.kiran.myclothes.utils.Constants

@Database(
    entities = [
        Clothes::class,
        FavouriteCloth::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothesDao() : ClothesDao
    abstract fun favouriteCloth() : FavouriteClothDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        Constants.LOCAL_DATABASE_NAME
                    ).build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}