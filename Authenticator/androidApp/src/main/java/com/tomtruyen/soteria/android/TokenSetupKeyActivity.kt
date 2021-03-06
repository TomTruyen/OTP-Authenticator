package com.tomtruyen.soteria.android

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.tomtruyen.soteria.android.databinding.ActivityAddTokenSetupKeyBinding
import com.tomtruyen.soteria.android.models.DatabaseService
import com.tomtruyen.soteria.android.models.token.Token

class TokenSetupKeyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTokenSetupKeyBinding

    private lateinit var labelLayout: TextInputLayout
    private lateinit var keyLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTokenSetupKeyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Add account"
        setSupportActionBar(toolbar)

        labelLayout = findViewById(R.id.inputLabel)
        keyLayout = findViewById(R.id.inputKey)

        binding.buttonAddToken.setOnClickListener {
            val labelText: String = labelLayout.editText?.text.toString().trim()
            var keyText: String = keyLayout.editText?.text.toString().trim()
            keyText = keyText.replace(" ", "") // Remove spaces within text

            if (isValidLabel(labelText) && isValidKey(keyText)) {
                val token = Token(labelText, keyText)

                val tokenPersistence = DatabaseService(this)
                tokenPersistence.saveToken(token)

                Toast.makeText(this, "${token.getLabel()} added", Toast.LENGTH_LONG).show()

                this.finish()
            }
        }
    }


    private fun isValidLabel(labelText: String): Boolean {
        if (labelText.isEmpty()) {
            labelLayout.error = "Label is required"
            return false
        }

        labelLayout.error = null
        return true
    }

    private fun isValidKey(keyText: String): Boolean {
        if (keyText.isEmpty()) {
            keyLayout.error = "Key is required"
            return false
        }

        if (keyText.length < 16) {
            keyLayout.error = "Key is too short"
            return false
        }

        keyLayout.error = null
        return true
    }
}