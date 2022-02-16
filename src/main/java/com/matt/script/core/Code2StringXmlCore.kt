package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import java.io.File
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

fun main() {
    Code2StringXmlCore.test()
}

object Code2StringXmlCore {

    fun test() {
        val excel2Map =
            ExcelCore.excel2Map(File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/keyTOkey.xls"), 0, 1)

        val oldKey2NewKeyMap=XmlCore.stringsXml2Map("/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res/values/strings.xml").toMutableMap()
        //支持添加
//        val oldKey2NewKeyMap = HashMap<String, String>()
//        excel2Map.forEach {
//            oldKey2NewKeyMap[it.key] = it.value[1]
//        }
        //新生成的key集合
        val newKey = LinkedHashSet<String>()

        FileUtilsWrapper.scanDirList(
            listOf("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp"),
            object : LinePretreatment {
                override fun line2NewLine(file: File, line: String, lineIndex: Int, lineSize: Int): String {
                    val regex = """
                        ".*?"
                    """.trimIndent()
                    return RegexUtilsWrapper.line2FormatLine(
                        line,
                        regex,
                        formatLineConvert = object : FormatLineConvert {
                            override fun formatLine2NewLine(
                                formatLine: String,
                                placeholderList: List<String>?
                            ): String {
                                if (placeholderList.isNullOrEmpty()) return formatLine
                                val valueList = placeholderList.map {
                                    val pureWord = it.substring(1, it.length - 1)
                                    if (!RegexUtilsWrapper.hasChinese(pureWord)) {
                                        it
                                    } else {
                                        val generateNewKey = value2StringKeyAuto(file, pureWord, oldKey2NewKeyMap)
                                        val key = generateNewKey.second
                                        if (generateNewKey.first) {
                                            newKey.add(key)
                                        }
                                        """MyContextUtils22222.getString(R.string.${key})"""
                                    }

                                }.toTypedArray()
                                return formatLine.format(*valueList)
                            }
                        })
                }
            },
            fileFilter = object : FileFilter {
                override fun filter(file: File): Boolean {
                    val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                    val second = splitFileByDot.second
                    //只替换这些文件
                    return second != null && (second == "java" || second == "kt")
                    //return second != null && (second == "m")
                    //return second != null && (second == "xml")
                }
            },
            scanFinishConsumer = object : Consumer<Boolean> {
                override fun accept(t: Boolean) {
                    println("====================解析结束===========================")
                    println(newKey.map { it + "===>" + oldKey2NewKeyMap[it] })

                    //开始写入xml
                    val pairList2StringXml = XmlCore.pairList2StringXml(oldKey2NewKeyMap.toList())
                    println(pairList2StringXml)
                }
            })
    }

//    fun line2SortMap(
//        file: File,
//        line: String,
//        valueRegex: String,
//        map: MutableMap<String, String>,
//    ): MutableMap<String, String> {
//        return RegexUtilsWrapper.line2FormatLine(line, valueRegex, formatLineConvert = object : FormatLineConvert {
//            override fun formatLine2NewLine(formatLine: String, placeholderList: List<String>?): String {
//
//            }
//
//        })
//    }

    fun value2StringKeyWrapper(file: File, value: String, map: MutableMap<String, String>): String {
        val key = value2StringKey(file, value, map)
        return """MyContextUtils.getString(R.string.${key})"""
    }

    fun value2StringKeyAuto(file: File, value: String, map: MutableMap<String, String>): Pair<Boolean, String> {
        val value2StringKey = value2StringKey(file, value, map)
        val key = value2StringKey.second
        if (value2StringKey.first) {
            map[key] = value
        }
        return Pair(value2StringKey.first, key)
    }

    fun value2StringKey(file: File, value: String, map: Map<String, String>): Pair<Boolean, String> {
        val key = map.filterValues { it == value }.keys.firstOrNull()
        return if (key == null) {
            val generateNewKey = generateNewKey(file, map)
            Pair(true, generateNewKey)
        } else {
            Pair(false, key)
        }
    }

    fun generateNewKey(file: File, map: Map<String, String>): String {
        val key = file.name + "_" + System.currentTimeMillis() + Random().nextDouble()
        if (map[key] != null) {
            throw IllegalAccessException("新生成的key已存在！！！=》" + key)
        }
        return key
    }
}