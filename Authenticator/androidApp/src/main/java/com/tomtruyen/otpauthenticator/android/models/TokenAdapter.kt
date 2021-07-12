package com.tomtruyen.otpauthenticator.android.models

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.*
import com.tomtruyen.otpauthenticator.android.R
import java.time.LocalDateTime


class TokenAdapter(private val ctx: Context) : BaseAdapter() {
    private val tokenPersistence: TokenPersistence = TokenPersistence(ctx)
    private val clipboardManager: ClipboardManager =
        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    var seconds: Int
    var percentage: Int = 100
    var shouldGenerateToken: Boolean = true

    init {
        seconds = getSecondsUntilRefresh()
    }

    fun getSecondsUntilRefresh() : Int{
        val secondsElapsedInMinute = LocalDateTime.now().second
        return if(secondsElapsedInMinute < 30) 30 - secondsElapsedInMinute else 60 - secondsElapsedInMinute
    }

    override fun getCount(): Int {
        return tokenPersistence.length()
    }

    override fun getItem(position: Int): Token? {
        return tokenPersistence.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = if (convertView == null) {
            val inflater: LayoutInflater =
                    ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_item, parent, false)
        } else {
            convertView
        }

        val t: Token = getItem(position)!!

        val title: TextView = v.findViewById(R.id.list_item_title)
        val code: TextView = v.findViewById(R.id.list_item_subtitle)
        title.text = t.getLabel()
        if (shouldGenerateToken) {
            var generatedCode : String = t.generateCode()
            generatedCode = generatedCode.substring(0, 3) + " " + generatedCode.substring(3, generatedCode.length)
            code.text = generatedCode
            shouldGenerateToken = false
        }

        val countdown: ProgressBar = v.findViewById(R.id.progress_circular)
        val countdownText: TextView = v.findViewById(R.id.progress_circular_text)

        countdownText.text = seconds.toString()
        countdown.progress = percentage

        v.setOnClickListener {
            val copyText = code.text.split(' ').joinToString("")
            println("===================")
            println("Copied: $copyText")

            val clip: ClipData = ClipData.newPlainText("2FA Code", copyText)
            clipboardManager.setPrimaryClip(clip)

            Toast.makeText(ctx, "Copied: $copyText to clipboard", Toast.LENGTH_LONG).show()
        }

        return v
    }
}