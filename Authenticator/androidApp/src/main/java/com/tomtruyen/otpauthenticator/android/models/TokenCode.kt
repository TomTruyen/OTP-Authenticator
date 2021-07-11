package com.tomtruyen.otpauthenticator.android.models

class TokenCode {
    private var mCode: String? = null
    private var mStart: Long = 0
    private var mUntil: Long = 0
    private var mNext: TokenCode? = null

    constructor(code: String?, start: Long, until: Long) {
        mCode = code
        mStart = start
        mUntil = until
    }

    constructor(prev: TokenCode, code: String?, start: Long, until: Long) {
        TokenCode(code, start, until)
        prev.mNext = this
    }

    constructor(code: String?, start: Long, until: Long, next: TokenCode?) {
        TokenCode(code, start, until)
        mNext = next
    }

    fun getCurrentCode(): String? {
        val active = getActive(System.currentTimeMillis()) ?: return null
        return active.mCode
    }

    fun getTotalProgress(): Int {
        val cur = System.currentTimeMillis()
        val total = getLast().mUntil - mStart
        val state = total - (cur - mStart)
        return (state * 1000 / total).toInt()
    }

    fun getCurrentProgress(): Int {
        val cur = System.currentTimeMillis()
        val active = getActive(cur) ?: return 0
        val total = active.mUntil - active.mStart
        val state = total - (cur - active.mStart)
        return (state * 1000 / total).toInt()
    }

    private fun getActive(curTime: Long): TokenCode? {
        if (curTime in mStart until mUntil) return this
        return if (mNext == null) null else mNext!!.getActive(curTime)
    }

    private fun getLast(): TokenCode {
        return if (mNext == null) this else mNext!!.getLast()
    }
}