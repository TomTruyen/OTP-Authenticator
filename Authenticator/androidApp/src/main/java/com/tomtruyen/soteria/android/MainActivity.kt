package com.tomtruyen.soteria.android

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.DataSetObserver
import android.net.Uri
import android.os.*
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.tomtruyen.soteria.android.databinding.ActivityMainBinding
import com.tomtruyen.soteria.android.models.token.Token
import com.tomtruyen.soteria.android.models.token.TokenAdapter
import com.tomtruyen.soteria.android.models.token.TokenPersistence
import java.security.InvalidParameterException


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mTokenAdapter: TokenAdapter
    private lateinit var mDatasetObserver: DataSetObserver
    private lateinit var mClipboardManager: ClipboardManager
    private lateinit var mQrResultLauncher : ActivityResultLauncher<Intent>
    private var mSelectedTokenPosition: Int = 0
    private var mActionMode: ActionMode? = null

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 998
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding Setup
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)

        // Toolbar Setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Don't allow screenshots
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        // Initiate ClipboardManager
        mClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // StartActivityResult Launchers
        mQrResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val result = IntentIntegrator.parseActivityResult(it.resultCode, it.data)
                if (result.contents != null) {
                    addToken(result.contents)
                } else {
                    Toast.makeText(this, "Failed to scan QR", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Listview Click Listener
        val listview = findViewById<ListView>(R.id.tokenList)

        listview.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _ ->
            val token = mTokenAdapter.getItem(position)

            val code = token.generateCode()
            val clip: ClipData = ClipData.newPlainText("2FA Code", code)
            mClipboardManager.setPrimaryClip(clip)
            Toast.makeText(this, "Copied: $code", Toast.LENGTH_SHORT).show()
        }

        listview.setOnItemLongClickListener { _, _, position: Int, _ ->
            mSelectedTokenPosition = position

            supportActionBar?.hide()
            mActionMode = startSupportActionMode(mActionModeCallback)

            true
        }

        // FAB Item Click Listeners
        mBinding.qrButton.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setPrompt("Place the QR code inside the rectangle")
            scanner.setBeepEnabled(false)
            mQrResultLauncher.launch(scanner.createScanIntent())
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

        hasWriteStoragePermission()

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
        mTokenAdapter.tokenPersistence.close()
        mTokenAdapter.unregisterDataSetObserver(mDatasetObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_default_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
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
                        .setTitle("Delete \"${token.getLabel()}\"")
                        .setMessage("Are you sure you want to delete \"${token.getLabel()}\"? \n\nNOTE: This could result in you losing access to the account!")
                        .setPositiveButton("Remove account") { _, _ ->
                            val tokenPersistence = TokenPersistence(this@MainActivity)
                            tokenPersistence.delete(mSelectedTokenPosition)
                            Toast.makeText(
                                this@MainActivity,
                                "${token.getLabel()} deleted",
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
                        token.rename(newLabel)

                        val tokenPersistence = TokenPersistence(this@MainActivity)
                        tokenPersistence.save(token)
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
            supportActionBar?.show()
        }
    }


    // Refresh Timer
    private fun startTimer() {
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                val seconds = mTokenAdapter.getSecondsUntilRefresh()

                val percentage = (seconds.toDouble() / 30) * 100

                mTokenAdapter.percentage = percentage.toInt()
                mTokenAdapter.seconds = seconds
                mTokenAdapter.notifyDataSetChanged()

                if(seconds <= 1 || seconds >= 29) {
                    mTokenAdapter.shouldGenerateToken = true
                }

                handler.postDelayed(this, 1000)
            }
        })
    }

    // Add token on QR Code Scan
    private fun addToken(uri: String) {
        try {
            val token = Token(Uri.parse(uri), false)

            val tokenPersistence = TokenPersistence(this)

            tokenPersistence.save(token)

            Toast.makeText(this, "${token.getLabel()} added ", Toast.LENGTH_SHORT).show()
        } catch (e: InvalidParameterException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Token.TokenUriInvalidException) {
            Toast.makeText(this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasWriteStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )

                return false
            }
        }

        return true
    }
}
