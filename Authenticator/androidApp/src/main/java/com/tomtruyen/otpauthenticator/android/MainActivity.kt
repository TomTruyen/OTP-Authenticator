package com.tomtruyen.otpauthenticator.android

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator
import com.tomtruyen.otpauthenticator.android.databinding.ActivityMainBinding
import com.tomtruyen.otpauthenticator.android.models.TokenItem


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Authenticator"

        binding.qrButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }

        binding.setupKeyButton.setOnClickListener {
            val intent = Intent(this, AddTokenSetupKey::class.java)
            startActivity(intent)
        }

        setupTokenList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun setupTokenList() {
        val item = TokenItem("Google", "123456");

        val itemList: List<Map<String, String>> = listOf(item.toHashMap())

        val tokenAdapter = SimpleAdapter(this, itemList, R.layout.list_item, arrayOf("title", "token"), intArrayOf(R.id.list_item_title, R.id.list_item_subtitle))

        binding.tokenList.adapter = tokenAdapter
    }
}
