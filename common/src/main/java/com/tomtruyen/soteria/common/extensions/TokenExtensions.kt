package com.tomtruyen.soteria.common.extensions

import com.tomtruyen.soteria.common.data.entities.Token
import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.apache.commons.codec.binary.Base32
import java.util.concurrent.TimeUnit

fun Token.generateTOTP(): String {
    val config = TimeBasedOneTimePasswordConfig(
        codeDigits = digits,
        hmacAlgorithm = HmacAlgorithm.SHA1,
        timeStep = period,
        timeStepUnit = TimeUnit.SECONDS
    )

    return TimeBasedOneTimePasswordGenerator(Base32().decode(secret), config).generate(System.currentTimeMillis())
}

fun Token.getIssuer(): String {
    val username = getUsername()

    return label.replace("($username)", "").trim()
}

fun Token.getUsername(): String {
    return label.substringAfter("(").substringBefore(")").trim()
}