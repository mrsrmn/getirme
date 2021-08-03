package com.emir.getirme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlinx.serialization.json.*


class MainActivity : AppCompatActivity() {
    private var testButton: Button? = null
    private var imageSlider: ImageSlider? = null
    private var buttonEkmek: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setIcon(R.mipmap.ic_launcher)
        }

        testButton = findViewById(R.id.testButton)
        imageSlider = findViewById(R.id.image_slider)
        buttonEkmek = findViewById(R.id.buttonEkmek)

        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel("https://bit.ly/2VeOzL1", "mmmmmmm yum"))
        imageList.add(SlideModel("https://bit.ly/3kSS98r", "imagine fruit"))
        imageList.add(SlideModel("https://bit.ly/2VehS0d", "this is literally"))

        imageSlider?.setImageList(imageList)

        testButton?.setOnClickListener { view ->
            val prefences = getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor = prefences.edit()

            val name = prefences.getString("name", "")
            val address = prefences.getString("address", "")
            val phone = prefences.getString("phone", "")

            lifecycleScope.launch(Dispatchers.IO) {
                khttp.post(
                    "http://192.168.1.105:5000/order",
                    headers = mapOf(
                        "address" to address.toString(),
                        "items" to Json.encodeToString(listOf("Su x1", "Gofret x5", "Dondurma x1")),
                        "price" to "100",
                        "orderer" to name.toString(),
                        "phone" to phone.toString()
                    ),
                )
            }

            val cart = prefences.getStringSet("cart", setOf())

            val cartSet = setOf("Ekmek")

            if (cart != null) {
                for (item in cart) {
                    cartSet.toMutableSet().add(item)
                }
            }

            editor.putStringSet("cart", cartSet)
            editor.apply()

            val snackbar = Snackbar.make(view, "Test Order was successful", 2000)
            snackbar.show()
        }

        buttonEkmek?.setOnClickListener {
            val intent = Intent(this, EkmekPage::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_cart -> {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            true
        }

        R.id.action_account -> {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
