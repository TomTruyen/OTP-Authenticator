package com.tomtruyen.soteria.android

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.tomtruyen.soteria.android.models.DatabaseService
import com.tomtruyen.soteria.android.models.Globals

class LockScreenActivity : AppCompatActivity() {
    private var mPin : String = ""
    private var mDigitImageList: ArrayList<ImageView> = arrayListOf()
    private lateinit var mDatabaseService: DatabaseService
    private lateinit var mDigitLayout: LinearLayout
    private var mIsEnablePasscode: Boolean = false
    private var mEnablePasscode: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        // Check if it is to enable the password
        val isEnable = intent.extras?.getBoolean("isEnable", false)

        if(isEnable != null && isEnable) {
            findViewById<TextView>(R.id.enterPasswordTextview).text = "Enter a passcode"
            val backButton = findViewById<ImageButton>(R.id.backButton)
            backButton.visibility = View.VISIBLE
            backButton.setOnClickListener { finish() }

            mIsEnablePasscode = true
        }

        // Setup DatabaseService
        mDatabaseService = DatabaseService(this)

        // Set DigitLayout
        mDigitLayout = findViewById(R.id.digitLayout)

        // ButtonClickListeners
        findViewById<Button>(R.id.btnZero).setOnClickListener { updatePin(0) }
        findViewById<Button>(R.id.btnOne).setOnClickListener { updatePin(1) }
        findViewById<Button>(R.id.btnTwo).setOnClickListener { updatePin(2) }
        findViewById<Button>(R.id.btnThree).setOnClickListener { updatePin(3) }
        findViewById<Button>(R.id.btnFour).setOnClickListener { updatePin(4) }
        findViewById<Button>(R.id.btnFive).setOnClickListener { updatePin(5) }
        findViewById<Button>(R.id.btnSix).setOnClickListener { updatePin(6) }
        findViewById<Button>(R.id.btnSeven).setOnClickListener { updatePin(7) }
        findViewById<Button>(R.id.btnEight).setOnClickListener { updatePin(8) }
        findViewById<Button>(R.id.btnNine).setOnClickListener { updatePin(9) }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { updatePin(-1) }

        // Setup Digit ImageView List
        val digit1 = findViewById<ImageView>(R.id.digit1)
        val digit2 = findViewById<ImageView>(R.id.digit2)
        val digit3 = findViewById<ImageView>(R.id.digit3)
        val digit4 = findViewById<ImageView>(R.id.digit4)
        val digit5 = findViewById<ImageView>(R.id.digit5)

        mDigitImageList.add(digit1)
        mDigitImageList.add(digit2)
        mDigitImageList.add(digit3)
        mDigitImageList.add(digit4)
        mDigitImageList.add(digit5)
    }

    override fun onBackPressed() {
        if(mIsEnablePasscode) {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updatePin(digit: Int) {
        if(digit == -1) {
            mPin = mPin.dropLast(1)
        } else if (mPin.length < mDigitImageList.size) {

            mPin += digit.toString()
        }

        if(mPin.length == mDigitImageList.size) {
            if(mIsEnablePasscode) {
                if(mEnablePasscode != "") {
                    if(mPin == mEnablePasscode) {
                        mDatabaseService.savePin(mPin)

                        Toast.makeText(this, "Passcode enabled", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // shake animation and toast "failed"
                        mEnablePasscode = ""
                        mPin = ""

                        val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
                        mDigitLayout.startAnimation(animation)

                        Toast.makeText(this, "Passcodes don't match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    mEnablePasscode = mPin
                    mPin = ""
                    findViewById<TextView>(R.id.enterPasswordTextview).text = "Repeat your passcode"

                    Toast.makeText(this, "Repeat your passcode to confirm", Toast.LENGTH_SHORT).show()
                }
            } else {
                unlock()
            }
        }

        updatePinLayout()
    }

    private fun updatePinLayout() {
        val digitCount = mPin.length

        for(i in mDigitImageList.indices) {
            val imageView = mDigitImageList[i]

            if(i < digitCount) {
                imageView.setImageResource(R.drawable.pin_circle_filled)
            } else {
                imageView.setImageResource(R.drawable.pin_circle)
            }
        }
    }

    private fun unlock() {
        val pin = mDatabaseService.readPin()

        if(pin != null && pin == mPin) {
            Globals.isLoggedIn = true
            finish()
        } else {
            val animation = AnimationUtils.loadAnimation(this, R.anim.shake)
            mDigitLayout.startAnimation(animation)

            mPin = ""

            Toast.makeText(this, "Incorrect passcode", Toast.LENGTH_SHORT).show()
        }
    }
}
