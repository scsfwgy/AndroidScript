package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import java.io.File
import java.util.function.Consumer

fun main() {
    //KeyConvertCore.scanCore()
    //KeyConvertCore.stringXmlList2Set()
    //KeyConvertCore.value2NewValue()
    KeyConvertCore.appStringsXml2WrapperStringsXml()
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
        FileUtilsWrapper.scanDirList(
            listOf(
                "/Users/matt.wang/AndroidStudioProjects/Android-LBK/app/src/main",
                "/Users/matt.wang/AndroidStudioProjects/Android-LBK/lib_wrapper/src/main"
            ),
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
                                                println("====>" + file.path + "," + file.name + "," + line)
                                            }
                                            //找不大就不处理
                                            newKey ?: it
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

                    value2NewValue()
                }
            })
    }

    /**
     * 对strings.xml去重的同时，内容部分替换为产品给的新文案
     */
    fun value2NewValue() {
        println("===========对strings.xml去重的同时，内容部分替换为产品给的新文案============")
        val excel2StringXml =
            ExcelCore.excel2StringXml("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/android5_auto.xls")
        excel2StringXml.forEach { module ->
            println(module.key)
            module.value.forEach { l ->
                val kvList = l.value
                println(l.key + "," + kvList.size)
                val map = HashMap<String, String>()
                kvList.forEach {
                    map[it.first] = it.second
                }
                val filePath = FileConfig.getFullDefaultValuesPath(module.key, l.key)
                val file = File(filePath)
                //转换过程已经去重
                val stringsXml2Map = XmlCore.stringsXml2Map(filePath)
                val list = ArrayList<Pair<String, String>>()
                stringsXml2Map.forEach {
                    val key = it.key
                    val newValue = map[key]
                    if (newValue == null) {
                        println("未找到新内容：" + key + "," + it.value)
                    }
                    if (newValue != it.value) {
                        println("找到文案有变化内容：" + key + "：" + it.value + "=====>>>>" + newValue)
                    }
                    list.add(Pair(key, newValue ?: it.value))
                }
                val newFileContent = XmlCore.pairList2StringXml(list)
                file.writeText(newFileContent)
            }
        }
    }

    /**
     * 将app下的strings.xml语言配置文件移动并合并到wrapper对应的文件下
     */
    fun appStringsXml2WrapperStringsXml() {
        println("===========将app下的strings.xml语言配置文件移动并合并到wrapper对应的文件下============")
        FileConfig.languageDirNameList.forEach { languageTriple ->
            val stringsXmlPathList =
                FileConfig.moduleList.map { module ->
                    FileConfig.getFullDefaultValuesPath(
                        module,
                        languageTriple.first
                    )
                }
            val mapList = stringsXmlPathList.map { XmlCore.stringsXml2Map(it) }
            val map = LinkedHashMap<String, String>()
            mapList.forEach { kv ->
                map.putAll(kv)
            }
            val sortMap2StringXml = XmlCore.sortMap2StringXml(map)

            //wrapper
            val wrapperValuesPath =
                FileConfig.getFullDefaultValuesPath(FileConfig.moduleList[1], languageTriple.first)
            val file = File(wrapperValuesPath)
            file.writeText(sortMap2StringXml)

            //del app
            val appValuesPath =
                FileConfig.getFullDefaultValuesPath(FileConfig.moduleList[0], languageTriple.first)
            File(appValuesPath).delete()
        }

    }

}