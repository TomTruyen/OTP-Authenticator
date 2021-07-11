package com.tomtruyen.otpauthenticator.android.models

import android.content.ClipboardManager
import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tomtruyen.otpauthenticator.android.R


class TokenAdapter(private val ctx: Context) : BaseAdapter() {
    private val tokenPersistence: TokenPersistence = TokenPersistence(ctx)
    private val clipboardManager: ClipboardManager =
        ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val tokenCodes: HashMap<String, TokenCode> = HashMap()

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



        return v
    }
}