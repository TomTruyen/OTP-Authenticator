package com.tomtruyen.otpauthenticator.android.models

import android.net.Uri
import dev.turingcomplete.kotlinonetimepassword.*
import org.apache.commons.codec.binary.Base32
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac


class Token {
    class TokenUriInvalidException(message: String) : Exception(message)

    enum class TokenType {
        HOTP, TOTP
    }

    var issuerInt : String = ""
    var issuerExt : String = ""
    private var issuerAlt : String = ""
    private var label : String = ""
    private var labelAlt : String = ""
    var image : String = ""
    var imageAlt : String = ""
    var type: TokenType = TokenType.TOTP
    var algo : String = ""
    var secret  : ByteArray = byteArrayOf()
    var digits : Int = 0
    var counter : Long = 0L
    var period : Int = 0

    @Throws(TokenUriInvalidException::class)
    constructor(uri: Uri, internal: Boolean) {
        validateTokenURI(uri)

        var path  = uri.path ?: ""
        // Remove leading '/'
        path = path.replace("/", "")

        if(path.isEmpty()) throw TokenUriInvalidException("Path lenght too short")

        val i : Int = path.indexOf(':')
        issuerExt = if(i < 0) "" else path.substring(0, i)
        issuerInt = uri.getQueryParameter("issuer") ?: ""
        label = path.substring(if(i >= 0) i + 1 else 0)

        algo = uri.getQueryParameter("algorithm") ?: ""
        if(algo == "") algo = "sha1"
        algo = algo.uppercase(Locale.US)

        try {
            Mac.getInstance("Hmac$algo")
        } catch (e: NoSuchAlgorithmException) {
            throw TokenUriInvalidException("")
        }

        try {
            var d = uri.getQueryParameter("digits")
            if(d == null) d = "6"
            digits = Integer.parseInt(d)
            if(!issuerExt.equals("Steam") && digits != 6 && digits != 8) throw TokenUriInvalidException("")
        } catch (e: NumberFormatException) {
            throw TokenUriInvalidException("")
        }

        try {
            var p = uri.getQueryParameter("period")
            if(p == null) p = "30"
            period = Integer.parseInt(p)
            period = if(period > 0) period else 30
        } catch (e: NumberFormatException) {
            throw TokenUriInvalidException("")
        }

        if(type == TokenType.HOTP) {
            try {
                var c = uri.getQueryParameter("counter")
                if(c == null) c = "0"
                counter = c.toLong()
            } catch (e: NumberFormatException) {
                throw TokenUriInvalidException("")
            }
        }

        try {
            val s = uri.getQueryParameter("secret")
            secret = Base32().decode(s)
        } catch (e: Exception) {
            throw TokenUriInvalidException("")
        }

        image = uri.getQueryParameter("image") ?: ""

        if(internal) {
            setIssuer(uri.getQueryParameter("issueralt") ?: "")
            setLabel(uri.getQueryParameter("labelalt") ?: "")
        }
    }

    private fun validateTokenURI(uri : Uri) {
        if(uri.scheme == null || !uri.scheme.equals("otpauth")) throw TokenUriInvalidException("Invalid scheme")

        if(uri.authority == null) throw TokenUriInvalidException("Missing authority")

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

        if(uri.path == null) throw TokenUriInvalidException("Missing path")
    }

    private fun setIssuer(issuer : String) {
        issuerAlt = if(issuer == issuerExt) "" else issuer
    }

    private fun setLabel(label: String) {
        labelAlt = if(label == this.label) "" else label
    }

    fun getLabel() : String {
        return label
    }

    fun getID(): String {
        return if (issuerInt != "") "$issuerInt:$label" else if (issuerExt != "") "$issuerExt:$label" else label
    }

    fun generateCode() : String {
        if(type == TokenType.HOTP) return generateHOTP()

        return generateTOTP()
    }

    private fun generateHOTP() : String {
        val config = HmacOneTimePasswordConfig(codeDigits = digits, hmacAlgorithm = HmacAlgorithm.SHA1)
        val hmacOneTimePasswordGenerator = HmacOneTimePasswordGenerator(secret, config)
        return hmacOneTimePasswordGenerator.generate(counter)
    }

    private fun generateTOTP() : String {
        val config = TimeBasedOneTimePasswordConfig(codeDigits = digits, hmacAlgorithm = HmacAlgorithm.SHA1, timeStep = period.toLong(), timeStepUnit = TimeUnit.SECONDS)
        val timeBasedOneTimePasswordGenerator = TimeBasedOneTimePasswordGenerator(secret, config)
        return timeBasedOneTimePasswordGenerator.generate(System.currentTimeMillis())
    }

    fun debug() {
        println("=== START DEBUG TOKEN ===")
        println("issuerInt: $issuerInt")
        println("issuerExt: $issuerExt")
        println("issuerAlt: $issuerAlt")
        println("label: $label")
        println("labelAlt: $labelAlt")
        println("image: $image")
        println("imageAlt: $imageAlt")
        println("type: $type")
        println("algo: $algo")
        println("secret: ${String(secret)}")
        println("digits: $digits")
        println("counter: $counter")
        println("period: $period")
        println("=== END DEBUG TOKEN ===")
    }
}