package com.tomtruyen.otpauthenticator.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.tomtruyen.otpauthenticator.android.databinding.ActivityAddTokenBinding
import com.tomtruyen.otpauthenticator.android.databinding.ActivityMainBinding

class AddToken : AppCompatActivity() {
    private lateinit var binding : ActivityAddTokenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTokenBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Token"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }
}