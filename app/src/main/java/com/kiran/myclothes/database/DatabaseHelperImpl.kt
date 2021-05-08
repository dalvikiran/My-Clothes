package com.kiran.myclothes.database

import com.kiran.myclothes.models.Clothes
import com.kiran.myclothes.models.FavouriteCloth

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {

    override suspend fun insertCloth(cloth: Clothes) : Long = appDatabase.clothesDao().insert(cloth)

    override suspend fun getClothes(type: Int): List<Clothes> = appDatabase.clothesDao().getClothesByType(type)

    override suspend fun addFavourite(favouriteCloth: FavouriteCloth)  = appDatabase.favouriteCloth().insert(favouriteCloth)

    override suspend fun deleteFavourite(favouriteCloth: FavouriteCloth)  = appDatabase.favouriteCloth().delete(favouriteCloth)

    override suspend fun getAllFavourites(): List<FavouriteCloth> = appDatabase.favouriteCloth().getAllFavouriteCloth()

}