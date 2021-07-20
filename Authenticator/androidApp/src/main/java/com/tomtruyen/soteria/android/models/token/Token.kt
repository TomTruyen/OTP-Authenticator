package com.tomtruyen.soteria.android.models.token

import android.net.Uri
import dev.turingcomplete.kotlinonetimepassword.*
import org.apache.commons.codec.binary.Base32
import java.security.InvalidParameterException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac


class Token {
    class TokenUriInvalidException(message: String) : Exception(message)

    enum class TokenType {
        HOTP, TOTP
    }

    var id: String = UUID.randomUUID().toString()
    private var issuerInt: String = ""
    private var issuerExt: String = ""
    private var issuerAlt: String = ""
    private var label: String = ""
    private var labelAlt: String = ""
    private var image: String = ""
    var type: TokenType = TokenType.TOTP
    private var algo: String = ""
    private var secret: ByteArray = byteArrayOf()
    private var digits: Int = 0
    private var counter: Long = 0L
    private var period: Int = 0

    constructor(label: String, key: String) {
        this.label = label
        labelAlt = label
        algo = "sha1"
        secret = Base32().decode(key)
        digits = 6
        period = 30
    }

    @Throws(TokenUriInvalidException::class, InvalidParameterException::class)
    constructor(uri: Uri, internal: Boolean) {
        validateTokenURI(uri)

        var path = uri.path ?: ""
        // Remove leading '/'
        path = path.replace("/", "")

        if (path.isEmpty()) throw TokenUriInvalidException("Path length too short")

        val i: Int = path.indexOf(':')
        issuerExt = if (i < 0) "" else path.substring(0, i)
        issuerInt = uri.getQueryParameter("issuer") ?: ""

        label = if(issuerExt != "") {
            "$issuerExt (${path.substring(if (i >= 0) i + 1 else 0)})"
        } else {
            path.substring(if (i >= 0) i + 1 else 0)
        }

        algo = uri.getQueryParameter("algorithm") ?: ""
        if (algo == "") algo = "sha1"
        algo = algo.uppercase(Locale.US)

        try {
            Mac.getInstance("Hmac$algo")
        } catch (e: NoSuchAlgorithmException) {
            throw TokenUriInvalidException("")
        }

        try {
            var d = uri.getQueryParameter("digits")
            if (d == null) d = "6"
            digits = Integer.parseInt(d)
            if (issuerExt != "Steam" && digits != 6 && digits != 8) throw TokenUriInvalidException(
                ""
            )
        } catch (e: NumberFormatException) {
            throw TokenUriInvalidException("")
        }

        try {
            var p = uri.getQueryParameter("period")
            if (p == null) p = "30"
            period = Integer.parseInt(p)
            period = if (period > 0) period else 30
        } catch (e: NumberFormatException) {
            throw TokenUriInvalidException("")
        }

        if (type == TokenType.HOTP) {
            throw InvalidParameterException("HOTP tokens are not supported")
        }

        try {
            val s = uri.getQueryParameter("secret")
            secret = Base32().decode(s)
        } catch (e: Exception) {
            throw TokenUriInvalidException("")
        }

        image = uri.getQueryParameter("image") ?: ""

        if (internal) {
            setIssuer(uri.getQueryParameter("issueralt") ?: "")
            setLabel(uri.getQueryParameter("labelalt") ?: "")
        }
    }

    private fun validateTokenURI(uri: Uri) {
        if (uri.scheme == null || !uri.scheme.equals("otpauth")) throw TokenUriInvalidException("Invalid scheme")

        if (uri.authority == null) throw TokenUriInvalidException("Missing authority")

        type = when {
            uri.authority.equals("totp") -> {
                TokenType.TOTP
            }
            uri.authority.equals("hotp") -> {
                TokenType.HOTP
            }
            else -> {
                throw TokenUriInvalidException("Authority type not found")
            }
        }

        if (uri.path == null) throw TokenUriInvalidException("Missing path")
    }

    private fun setIssuer(issuer: String) {
        issuerAlt = if (issuer == issuerExt) "" else issuer
    }

    private fun setLabel(label: String) {
        labelAlt = if (label == this.label) "" else label
    }

    fun rename(value: String) {
        label = value
        setLabel(value)
    }

    fun getLabel(): String {
        return label
    }

    fun generateCode(): String {
        if (type == TokenType.HOTP) return generateHOTP()

        return generateTOTP()
    }

    private fun generateHOTP(): String {
        val config =
            HmacOneTimePasswordConfig(codeDigits = digits, hmacAlgorithm = HmacAlgorithm.SHA1)
        val hmacOneTimePasswordGenerator = HmacOneTimePasswordGenerator(secret, config)
        return hmacOneTimePasswordGenerator.generate(counter)
    }

    private fun generateTOTP(): String {
        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = digits,
            hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = period.toLong(),
            timeStepUnit = TimeUnit.SECONDS
        )
        val timeBasedOneTimePasswordGenerator = TimeBasedOneTimePasswordGenerator(secret, config)
        return timeBasedOneTimePasswordGenerator.generate(System.currentTimeMillis())
    }

    fun isEqual(token: Token): Boolean {
        if (token.id == id) return true

        if (token.secret.contentEquals(secret) && token.issuerInt == issuerInt && token.issuerExt == token.issuerExt && token.issuerAlt == token.issuerAlt) return true

        return false
    }
}