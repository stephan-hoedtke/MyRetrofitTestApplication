package com.example.myretrofittestapplication

import java.io.InputStreamReader

object MockResponseFileReader {

    fun read(path: String): String {
        val file = javaClass.classLoader?.getResourceAsStream(path)
        val reader = InputStreamReader(file)
        val content = reader.readText()
        reader.close()
        return content
    }
}
