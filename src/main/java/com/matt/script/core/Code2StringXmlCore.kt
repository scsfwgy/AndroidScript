package com.matt.script.core

import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import java.io.File
import java.util.function.Consumer

fun main() {
    Code2StringXmlCore.test()
}

object Code2StringXmlCore {
    var count = 0

    fun test() {
        val map = LinkedHashMap<String, String>()
        map["key1"] = "模拟已存在key_kt"
        map["key2"] = "测试重复key_xml"
        code2StringXmlCore(
            listOf("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp"),
            map,
            "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/values/strings.xml",
            object : FileLineParse {
                override fun parse(file: File): Triple<String?, String?, PlaceHolderFilter?>? {
                    val fileByDot = FileUtilsWrapper.splitFileByDot(file)
                    return when (fileByDot.second) {
                        "java", "kt" -> {
                            return Triple(
                                RegexUtilsWrapper.containsZhRegex, """
                                            MyContextUtils2.getString(R.string.%s)
                                        """.trimIndent(), object : PlaceHolderFilter {
                                    override fun placeholder2RealWords(placeholder: String): String {
                                        return placeholder.replace(
                                            """
                                            "
                                        """.trimIndent(), ""
                                        )
                                    }

                                }
                            )
                        }
                        "xml" -> {
                            return Triple(
                                RegexUtilsWrapper.xmlContainerZhContentRegex,
                                """@string/%s""".trimIndent(),
                                object : PlaceHolderFilter {
                                    override fun placeholder2RealWords(placeholder: String): String {
                                        return placeholder
                                    }

                                }
                            )
                        }
                        else -> {
                            null
                        }
                    }
                }

            }
        )
    }

    /**
     * 入口
     */
    fun code2StringXmlCore(
        scanPathList: List<String>,
        sortedLanguageMap: Map<String, String>,
        outputStringXmlPath: String,
        fileLineParse: FileLineParse,
    ) {
        val oldKey2NewKeyMap = sortedLanguageMap.toMutableMap()

        //新生成的key集合
        val newKey = LinkedHashSet<String>()

        FileUtilsWrapper.scanDirList(scanPathList,
            object : LinePretreatment {
                override fun line2NewLine(file: File, line: String, lineIndex: Int, lineSize: Int): String {
                    val parsePair = fileLineParse.parse(file)
                    val first = parsePair?.first ?: return line
                    val second = parsePair.second ?: return line
                    val placeHolderFilter = parsePair.third ?: return line
                    return RegexUtilsWrapper.line2FormatLine(
                        line,
                        first,
                        formatLineConvert = object : FormatLineConvert {
                            override fun formatLine2NewLine(
                                formatLine: String,
                                placeholderList: List<String>?
                            ): String {
                                if (placeholderList.isNullOrEmpty()) return formatLine
                                val valueList = placeholderList.map {
                                    val pureWord = placeHolderFilter.placeholder2RealWords(it)
                                    val generateNewKey = value2StringKeyAuto(file, pureWord, oldKey2NewKeyMap)
                                    val key = generateNewKey.second
                                    if (generateNewKey.first) {
                                        newKey.add(key)
                                    }
                                    second.format(key)
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
                    //return second != null && (second == "java" || second == "kt")
                    //return second != null && (second == "m")
                    //return second != null && (second == "xml")
                    return true
                }
            },
            scanFinishConsumer = object : Consumer<Boolean> {
                override fun accept(t: Boolean) {
                    println("====================解析结束===========================")
                    println(newKey.map { it + "===>" + oldKey2NewKeyMap[it] })

                    //开始写入xml
                    val pairList2StringXml = XmlCore.pairList2StringXml(oldKey2NewKeyMap.toList())
                    println(pairList2StringXml)

                    println("------")

                    val stringXmlPath = FileUtilsWrapper.getFileByCreate(outputStringXmlPath)
                    stringXmlPath.writeText(pairList2StringXml)
                    println("操作完成：" + stringXmlPath)
                }
            })
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
        val key = file.name + "_" + System.currentTimeMillis() + "_" + count++
        if (map[key] != null) {
            throw IllegalAccessException("新生成的key已存在！！！=》" + key)
        }
        return key
    }
}