package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.blankj.FileUtils
import java.io.File

fun main() {
    val scanFiles =
        AndroidFileCore.scanFiles("/Users/matt.wang/AsProject/Android-LBK/app/src/main/java/com/superchain/lbanknew/ui/activity")
    scanFiles.forEach {
        AndroidFileCore.parsingJavaOrKtFile(it)
    }

    //AndroidFileCore.parsingJavaOrKtFile(File("/Users/matt.wang/IdeaProjects/AndroidScript/src/main/java/com/matt/script/AndroidScript.kt"))
}

object AndroidFileCore {
    fun scanFiles(scanDirPath: String): List<File> {
        val file = File(scanDirPath)
        if (!file.exists()) throw IllegalAccessException("文件路径不存在")
        return FileUtilsWrapper.listFileByPath(scanDirPath)
    }

    fun parsingJavaOrKtFile(file: File) {
        val readLine = FileUtilsWrapper.readLine(file)
        readLine.forEach {
            if (it.contains("R.string.")) {
                println(it)
            }
        }
    }
}