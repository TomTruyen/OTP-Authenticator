package com.tomtruyen.otpauthenticator.android

import android.app.Activity
import android.content.*
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.WindowManager.LayoutParams
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.tomtruyen.otpauthenticator.android.databinding.ActivityMainBinding
import com.tomtruyen.otpauthenticator.android.models.token.Token
import com.tomtruyen.otpauthenticator.android.models.token.TokenAdapter
import com.tomtruyen.otpauthenticator.android.models.token.TokenPersistence
import java.security.InvalidParameterException


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mTokenAdapter: TokenAdapter
    private lateinit var mDatasetObserver: DataSetObserver
    private lateinit var mClipboardManager: ClipboardManager
    private var mSelectedTokenPosition: Int = 0
    private var mActionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding Setup
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        // Toolbar Setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Don't allow screenshots
        window.setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE)

        // Initiate ClipboardManager
        mClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Listview Click Listener
        val listview = findViewById<ListView>(R.id.tokenList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val token = mTokenAdapter.getItem(position)

            if (token != null) {
                val code = token.generateCode()
                val clip: ClipData = ClipData.newPlainText("2FA Code", code)
                mClipboardManager.setPrimaryClip(clip)
                Toast.makeText(this, "Copied: $code", Toast.LENGTH_SHORT).show()
            }
        }

        listview.setOnItemLongClickListener { _, _, position: Int, _ ->
            mSelectedTokenPosition = position

            if (mActionMode != null) {
                false
            }

            mActionMode = startSupportActionMode(mActionModeCallback)
            true
        }


        // FAB Item Click Listeners
        mBinding.qrButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }

        mBinding.setupKeyButton.setOnClickListener {
            val intent = Intent(this, TokenSetupKeyActivity::class.java)
            startActivity(intent)
        }

        // TokenAdapter setup
        mTokenAdapter = TokenAdapter(this)
        mBinding.tokenList.adapter = mTokenAdapter

        mDatasetObserver = object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                if (mTokenAdapter.count == 0) {
                    findViewById<View>(android.R.id.empty).visibility = View.VISIBLE
                } else {
                    findViewById<View>(android.R.id.empty).visibility = View.GONE
                }
            }
        }

        mTokenAdapter.registerDataSetObserver(mDatasetObserver)

        startTimer()
    }

    override fun onResume() {
        super.onResume()
        mTokenAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        mTokenAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTokenAdapter.unregisterDataSetObserver(mDatasetObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_default_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.actionSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    addToken(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    // ActionMode
    private var mActionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.appbar_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val token = mTokenAdapter.getItem(mSelectedTokenPosition)


            return when (item.itemId) {
                R.id.actionDelete -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                        .setTitle("Delete \"${token?.getLabel()}\"")
                        .setMessage("Are you sure you want to delete \"${token?.getLabel()}\"? \n\nNOTE: This could result in you losing access to the account!")
                        .setPositiveButton("Remove account") { _, _ ->
                            val tokenPersistence = TokenPersistence(this@MainActivity)
                            tokenPersistence.delete(mSelectedTokenPosition)
                            Toast.makeText(
                                this@MainActivity,
                                "${token?.getLabel()} deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()

                    val primaryColor =
                        ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(primaryColor)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(primaryColor)
                    mode.finish()
                    true
                }
                R.id.actionEdit -> {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    val layoutInflater = LayoutInflater.from(this@MainActivity)
                    val view: View = layoutInflater.inflate(R.layout.edit_alert_dialog, null)
                    builder.setTitle("Rename")
                    builder.setView(view)
                    builder.create()

                    builder.setNegativeButton("Cancel", null)
                    builder.setPositiveButton(
                        "Rename"
                    ) { _, _ ->
                        val inputLayout = view.findViewById(R.id.renameText) as TextInputLayout
                        val newLabel = inputLayout.editText?.text.toString()
                        if (token != null) {
                            token.rename(newLabel)

                            val tokenPersistence = TokenPersistence(this@MainActivity)
                            tokenPersistence.save(token)
                        }
                    }
                    builder.show()

                    mode.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mActionMode = null
        }
    }


    // Refresh Timer
    private fun startTimer() {
        object : CountDownTimer(mTokenAdapter.getSecondsUntilRefresh().toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val seconds = mTokenAdapter.getSecondsUntilRefresh()
                val percentage = (seconds.toDouble() / 30) * 100

                mTokenAdapter.percentage = percentage.toInt()
                mTokenAdapter.seconds = seconds
                mTokenAdapter.notifyDataSetChanged()

            }

            override fun onFinish() {
                mTokenAdapter.shouldGenerateToken = true
                this.start()
            }
        }.start()
    }

    // Add token on QR Code Scan
    private fun addToken(uri: String) {
        try {
            val token = Token(Uri.parse(uri), false)

            val tokenPersistence = TokenPersistence(this)

            val saved = tokenPersistence.save(token)

            if (saved) {
                Toast.makeText(this, "${token.getLabel()} added ", Toast.LENGTH_SHORT).show()
            }
        } catch (e: InvalidParameterException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Token.TokenUriInvalidException) {
            Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
