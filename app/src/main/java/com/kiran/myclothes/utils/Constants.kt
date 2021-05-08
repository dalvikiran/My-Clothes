package com.kiran.myclothes.utils

import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView


class Constants {

    companion object{
        const val LOCAL_DATABASE_NAME = "ClothesDB"
        const val CLOTH_TYPE_SHIRT = 1
        const val CLOTH_TYPE_TROUSER = 2


        fun rotateImageView(image: ImageView){
            val rotate = RotateAnimation(
                0F,
                -180F,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 300
            rotate.interpolator = LinearInterpolator()
            image.startAnimation(rotate)
        }
    }

}