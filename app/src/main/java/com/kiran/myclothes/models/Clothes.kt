package com.kiran.myclothes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Clothes (
    @PrimaryKey(autoGenerate = true)
    var id : Long? = null,
    var clothType : Int? = null,
    var imagePath : String? = null,

)