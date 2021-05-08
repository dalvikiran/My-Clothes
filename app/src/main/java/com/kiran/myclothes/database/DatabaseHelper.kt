package com.kiran.myclothes.database

import com.kiran.myclothes.models.Clothes
import com.kiran.myclothes.models.FavouriteCloth

interface DatabaseHelper {

    suspend fun insertCloth(cloth: Clothes) : Long

    suspend fun getClothes(type : Int): List<Clothes>

    suspend fun addFavourite(favouriteCloth: FavouriteCloth)

    suspend fun deleteFavourite(favouriteCloth: FavouriteCloth)

    suspend fun getAllFavourites(): List<FavouriteCloth>
}