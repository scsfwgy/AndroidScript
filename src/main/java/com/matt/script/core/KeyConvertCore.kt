package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.FileUtils
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
    KeyConvertCore.test()
}

object KeyConvertCore {
    val debug = true

    fun test() {
        val excel2Map =
            ExcelCore.excel2Map(File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/keyTOkey.xls"), 0, 1)
        val newMap = HashMap<String, String>()
        excel2Map.forEach {
            newMap[it.key] = it.value[1]
        }
        //println(newMap)
        runKeyConvertCore("/Users/matt.wang/AsProject/Android-LBK/app/src/main/java/com/superchain/lbanknew/ui",
            RegexUtilsWrapper.javaOrKtPureKeyRegex,
            newMap,
            noNewKeyValue = "noNewKeyValueYouCanGlobalSearchMe",
            fileFilter = object : FileFilter {
                override fun filter(file: File): Boolean {
                    val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                    val second = splitFileByDot.second
                    //只替换这些文件
                    return second != null && (second == "java" || second == "kt")
                }

            })
    }

    fun runKeyConvertCore(
        scanPathDir: String,
        pureKeyRegular: String,
        oldKey2NewKeyMap: Map<String, String>,
        noNewKeyValue: String?,
        fileFilter: FileFilter? = null,
        filterSuffix: String? = null,
        filterPrefix: String? = null,
    ) {
        val listFileByPath = FileUtilsWrapper.listFileByPath(scanPathDir, filterSuffix, filterPrefix)
        listFileByPath.forEach {
            if (fileFilter != null && !fileFilter.filter(it)) {
                if (debug) {
                    println("该文件已被fileFilter过滤，不进行解析")
                }
                return@forEach
            }
            convert(it, pureKeyRegular, oldKey2NewKeyMap, noNewKeyValue)
        }
    }

    fun convert(
        file: File,
        pureKeyRegular: String,
        oldKey2NewKeyMap: Map<String, String>,
        noNewKeyValue: String?,
    ) {
        if (!file.exists()) {
            throw IllegalAccessException("要操作的文件不存在：$file")
        }
        if (oldKey2NewKeyMap.isEmpty()) {
            throw IllegalAccessException("新旧key字典为空：$oldKey2NewKeyMap")
        }

        val backUpSuffix = ".backUp"
        val tempFile = File(file.parentFile.path, file.name + backUpSuffix)
        //清空
        tempFile.writeText("")
        val readLines = file.readLines()
        readLines.forEachIndexed { index, line ->
            val keyList = RegexUtils.getMatches(pureKeyRegular, line)
            if (keyList.isNotEmpty()) {
                val newFormatLine = RegexUtils.getReplaceAll(line, pureKeyRegular, "%s")
                var newLine = "该行解析错误,请排查具体原因=====>>>" + line
                try {
                    newLine = String.format(
                        newFormatLine, *keyList.map { oldKey2NewKeyMap[it] ?: (noNewKeyValue ?: it) }.toTypedArray()
                    )
                } catch (e: Exception) {
                    println("出现异常（后续操作继续）：${file.path},line:$index" + e.localizedMessage)
                    e.printStackTrace()
                }
                if (debug) {
                    println("老Line:$line")
                    println("格式化Line:：$newFormatLine")
                    println("新Line:$newLine")
                }
                tempFile.appendText(newLine)
            } else {
                tempFile.appendText(line)
            }
            //写入默认,最后一行就不要写入换行符
            if (index != readLines.size - 1) {
                tempFile.appendText("\n")
            }
        }

        //覆盖老文件
        val name = file.name
        FileUtils.delete(file)
        FileUtils.rename(tempFile, name)
    }

}