package com.kiran.myclothes.database.dao

import androidx.room.*
import com.kiran.myclothes.models.Clothes

@Dao
interface ClothesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clothes: Clothes) : Long

    @Update
    fun update(clothes: Clothes)

    @Delete
    fun delete(clothes: Clothes)

    @Query("SELECT * from clothes")
    fun getAllClothes() : List<Clothes>

    @Query("SELECT * from clothes WHERE clothType=:type")
    fun getClothesByType(type : Int) : List<Clothes>

}