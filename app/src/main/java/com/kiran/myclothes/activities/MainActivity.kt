package com.kiran.myclothes.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.kiran.myclothes.R
import com.kiran.myclothes.adapters.ClothesViewPagerAdapter
import com.kiran.myclothes.database.AppDatabase
import com.kiran.myclothes.database.dao.ClothesDao
import com.kiran.myclothes.database.DatabaseHelper
import com.kiran.myclothes.database.DatabaseHelperImpl
import com.kiran.myclothes.database.dao.FavouriteClothDao
import com.kiran.myclothes.databinding.ActivityMainBinding
import com.kiran.myclothes.models.Clothes
import com.kiran.myclothes.models.FavouriteCloth
import com.kiran.myclothes.utils.ChoosePhoto
import com.kiran.myclothes.utils.Constants
import com.kiran.myclothes.utils.coroutineMainScope
import com.kiran.myclothes.utils.coroutineScope
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private var db: AppDatabase? = null
    private var dbHelper: DatabaseHelper? = null
    private var clothesDao: ClothesDao? = null
    private var favouriteClothesDao: FavouriteClothDao? = null

    lateinit var choosePhoto : ChoosePhoto
//    lateinit var choosePhoto2 : ChoosePhoto

    lateinit var shirtsViewPagerAdapter : ClothesViewPagerAdapter
    lateinit var trousersViewPagerAdapter : ClothesViewPagerAdapter

    var shirtList = ArrayList<Clothes>()
    var trouserList = ArrayList<Clothes>()

    var favouriteClothList = ArrayList<FavouriteCloth>()

    var type = 0
    var shirtPagerPosition = 0
    var trouserPagerPosition = 0

    var favourite = false
    var favouriteCloth : FavouriteCloth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        shirtsViewPagerAdapter = ClothesViewPagerAdapter(this, shirtList)
        trousersViewPagerAdapter = ClothesViewPagerAdapter(this, trouserList)

        binding.shirtsViewPager.adapter = shirtsViewPagerAdapter
        binding.trousersViewPager.adapter = trousersViewPagerAdapter


        db = AppDatabase.getAppDataBase(context = this)
        clothesDao = db?.clothesDao()
        favouriteClothesDao = db?.favouriteCloth()

        dbHelper = AppDatabase.INSTANCE?.let { DatabaseHelperImpl(it) }

        getClothes()

        binding.addShirtsLayout.setOnClickListener{
            type = Constants.CLOTH_TYPE_SHIRT
            choosePhoto = ChoosePhoto(this@MainActivity)
        }
        binding.addTrousersLayout.setOnClickListener{
            type = Constants.CLOTH_TYPE_TROUSER
            choosePhoto = ChoosePhoto(this@MainActivity)
        }
        binding.addShirtImageView.setOnClickListener{
            type = Constants.CLOTH_TYPE_SHIRT
            choosePhoto = ChoosePhoto(this@MainActivity)
        }
        binding.addTrouserImageView.setOnClickListener{
            type = Constants.CLOTH_TYPE_TROUSER
            choosePhoto = ChoosePhoto(this@MainActivity)
        }

        binding.favoriteImageView.setOnClickListener{
           /* var intent = Intent(this, ImagePickerActivity::class.java)
            startActivity(intent)*/
            addFavourite()
        }

        binding.shuffleImageView.setOnClickListener{
            Constants.rotateImageView(binding.shuffleImageView)
            shuffleClothes()
        }

        onPageChangeListener()
    }

    private fun onPageChangeListener(){
        binding.shirtsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {           }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                shirtPagerPosition = position
                binding.shirtPagerIndicatorTextView.text = "${shirtPagerPosition+1}/${shirtList.size}"
                checkFavorites()
            }
        })

        binding.trousersViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                trouserPagerPosition = position
                binding.trouserPagerIndicatorTextView.text = "${trouserPagerPosition+1}/${trouserList.size}"
                checkFavorites()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ChoosePhoto.CHOOSE_PHOTO_INTENT) {
                var uri : Uri
                if ( data != null && data.data != null) {
                    choosePhoto.handleGalleryResult(data)
//                    uri = choosePhoto.handleGalleryResult(data)
                } else {
                    choosePhoto.handleCameraResult(choosePhoto.cameraUri)
//                    uri = choosePhoto.cameraUri
                }
                /*val cloth = Clothes(null, type, uri.toString())
                saveCloth(cloth)*/

            } else if (requestCode == ChoosePhoto.SELECTED_IMG_CROP) {

                val cloth = Clothes(null, type, choosePhoto.getCropImageUrl().toString())
                saveCloth(cloth)
            } else if (requestCode == UCrop.REQUEST_CROP) {

//                val cloth = Clothes(null, type, choosePhoto.getCropImageUrl().toString())
                val cloth = Clothes(null, type, UCrop.getOutput(data!!).toString())
                saveCloth(cloth)
            }/*else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
            }*/
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ChoosePhoto.SELECT_PICTURE_CAMERA) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) choosePhoto.showAlertDialog()
        }

    }

    private fun getClothes(){
        coroutineScope.launch {
            try {
                shirtList = clothesDao?.getClothesByType(Constants.CLOTH_TYPE_SHIRT) as ArrayList<Clothes>
                trouserList = clothesDao?.getClothesByType(Constants.CLOTH_TYPE_TROUSER) as ArrayList<Clothes>

                favouriteClothList = favouriteClothesDao?.getAllFavouriteCloth() as ArrayList<FavouriteCloth>

                coroutineMainScope.launch {
                    shirtsViewPagerAdapter.setClothesList(shirtList)
                    trousersViewPagerAdapter.setClothesList(trouserList)
                    showUIElement()
                }

            } catch (e: Exception) {
                // handler error
                Log.e("tag",e.toString())
            }

        }
    }

    private fun saveCloth(cloth: Clothes) {
        coroutineScope.launch {
            try {
                val id = dbHelper?.insertCloth(cloth)!!
                coroutineMainScope.launch {
                    cloth.id = id
                    addClothOnUI(cloth)
                }
            } catch (e: Exception) {
                // handler error
            }
        }
    }

    private fun addClothOnUI(cloth : Clothes){
        if (type == Constants.CLOTH_TYPE_SHIRT){
            shirtList.add(cloth)
            shirtsViewPagerAdapter.notifyDataSetChanged()
        }else if (type == Constants.CLOTH_TYPE_TROUSER){
            trouserList.add(cloth)
            trousersViewPagerAdapter.notifyDataSetChanged()
        }
        showUIElement()
    }

    private fun showUIElement(){
        if (shirtList.size > 0){
            binding.addShirtsLayout.visibility = View.GONE
            binding.shirtsViewPager.visibility = View.VISIBLE
            binding.addShirtImageView.visibility = View.VISIBLE
            binding.shirtPagerIndicatorTextView.visibility = View.VISIBLE
            binding.shirtPagerIndicatorTextView.text = "${shirtPagerPosition+1}/${shirtList.size}"
        }
        if (trouserList.size > 0){
            binding.addTrousersLayout.visibility = View.GONE
            binding.trousersViewPager.visibility = View.VISIBLE
            binding.addTrouserImageView.visibility = View.VISIBLE
            binding.trouserPagerIndicatorTextView.visibility = View.VISIBLE
            binding.trouserPagerIndicatorTextView.text = "${trouserPagerPosition+1}/${trouserList.size}"
        }
        if (shirtList.size > 0 && trouserList.size > 0){
            binding.favoriteImageView.visibility = View.VISIBLE
            binding.shuffleImageView.visibility = View.VISIBLE
        }
    }


    private fun addFavourite(){
        coroutineScope.launch {
            try {
                if (favourite){
                    favourite = false
                    favouriteClothesDao?.delete(favouriteCloth!!)
                }else{
                    favourite = true
                    favouriteCloth = FavouriteCloth(null, shirtList[shirtPagerPosition].id!!, trouserList[trouserPagerPosition].id!!)
                    favouriteClothesDao?.insert(favouriteCloth!!)
                    favouriteClothList.add(favouriteCloth!!)
                }
                coroutineMainScope.launch {
                    if (favourite)
                        binding.favoriteImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite))
                    else
                        binding.favoriteImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite_border))
                }

            } catch (e: Exception) {
                // handler error
            }
        }
    }

    private fun checkFavorites(){
        try{
            if (shirtList.size > 0 && trouserList.size > 0){
                val shirtSelectedId = shirtList[shirtPagerPosition].id
                val trouserSelectedId = trouserList[trouserPagerPosition].id

                favourite = false
                favouriteCloth = null
                for (item in favouriteClothList){
                    if (item.shirtId?.equals(shirtSelectedId)!! && item.trouserId?.equals(trouserSelectedId)!!){
                        favourite = true
                        favouriteCloth = item
                        break
                    }
                }
            }

            if (favourite)
                binding.favoriteImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite))
            else
                binding.favoriteImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite_border))
        }catch (e: Exception) {
            // handler error
            Log.e("tag",e.toString())
        }

    }

    fun shuffleClothes(){
        if (shirtList.size > 1 && trouserList.size > 1){
            val shirtPosition = (0 until shirtList.size).random()
            val trouserPosition = (0 until trouserList.size).random()

            binding.shirtsViewPager.setCurrentItem(shirtPosition,true)
            binding.trousersViewPager.setCurrentItem(trouserPosition,true)

        }
    }
}