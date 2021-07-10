package com.tomtruyen.otpauthenticator.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SimpleAdapter
import androidx.appcompat.widget.Toolbar
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

        binding.addNewToken.setOnClickListener {
            val intent = Intent(this, AddToken::class.java)
            startActivity(intent)
        }

        setupTokenList()
    }

    private fun setupTokenList() {
        val item = TokenItem("Google", "123456");

        val itemList: List<Map<String, String>> = listOf(item.toHashMap())

        val tokenAdapter = SimpleAdapter(this, itemList, R.layout.list_item, arrayOf("title", "token"), intArrayOf(R.id.list_item_title, R.id.list_item_subtitle))

        binding.tokenList.adapter = tokenAdapter
    }
}
