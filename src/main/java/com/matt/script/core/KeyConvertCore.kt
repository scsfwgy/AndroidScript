package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.RegexUtils
import java.io.File
import java.util.function.Consumer

fun main() {
    KeyConvertCore.scanCore()
}

object KeyConvertCore {
    val debug = true
    val unFindKeySet = mutableSetOf<String>()
    val processErrorFileList = mutableSetOf<String>()

    fun scanCore() {
        val excel2Map =
            ExcelCore.excel2Map(File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/keyTOkey.xls"), 0, 1)
        val oldKey2NewKeyMap = HashMap<String, String>()
        excel2Map.forEach {
            oldKey2NewKeyMap[it.key] = it.value[1]
        }
        val noNewKeyValue = "noNewKeyValue"
        FileUtilsWrapper.scanDirList("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp",
            linePretreatment = object : LinePretreatment {
                override fun line2NewLine(file: File, line: String, lineIndex: Int, lineSize: Int): String {
                    val pLine = RegexUtils.getReplaceAll(line, RegexUtilsWrapper.iosSpecialRegex, "~~~~~~~~~")
                    //val pLine = line
                    var errorType = false
                    val finalLine = RegexUtilsWrapper.line2FormatLine(
                        pLine,
                        RegexUtilsWrapper.iosPureKeyRegex,
                        formatLineConvert = object : FormatLineConvert {
                            override fun formatLine2NewLine(
                                formatLine: String,
                                placeholderList: List<String>?
                            ): String {
                                if (placeholderList == null) {
                                    return formatLine
                                } else {
                                    var finalLine = "该行解析错误,请排查具体原因=====>>>" + pLine
                                    try {
                                        finalLine = formatLine.format(*placeholderList.map {
                                            val newKey = oldKey2NewKeyMap[it]
                                            if (newKey == null) {
                                                unFindKeySet.add(it)
                                            }
                                            newKey ?: noNewKeyValue
                                        }.toTypedArray())
                                    } catch (e: Exception) {
                                        errorType = true
                                        println("出现异常（后续操作继续）：${file.path},line:$lineIndex" + e.localizedMessage)
                                        e.printStackTrace()
                                    }
                                    return finalLine
                                }
                            }

                        })
                    //最后一行
                    if (lineIndex == lineSize - 1) {
                        if (errorType) {
                            processErrorFileList.add(file.path)
                        }
                    }
                    return finalLine
                }
            },
            fileFilter = object : FileFilter {
                override fun filter(file: File): Boolean {
                    val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                    val second = splitFileByDot.second
                    //只替换这些文件
                    //return second != null && (second == "java" || second == "kt")
                    return second != null && (second == "m")
                    //return second != null && (second == "xml")
                }
            },
            scanFinishConsumer = object : Consumer<Boolean> {
                override fun accept(t: Boolean) {
                    println("====================解析结束===========================")
                    println("=======打印未找到新key的旧key集合========")
                    println(unFindKeySet)
                    println("=======打印出错的文件集合，注意排查处理========")
                    println(processErrorFileList)
                }
            })
    }

}