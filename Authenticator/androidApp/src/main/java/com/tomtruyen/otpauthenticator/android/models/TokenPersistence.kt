package com.tomtruyen.otpauthenticator.android.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class TokenPersistence(ctx: Context) {
    private val NAME = "tokens"
    private val ORDER = "tokenOrder"
    private var gson: Gson = Gson()
    private var prefs: SharedPreferences = ctx.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    private fun getTokenOrder(): List<String> {
        val type: Type = object : TypeToken<List<String?>?>() {}.type
        val str = prefs.getString(ORDER, "[]")
        val order = gson.fromJson<List<String>>(str, type)
        return order ?: LinkedList<String>()
    }

    private fun setTokenOrder(order: List<String>): SharedPreferences.Editor? {
        return prefs.edit()?.putString(ORDER, gson.toJson(order))
    }

    fun getAll() : List<Map<String, String>> {

        val keys = prefs.all

        val tokenItems : MutableList<Map<String, String>> = mutableListOf()

        for ((key, value) in keys) {

            println("Key: $key - Value: $value")

            if(key != ORDER) {
                val token = gson.fromJson(value.toString(), Token::class.java)

                val item = TokenItem(token.getLabel(), token.generateCode())

                tokenItems.add(item.toHashMap())
            }
        }

        return tokenItems
    }

    fun save(token: Token) : Boolean {
        val key : String = token.getID()


        if(prefs.contains(key)) {
            prefs.edit().putString(key, gson.toJson(token)).apply()
            return true
        }

        val order = getTokenOrder().toMutableList()
        order.add(0, key)
        setTokenOrder(order.toList())?.putString(key, gson.toJson(token))?.apply()

        return true
    }
}