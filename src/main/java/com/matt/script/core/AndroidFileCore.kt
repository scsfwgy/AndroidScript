package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.FileUtils
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
//    val scanFiles =
//        AndroidFileCore.scanFiles("/Users/matt.wang/AsProject/Android-LBK/app/src/main/java/com/superchain/lbanknew/ui/activity")
//    scanFiles.forEach {
//        AndroidFileCore.parsingJavaOrKtFile(it)
//    }

    //AndroidFileCore.parsingJavaOrKtFile(File("/Users/matt.wang/IdeaProjects/AndroidScript/src/main/java/com/matt/script/AndroidScript.kt"))

    val ios = """(?<=RDLocalizedString\(@")[a-zA-Z0-9\u4e00-\u9fa5_.\\、。"]+(?="\))"""

    val suffix = ".backUp"
    val keyRegular = """[a-zA-Z0-9_.]+"""
    val classStringRegular = """(?<=R.string\.)$keyRegular"""

    val xmlStringRegular = """text="@string/$keyRegular"""

    val iosfile1=File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/NoticeSetViewController.m")
    val file1 = File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/LbkAppealActivity.kt")
    val newFil2 = File(file1.parentFile.path, file1.name + suffix)

    val file2 = File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/wallet_activity_add_address.xml")
    newFil2.writeText("")
    iosfile1.readLines().forEach {
        val keyList = RegexUtils.getMatches(ios, it)
        if (keyList.isNotEmpty()) {
            val newFormatLine = RegexUtils.getReplaceAll(it, ios, "%s")
            val newLine = String.format(newFormatLine, *keyList.map { "newKey" }.toTypedArray())
            println("老Line:" + it)
            println("格式化Line:：" + newFormatLine)
            println("新Line:" + newLine)
            newFil2.appendText(newLine + "\n")
        } else {
            //写入默认
            newFil2.appendText(it + "\n")
        }
    }

    //覆盖老文件
//    val name = file1.name
//    FileUtils.delete(file1)
//    FileUtils.rename(newFil2, name)

    println("-------------")

//    file2.readLines().forEach {
//        val matches = RegexUtils.getMatches(xmlStringRegular, it)
//        if (matches.isNotEmpty()) {
//            matches.forEach { item ->
//                val oldKey = item.replace("""text="@string/""", "")
//                println(oldKey)
//                val newKey = "newKey"
//            }
//        }
//    }
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