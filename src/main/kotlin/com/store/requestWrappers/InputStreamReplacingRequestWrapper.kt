package com.store.requestWrappers

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper

class InputStreamReplacingRequestWrapper(request: HttpServletRequest, private val newInputStream: InputStream)
    : HttpServletRequestWrapper(request) {

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            private val reader = BufferedReader(InputStreamReader(newInputStream))

            override fun isFinished(): Boolean = newInputStream.available() == 0
            override fun isReady(): Boolean = true
            override fun setReadListener(listener: ReadListener?) {}

            override fun read(): Int = reader.read()
        }
    }
}