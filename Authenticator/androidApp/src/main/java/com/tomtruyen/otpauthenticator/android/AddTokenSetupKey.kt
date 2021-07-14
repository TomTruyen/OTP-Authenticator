package com.tomtruyen.otpauthenticator.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.tomtruyen.otpauthenticator.android.databinding.ActivityAddTokenSetupKeyBinding
import com.tomtruyen.otpauthenticator.android.models.Token
import com.tomtruyen.otpauthenticator.android.models.TokenPersistence

class AddTokenSetupKey : AppCompatActivity() {
    private lateinit var binding: ActivityAddTokenSetupKeyBinding

    private lateinit var labelLayout: TextInputLayout
    private lateinit var keyLayout : TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTokenSetupKeyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Token"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        labelLayout = findViewById(R.id.inputLabel)
        keyLayout = findViewById(R.id.inputKey)

        binding.buttonAddToken.setOnClickListener {view: View ->
            val labelText : String = labelLayout.editText?.text.toString().trim()
            var keyText : String = keyLayout.editText?.text.toString().trim()
            keyText = keyText.replace(" ", "") // Remove spaces within text

            if(isValidLabel(labelText) && isValidKey(keyText)) {
                val token = Token(labelText, keyText)

                val tokenPersistence = TokenPersistence(this)
                val saved = tokenPersistence.save(token)

                if(saved) {
                    Toast.makeText(this, "${token.getLabel()} added ", Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }
        }
    }



    private fun isValidLabel(labelText : String) : Boolean {
        if(labelText.isEmpty()) {
            labelLayout.error = "Label is required"
            return false
        }

        return true
    }

    private fun isValidKey(keyText : String) : Boolean {
        if(keyText.isEmpty()) {
            keyLayout.error = "Key is required"
            return false
        }

        if(keyText.length < 16) {
            keyLayout.error = "Key is too short"
        }

        return true
    }
}