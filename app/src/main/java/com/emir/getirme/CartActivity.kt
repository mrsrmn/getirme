package com.emir.getirme

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CartActivity : AppCompatActivity() {
    private var emptyText: TextView? = null
    private var fab: FloatingActionButton? = null
    private var layout: ConstraintLayout? = null
    private var orderButton: Button? = null
    private var totalPrice: Int = 0
    private var totalPriceField: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cart)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        emptyText = findViewById(R.id.emptyText)
        fab = findViewById(R.id.fabEnptyCart)
        orderButton = findViewById(R.id.order_button)
        layout = findViewById(R.id.cart_layout)
        totalPriceField = findViewById(R.id.totalPrice)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setIcon(R.mipmap.ic_launcher)
        }

        val prefences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        val cart = prefences.getStringSet("cart", setOf())

        if (cart == null || cart.isEmpty()) {
            emptyText?.isVisible = true
            orderButton?.isVisible = false
            totalPriceField?.isVisible = false
        } else {
            emptyText?.isVisible = false
            orderButton?.isVisible = true
            totalPriceField?.isVisible = true

            for ((index, value) in cart.iterator().withIndex()) {
                val newField = TextView(this)
                val priceField = TextView(this)

                newField.text = value.toString()
                newField.textSize = 27F
                newField.setTextColor(Color.BLACK)

                if (value == "Ekmek") {
                    priceField.text = "1₺"
                    totalPrice = totalPrice.inc()
                }
                priceField.textSize = 27F
                priceField.setTextColor(Color.GRAY)

                totalPriceField?.text = "Toplam: $totalPrice₺"

                if (index > 0) {
                    newField.setPadding(70, 50, 0, 0)
                    priceField.setPadding(300, 50, 0, 0)
                } else {
                    newField.setPadding(70, 200, 0, 0)
                    priceField.setPadding(300, 200, 0, 0)
                }

                layout?.addView(newField)
                layout?.addView(priceField)
            }
        }

        fab?.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)

            alertDialogBuilder.setTitle("Emin misin?")
            alertDialogBuilder.setMessage("Bu sepetteki her şeyi kaldırır!")

            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                editor.putStringSet("cart", setOf())
                editor.apply()

                finish()
                startActivity(intent)
            }

            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }

            alertDialogBuilder.setPositiveButton("Evet",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )

            alertDialogBuilder.setNegativeButton("Hayır",
                DialogInterface.OnClickListener(function = negativeButtonClick)
            )

            val builder = alertDialogBuilder.create()
            builder.show()
        }

        orderButton?.setOnClickListener { view ->
            val name = prefences.getString("name", "")
            val address = prefences.getString("address", "")
            val phone = prefences.getString("phone", "")

            lifecycleScope.launch(Dispatchers.IO) {
                khttp.post(
                    "http://192.168.1.105:5000/order",
                    headers = mapOf(
                        "address" to address.toString(),
                        "items" to Json.encodeToString(cart?.toMutableList()),
                        "price" to totalPrice.toString(),
                        "orderer" to name.toString(),
                        "phone" to phone.toString()
                    ),
                )
            }

            editor.putStringSet("cart", setOf())
            editor.apply()

            val snackbar = Snackbar.make(view, "Siparişiniz alındı!", 2000)
            snackbar.show()

            finish()
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp() : Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

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