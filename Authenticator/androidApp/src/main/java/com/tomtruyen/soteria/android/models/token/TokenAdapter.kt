package com.tomtruyen.soteria.android.models.token

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.tomtruyen.soteria.android.R
import com.tomtruyen.soteria.android.models.DatabaseService
import java.time.LocalDateTime


class TokenAdapter(private val ctx: Context) : BaseAdapter() {
    val databaseService: DatabaseService = DatabaseService(ctx)
    var seconds: Int
    var percentage: Int = 100

    init {
        seconds = getSecondsUntilRefresh()
    }

    fun getSecondsUntilRefresh(): Int {
        val secondsElapsedInMinute = LocalDateTime.now().second
        return if (secondsElapsedInMinute < 30) 30 - secondsElapsedInMinute else 60 - secondsElapsedInMinute
    }

    override fun getCount(): Int {
        return databaseService.tokenLength()
    }

    // Get Item at Index
    override fun getItem(position: Int): Token {
        return databaseService.readOneToken(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Update view
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = if (convertView == null) {
            val inflater: LayoutInflater =
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.list_item, parent, false)
        } else {
            convertView
        }

        val t: Token = getItem(position)

        val title: TextView = v.findViewById(R.id.list_item_title)
        val code: TextView = v.findViewById(R.id.list_item_subtitle)

        title.text = t.getLabel()

        // Show Formatted Generated Code
        var generatedCode: String = t.generateCode()
        generatedCode = generatedCode.substring(0, 3) + " " + generatedCode.substring(
            3,
            generatedCode.length
        )
        code.text = generatedCode

        val progressBar: ProgressBar = v.findViewById(R.id.progress_circular)
        val countdownText: TextView = v.findViewById(R.id.progress_circular_text)

        countdownText.text = seconds.toString()
        progressBar.progress = percentage

        return v
    }
}