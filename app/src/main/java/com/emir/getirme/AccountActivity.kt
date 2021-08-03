package com.emir.getirme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class AccountActivity : AppCompatActivity() {
    private var nameField: EditText? = null
    private var addressField: EditText? = null
    private var fab: FloatingActionButton? = null
    private var phoneField: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.account)

        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setIcon(R.mipmap.ic_launcher)
        }

        nameField = findViewById(R.id.editTextName)
        addressField = findViewById(R.id.addressField)
        fab = findViewById(R.id.fabAccount)
        phoneField = findViewById(R.id.phoneField)

        val prefences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = prefences.edit()

        val name = prefences.getString("name", "")
        val address = prefences.getString("address", "")
        val phone = prefences.getString("phone", "")

        if (name == null) {
            nameField?.hint = "Lütfen bir isim giriniz"
        } else {
            nameField?.setText(name)
        }

        if (address == null) {
            addressField?.hint = "Lütfen bir adres giriniz"
        } else {
            addressField?.setText(address)
        }

        if (phone == null) {
            phoneField?.hint = "Lütfen bir numara giriniz"
        } else {
            phoneField?.setText(phone)
        }

        fab?.setOnClickListener { view ->
            if (nameField?.text.toString().isEmpty()) {
                val snackbar = Snackbar.make(view, "Lütfen bir isim yazınız!", 2000)
                snackbar.show()
            } else if (addressField?.text.toString().isEmpty()) {
                val snackbar = Snackbar.make(view, "Lütfen bir adres yazınız!", 2000)
                snackbar.show()
            } else if (phoneField?.text.toString().isEmpty()) {
                val snackbar = Snackbar.make(view, "Lütfen bir telefon no yazınız!", 2000)
                snackbar.show()
            } else {
                editor.putString("name", nameField?.text.toString())
                editor.putString("address", addressField?.text.toString())
                editor.putString("phone", phoneField?.text.toString())
                editor.apply()

                val snackbar = Snackbar.make(view, "Değişiklikler kaydedildi!", 2000)
                snackbar.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}