package com.tomtruyen.otpauthenticator.android

import android.app.Activity
import android.content.Intent
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator
import com.tomtruyen.otpauthenticator.android.databinding.ActivityMainBinding
import com.tomtruyen.otpauthenticator.android.models.Token
import com.tomtruyen.otpauthenticator.android.models.TokenAdapter
import com.tomtruyen.otpauthenticator.android.models.TokenPersistence


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var tokenAdapter: TokenAdapter
    private lateinit var datasetObserver: DataSetObserver

//    private lateinit var tokenPersistence : TokenPersistence
//    private lateinit var tokenAdapter : ArrayAdapter<Map<String, String>>
//    private var tokenItemList : MutableList<Map<String, String>> = mutableListOf()

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

        tokenAdapter = TokenAdapter(this)
        binding.tokenList.adapter = tokenAdapter

        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

        datasetObserver = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                if (tokenAdapter.count == 0) {
                    findViewById<View>(android.R.id.empty).visibility = View.VISIBLE
                } else {
                    findViewById<View>(android.R.id.empty).visibility = View.GONE
                }
            }
        }

        tokenAdapter.registerDataSetObserver(datasetObserver)


//        tokenPersistence = TokenPersistence(this)

//        setupTokenList()
    }

    override fun onResume() {
        super.onResume()
        tokenAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        tokenAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        tokenAdapter.unregisterDataSetObserver(datasetObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    addToken(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun addToken(uri: String) {
        try {
            val token = Token(Uri.parse(uri), false)

            println("==================")
            token.debug()
            println("==================")

            val tokenPersistence = TokenPersistence(this)
            if(tokenPersistence.tokenExists(token)) {
                return
            }

            tokenPersistence.save(token)
        } catch (e : Token.TokenUriInvalidException) {
            e.printStackTrace()
        }
    }

//    private fun addToken(token: Token) {
//        tokenPersistence.save(token)
//
////        setupTokenList()
//        tokenAdapter.notifyDataSetChanged()
//
//        Toast.makeText(this, "Added token", Toast.LENGTH_LONG).show()
//    }
//
//    private fun setupTokenList() {
//        tokenItemList = tokenPersistence.getAll().toMutableList()
//
////        tokenAdapter = ArrayAdapter(this, tokenItemList, )
////        tokenAdapter = SimpleAdapter(this, tokenItemList, R.layout.list_item, arrayOf("title", "token"), intArrayOf(R.id.list_item_title, R.id.list_item_subtitle))
//
//        binding.tokenList.adapter = tokenAdapter
//    }
}
