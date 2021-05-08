package com.kiran.myclothes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteCloth (
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    var shirtId : Long,
    var trouserId : Long

)