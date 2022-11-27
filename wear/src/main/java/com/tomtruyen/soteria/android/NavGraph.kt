package com.tomtruyen.soteria.android

object NavGraph {
    const val Tokens = "tokens"
    const val TokenDetail = "token_detail/{id}"
    fun toTokenDetail(id: String) = "token_detail/$id"
}