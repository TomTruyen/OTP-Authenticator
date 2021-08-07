package com.tomtruyen.soteria.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class LockScreenActivity : AppCompatActivity() {
    private var mPin : String = ""
    private var mDigitImageList: ArrayList<ImageView> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

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

    // Disable back press
    override fun onBackPressed() {}

    private fun updatePin(digit: Int) {
        if(digit == -1) {
            mPin = mPin.dropLast(1)
        } else if (mPin.length < mDigitImageList.size) {

            mPin += digit.toString()
        }

        println(mPin)

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
}
