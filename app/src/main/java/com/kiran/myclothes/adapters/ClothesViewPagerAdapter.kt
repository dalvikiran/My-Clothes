package com.kiran.myclothes.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.kiran.myclothes.R
import com.kiran.myclothes.models.Clothes
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ClothesViewPagerAdapter(context: Context, clothes: ArrayList<Clothes>) : PagerAdapter() {

       // Context object
    var context: Context

    // Array of images
    var clothes: ArrayList<Clothes>

    // Layout Inflater
    var mLayoutInflater: LayoutInflater

    // Viewpager Constructor
    init {
        this.context = context
        this.clothes = clothes
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setClothesList(clothes : ArrayList<Clothes>){
        this.clothes = clothes
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        // return the number of images
        return clothes.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // inflating the item.xml
        val itemView: View = mLayoutInflater.inflate(R.layout.clothes_item_layout, container, false)

        // referencing the image view from the item.xml file
        val imageView: ImageView = itemView.findViewById(R.id.clothes_image_view) as ImageView

//        Uri.fromFile(File(images[position]))
        // setting the image in the imageView
        imageView.setImageURI(Uri.parse(clothes[position].imagePath))

       /* Glide.with(mContext)
                .load(new File(pictureUri.getPath())) // Uri of the picture
            .into(profileAvatar);*/

        Glide.with(context)
            .load(File(Uri.parse(clothes[position].imagePath).path)) //                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)

        // Adding the View
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as LinearLayout)
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }


}