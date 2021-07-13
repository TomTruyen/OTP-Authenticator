package com.tomtruyen.otpauthenticator.android

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.tomtruyen.otpauthenticator.android.databinding.ActivityMainBinding
import com.tomtruyen.otpauthenticator.android.models.Token
import com.tomtruyen.otpauthenticator.android.models.TokenAdapter
import com.tomtruyen.otpauthenticator.android.models.TokenPersistence
import java.security.InvalidParameterException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenAdapter: TokenAdapter
    private lateinit var datasetObserver: DataSetObserver
    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding Setup
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        // Toolbar Setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Don't allow screenshots
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

        // Initiate ClipboardManager
        clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Listview Click Listener
        val listview = findViewById<ListView>(R.id.tokenList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val token = tokenAdapter.getItem(position)

            if (token != null) {
                val code = token.generateCode()
                val clip: ClipData = ClipData.newPlainText("2FA Code", code)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(this, "Copied: $code", Toast.LENGTH_LONG).show()
            }
        }

        listview.setOnItemLongClickListener { _, _, position: Int, _ ->
            val token = tokenAdapter.getItem(position)

            val dialog = AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete)
                .setTitle("Delete \"${token?.getLabel()}\"")
                .setMessage("Are you sure you want to delete \"${token?.getLabel()}?\"")
                .setPositiveButton("Yes") { _, _ ->
                    val tokenPersistence = TokenPersistence(this)
                    tokenPersistence.delete(position)
                    Toast.makeText(this, "${token?.getLabel()} deleted", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("No", null)
                .show()

            val primaryColor = ContextCompat.getColor(this, R.color.primary)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor)

            true
        }


        // FAB Item Click Listeners
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

        // TokenAdapter setup
        tokenAdapter = TokenAdapter(this)
        binding.tokenList.adapter = tokenAdapter

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

        startTimer()
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
        if (resultCode == Activity.RESULT_OK) {
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

    // Refresh Timer
    private fun startTimer() {
        object : CountDownTimer(tokenAdapter.getSecondsUntilRefresh().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val seconds = tokenAdapter.getSecondsUntilRefresh()
                val percentage = (seconds.toDouble() / 30) * 100

                tokenAdapter.percentage = percentage.toInt()
                tokenAdapter.seconds = seconds
                tokenAdapter.notifyDataSetChanged()

            }

            override fun onFinish() {
                tokenAdapter.shouldGenerateToken = true
                this.start()
            }
        }.start()
    }

    // Add token on QR Code Scan
    private fun addToken(uri: String) {
        try {
            val token = Token(Uri.parse(uri), false)

            val tokenPersistence = TokenPersistence(this)
            if (tokenPersistence.tokenExists(token)) {
                return
            }

            tokenPersistence.save(token)

            Toast.makeText(this, "${token.getLabel()} added ", Toast.LENGTH_LONG).show()
        } catch (e: InvalidParameterException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        } catch (e: Token.TokenUriInvalidException) {
            Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_LONG).show()
        }
    }
}
