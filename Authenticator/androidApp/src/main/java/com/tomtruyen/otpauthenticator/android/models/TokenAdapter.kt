package com.tomtruyen.otpauthenticator.android.models

import android.content.ClipboardManager
import android.content.Context
import android.database.DataSetObserver
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tomtruyen.otpauthenticator.android.R
import kotlin.math.abs


class TokenAdapter(private val ctx: Context) : BaseAdapter() {
    private val tokenPersistence: TokenPersistence = TokenPersistence(ctx)
    private val clipboardManager: ClipboardManager =
        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val tokenCodes: HashMap<String, TokenCode> = HashMap()
    var seconds : Int = 30
    var percentage : Int = 100
    var shouldGenerateToken : Boolean = true


    init {
        registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                tokenCodes.clear()
            }

            override fun onInvalidated() {
                tokenCodes.clear()
            }
        })
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
        if(shouldGenerateToken) {
            code.text = t.generateCode()
            shouldGenerateToken = false
        }

        val countdown : ProgressBar = v.findViewById(R.id.progress_circular)
        val countdownText : TextView = v.findViewById(R.id.progress_circular_text)

        countdownText.text = seconds.toString()
        countdown.progress = percentage

        return v
    }
}