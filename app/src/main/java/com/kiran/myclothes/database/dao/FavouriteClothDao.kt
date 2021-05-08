package com.kiran.myclothes.database.dao

import androidx.room.*
import com.kiran.myclothes.models.Clothes
import com.kiran.myclothes.models.FavouriteCloth

@Dao
interface FavouriteClothDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favouriteCloth: FavouriteCloth)

    @Update
    fun update(favouriteCloth: FavouriteCloth)

    @Delete
    fun delete(favouriteCloth: FavouriteCloth)

    @Query("SELECT * from favouritecloth")
    fun getAllFavouriteCloth() : List<FavouriteCloth>

   /* @Query("SELECT * from clothes WHERE clothType=:type")
    fun getClothesByType(type : Int) : List<Clothes>*/

}