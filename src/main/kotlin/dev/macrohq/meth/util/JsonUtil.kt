package dev.macrohq.meth.util

import com.google.gson.Gson
import dev.macrohq.meth.util.Logger.log
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

object JsonUtil {
  private val gson = Gson()
  private fun fileExists(fileName: String): Boolean = File(fileName).exists()
  private fun readFile(fileName: String): String = if (!fileExists(fileName)) "" else File(fileName).readText()
  fun getMap(fileName: String): Map<String, String> {
    val file = File(fileName)
    if (!file.exists() || file.readText().isEmpty()) return emptyMap()
    return gson.fromJson(readFile(fileName), Map::class.java) as Map<String, String>
  }

  fun updateJson(fileName: String, jsonData: Map<String, String>) {
    val file = File(fileName)

    if (!file.exists()) {
      log("in here")
      log(file.parentFile.mkdirs())
      log(file.createNewFile())
    }
    val fileData = if (file.readText().isEmpty()) mapOf<String, String>()
    else gson.fromJson(file.readText(), Map::class.java)

    file.writeText(gson.toJson(fileData + jsonData))
    log("path: ${file.path}")
  }
}