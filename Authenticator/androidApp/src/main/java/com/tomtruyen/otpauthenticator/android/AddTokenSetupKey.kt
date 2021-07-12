package com.tomtruyen.otpauthenticator.android

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.tomtruyen.otpauthenticator.android.databinding.ActivityAddTokenSetupKeyBinding

class AddTokenSetupKey : AppCompatActivity() {
    private lateinit var binding: ActivityAddTokenSetupKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTokenSetupKeyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Token"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}