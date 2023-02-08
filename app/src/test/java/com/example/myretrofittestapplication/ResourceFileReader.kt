package com.example.myretrofittestapplication

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Read a file from the folder test/resources
 */
object ResourceFileReader {

    fun read(path: String): String {
        val file = javaClass.classLoader?.getResourceAsStream(path)
        val reader = InputStreamReader(file, StandardCharsets.UTF_8)
        val content = reader.readText()
        reader.close()
        return content
    }
}
