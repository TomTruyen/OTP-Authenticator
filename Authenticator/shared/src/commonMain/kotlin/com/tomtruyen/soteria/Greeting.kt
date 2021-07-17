package com.tomtruyen.soteria

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}