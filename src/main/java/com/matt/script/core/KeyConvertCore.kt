package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.config.LogWrapper
import com.matt.script.core.interfaces.FileFilter
import com.matt.script.core.interfaces.FormatLineConvert
import com.matt.script.core.interfaces.LinePretreatment
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.RegexUtils
import java.io.File
import java.util.function.Consumer

fun main() {
    //KeyConvertCore.scanCore()
    //KeyConvertCore.stringXmlList2Set()
    //KeyConvertCore.value2NewValue()
    //KeyConvertCore.appStringsXml2WrapperStringsXml()
    //KeyConvertCore.removeUnUseStringXmlKey()
    //KeyConvertCore.stringXmlMarkFlag()
    //ExcelCore.androidLbkXml2Excel()
    // KeyConvertCore.lbkExcel2StringXml()
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
            RegexUtilsWrapper.javaOrKtPureStringKeyRegex
        } else if (fileName == "strings.xml") {
            RegexUtilsWrapper.stringXmlPureKeyRegex
        } else if (second == "xml") {
            RegexUtilsWrapper.xmlPureStringKeyRegex
        } else if (second == "m") {
            RegexUtilsWrapper.iosPureKeyRegex
        } else {
            null
        }
    }

    fun getOldKey2NewKeyMap(): Map<String, String> {
        val listList = ExcelUtils.baseExcel2StringXml(
            "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/Android多语言自动化抽取转Excel_2022-03-08_13-40-32---2.xlsx",
            beginRow = 1
        )
        val map = HashMap<String, String>()
        listList.forEach {
            val key = it.getOrNull(1)
            val value = it.getOrNull(2)
            val skip = key.isNullOrEmpty() || value.isNullOrEmpty()
            if (!skip) {
                map[key ?: ""] = value ?: ""
            }
        }
        return map
    }

    /**
     * 新版本旧key替换，更加简单粗暴
     */
    fun oldKey2NewKey() {
        val debug = false
        val oldKey2NewKeyMap = getOldKey2NewKeyMap()
        val list = if (debug) {
            listOf("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp")
        } else {
            listOf(
                "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
                "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main"
            )
        }
        val fileList = FileUtilsWrapper.scanDirList(list, fileFilter = object : FileFilter {
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
        })
        fileList.forEach { file ->
            val lineRegex = fileLineRegex(file)
            val readTextStr = file.readText()
            val keyList = RegexUtils.getMatches(lineRegex, readTextStr)
            if (keyList.isEmpty()) {
                LogWrapper.loggerWrapper(this).debug("该文件没有任何变化：" + file.path)
            } else {
                val placeholder = "~~~~~~~~~"
                val special = "%"
                val tempContent = readTextStr.replace(special, placeholder)
                val newFormatTxt = RegexUtils.getReplaceAll(tempContent, lineRegex, "%s")
                if (debug) {
                    LogWrapper.loggerWrapper(this).debug("格式化后文案：" + newFormatTxt)
                }
                val newKeyList = keyList.map {
                    val newKey = oldKey2NewKeyMap[it]
                    if (newKey != null) {
                        LogWrapper.loggerWrapper(this).debug("替换新key：" + it + "===>>>" + newKey + "," + file.path)
                    }
                    newKey ?: it
                }
                val newContentTxt = try {
                    newFormatTxt.format(*newKeyList.toTypedArray())
                } catch (e: Exception) {
                    LogWrapper.loggerWrapper(this).debug("格式化出现问题：" + file.path, e)
                    throw e
                }
                LogWrapper.loggerWrapper(this).debug("准备覆写文件：该文件总共" + keyList.size + "个格式化文案，" + file.path)
                file.writeText(newContentTxt.replace(placeholder, special))
            }
        }
        //开始回写values
        if (!debug) {
            tryOldKey2NewKey()
        }
    }

    @Deprecated("废弃")
    fun scanCore() {
        val oldKey2NewKeyMap = getOldKey2NewKeyMap()
        val noNewKeyValue = "noNewKeyValue"
        FileUtilsWrapper.scanDirListByLine(
            listOf(
                "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
                "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main"
            ),
            linePretreatment = object : LinePretreatment {
                override fun line2NewLine(
                    file: File,
                    fileContent: String,
                    line: String,
                    lineIndex: Int,
                    lineSize: Int
                ): String {
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

                    tryOldKey2NewKey()
                }
            })
    }

    /**
     * 把strings.xml中的老key替换为新key
     */
    fun tryOldKey2NewKey() {
        println("===========把strings.xml中的老key替换为新key============")
        val oldKey2NewKeyMap = getOldKey2NewKeyMap()

        FileConfig.languageDirNameList.forEach { languageTriple ->
            val stringsXmlPath = FileConfig.getFullDefaultValuesPath(
                FileConfig.moduleList[1],
                languageTriple.first
            )
            val second = languageTriple.second
            val suffix = second.substring(second.length - 2)
            val realSuffix = "_" + suffix
            val map = XmlCore.stringsXml2SortedMap(stringsXmlPath)
            val newMap = LinkedHashMap<String, String>()
            map.forEach {
                val key = it.key
                val newKey = oldKey2NewKeyMap[key]
                val value = it.value
                //移除语言后缀
                val finalValue = value.replace(realSuffix, "")
                newMap[newKey ?: key] = finalValue
            }

            val sortMap2StringXml = XmlCore.sortMap2StringXml(newMap)

            //wrapper
            val wrapperValuesPath =
                FileConfig.getFullDefaultValuesPath(FileConfig.moduleList[1], languageTriple.first)
            val file = File(wrapperValuesPath)
            file.writeText(sortMap2StringXml)
        }
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
                val stringsXml2Map = XmlCore.stringsXml2SortedMap(filePath)
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
            val mapList = stringsXmlPathList.map { XmlCore.stringsXml2SortedMap(it) }
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

    /**
     * 找到未使用的语言key并移除
     */
    fun removeUnUseStringXmlKey() {
        val stringXml =
            File("/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res/values/strings.xml")
        val findKeySet = HashSet<String>()
        val stringsXml2SortedMap =
            XmlCore.stringsXml2SortedMap(stringXml.path)
//        stringsXml2SortedMap.forEach {
//            println(it.key + "-->" + it.value)
//        }
        FileUtilsWrapper.scanDirListByLine(listOf(
            "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/MPChartLib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/mycommonlib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/faceidmodule/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lbankcorelib/src/main",
        ), object : LinePretreatment {
            fun findKeyFun(keyList: List<String>) {
                keyList.forEach {
                    val s = stringsXml2SortedMap[it]
                    if (s != null) {
                        findKeySet.add(it)
                    }
                }
            }

            override fun line2NewLine(
                file: File,
                fileContent: String,
                line: String,
                lineIndex: Int,
                lineSize: Int
            ): String {
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                val second = splitFileByDot.second
                if (second == "java" || second == "kt") {
                    val list = RegexUtils.getMatches(RegexUtilsWrapper.javaOrKtPureStringKeyRegex, line)
                    if (list.isNotEmpty()) {
                        findKeyFun(list)
                    }
                } else if (second == "xml") {
                    val list = RegexUtils.getMatches(RegexUtilsWrapper.xmlPureStringKeyRegex, line)
                    if (list.isNotEmpty()) {
                        findKeyFun(list)
                    }
                }
                return line
            }
        }, object : FileFilter {
            override fun filter(file: File): Boolean {
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                val second = splitFileByDot.second
                return second == "java" || second == "kt" || second == "xml" || second == "m"
            }
        }, object : Consumer<Boolean> {
            override fun accept(t: Boolean) {
                println("====处理完毕===")
                val unFindKeySet = stringsXml2SortedMap.keys.toMutableSet()
                unFindKeySet.removeAll(findKeySet)
                println("未使用列表：" + findKeySet)
                println("使用列表：" + unFindKeySet)

//                println("====开始回写=====")
//                val stringMap = stringsXml2SortedMap.filter { findKeySet.contains(it.key) }
//                val sortMap2StringXml = XmlCore.sortMap2StringXml(stringMap)
//                stringXml.writeText(sortMap2StringXml)
//                println("====回写结束=====")
            }
        })
    }

    /**
     * 对所有文案打标记，方便测试
     */
    fun stringXmlMarkFlag() {
        println("===========对所有文案打标记，方便测试============")
        FileConfig.languageDirNameList.forEach { languageTriple ->
            val stringsXmlPath = FileConfig.getFullDefaultValuesPath(
                FileConfig.moduleList[1],
                languageTriple.first
            )
            val second = languageTriple.second
            val suffix = second.substring(second.length - 2)
            val map = XmlCore.stringsXml2SortedMap(stringsXmlPath)
            val newMap = LinkedHashMap<String, String>()
            map.forEach {
                newMap[it.key] = it.value + "_" + suffix
            }

            val sortMap2StringXml = XmlCore.sortMap2StringXml(newMap)

            //wrapper
            val wrapperValuesPath =
                FileConfig.getFullDefaultValuesPath(FileConfig.moduleList[1], languageTriple.first)
            val file = File(wrapperValuesPath)
            file.writeText(sortMap2StringXml)
        }
    }

    /**
     * 将Excel到处为项目用的语言配置 Excel=>strings.xml List
     */
    fun lbkExcel2StringXml() {
        println("===========将Excel到处为项目用的语言配置============")
        val baseExcel2StringXml =
            ExcelUtils.baseExcel2StringXml("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/Android多语言自动化抽取转Excel_2022-02-21_15-24-39.xlsx")
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug(baseExcel2StringXml.size.toString() + "," + baseExcel2StringXml.firstOrNull()?.size)
    }

}