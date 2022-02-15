package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import java.io.File
import java.util.function.Consumer

fun main() {
    KeyConvertCore.scanCore()
}

object KeyConvertCore {
    val debug = true
    val unFindKeySet = mutableSetOf<String>()
    val processErrorFileList = mutableSetOf<String>()


    /**
     * 各个文件对应的解析规则
     */
    fun fileLineRegex(file: File): String? {
        val fileName = file.name
        val pair = FileUtilsWrapper.splitFileByDot(file)
        val second = pair.second
        return if (second == "java" || second == "kt") {
            RegexUtilsWrapper.javaOrKtPureKeyRegex
        } else if (fileName == "strings.xml") {
            RegexUtilsWrapper.stringXmlPureKeyRegex
        } else if (second == "xml") {
            RegexUtilsWrapper.xmlPureKeyRegex
        } else if (second == "m") {
            RegexUtilsWrapper.iosPureKeyRegex
        } else {
            null
        }
    }

    fun scanCore() {
        val excel2Map =
            ExcelCore.excel2Map(File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/keyTOkey_auto.xls"), 0, 1)
        val oldKey2NewKeyMap = HashMap<String, String>()
        excel2Map.forEach {
            oldKey2NewKeyMap[it.key] = it.value[1]
        }
        val noNewKeyValue = "noNewKeyValue"
        FileUtilsWrapper.scanDirList("/Users/matt.wang/AndroidStudioProjects/Android-LBK/lib_wrapper",
            linePretreatment = object : LinePretreatment {
                override fun line2NewLine(file: File, line: String, lineIndex: Int, lineSize: Int): String {
                    val placeholder = "~~~~~~~~~"
                    val special = "%"
                    //val pLine = RegexUtils.getReplaceAll(line, RegexUtilsWrapper.iosSpecialRegex, "~~~~~~~~~")
                    val pLine = line.replace(special, placeholder)
                    var errorType = false
                    val finalLine = RegexUtilsWrapper.line2FormatLine(
                        pLine,
                        fileLineRegex(file),
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
                                        println("出现异常（后续操作继续）：${file.path},line:$line" + e.localizedMessage)
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
                    return finalLine.replace(placeholder, special)
                }
            },
            fileFilter = object : FileFilter {
                override fun filter(file: File): Boolean {
                    val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                    val second = splitFileByDot.second
                    //只替换这些文件
                    //return second != null && (second == "java" || second == "kt")
                    //return second != null && (second == "m")
                    //return second != null && (second == "xml")
                    //return second != null && file.name.equals("strings.xml")
                    return second == "java" || second == "kt" || second == "xml" || second == "m"
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