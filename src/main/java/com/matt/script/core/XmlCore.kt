package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.config.LogWrapper
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
    XmlCore.iOSLbkExcel2StringXmlDebug()
}

object XmlCore {

    const val TAG = "CoreWrapper"

    fun absStringXml2NewStringXml(languageDir: String, newSortKeySet: Set<String>, iosType: Boolean) {
        LogWrapper.loggerWrapper(XmlCore.javaClass)
            .debug("===========将Excel到处为项目用的语言配置 Excel=>strings.xml List============")
        LogWrapper.loggerWrapper(this).debug("导出路径：$languageDir,扫描路径：${newSortKeySet.size},iOS?:$iosType")

        val tripleList = if (iosType) {
            FileConfig.languageDirNameListIOS
        } else {
            FileConfig.languageDirNameList
        }
        tripleList.forEachIndexed { index, triple ->
            val dir = FileUtilsWrapper.getDirByCreate(languageDir + "/" + triple.first)
            val fileName = if (iosType) FileConfig.stringsXmlFileNameIOS else FileConfig.stringsXmlFileName
            val fullPath = "$dir/$fileName"
            val fullPatFile = FileUtilsWrapper.getFileByCreate(fullPath)
            val oldMap = absStringXml2SortMap(fullPath, iosType)
            val newSortMap = LinkedHashMap<String, String>()
            newSortKeySet.forEach {
                newSortMap[it] = oldMap[it] ?: it
            }
            val pairList2StringXml = asbMap2StringXml(newSortMap, iosType)
            fullPatFile.writeText(pairList2StringXml)
            LogWrapper.loggerWrapper(XmlCore.javaClass).debug("输入路径：" + fullPatFile.path)
        }
    }

    fun absLbkExcel2StringXml(languageDir: String, excelPath: String, iosType: Boolean) {
        LogWrapper.loggerWrapper(XmlCore.javaClass)
            .debug("===========将Excel到处为项目用的语言配置 Excel=>strings.xml List============")
        LogWrapper.loggerWrapper(this).debug("导出路径：$languageDir,扫描路径：$excelPath,iOS?:$iosType")
        val baseExcel2StringXml = ExcelUtils.baseExcel2StringXml(excelPath)
        LogWrapper.loggerWrapper(this)
            .debug(baseExcel2StringXml.size.toString() + "," + baseExcel2StringXml.firstOrNull()?.size)

        val tripleList = ExcelCore.loadDefaultLanguageList()
        tripleList.forEachIndexed { index, triple ->
            val dir = FileUtilsWrapper.getDirByCreate(languageDir + "/" + triple.languageDir(iosType))
            val fileName = if (iosType) FileConfig.stringsXmlFileNameIOS else FileConfig.stringsXmlFileName
            val fullPath = "$dir/$fileName"
            val fullPatFile = FileUtilsWrapper.getFileByCreate(fullPath)
            val pairList = baseExcel2StringXml.filterIndexed { index2, _ -> index2 > 0 }.map { itemList ->
                val defaultKey = itemList[1]
                //val key = itemList[1]
                val newKey = itemList[2]
                val realKey = if (newKey.isNullOrEmpty()) defaultKey ?: "no key" else newKey
                val value = itemList[index + 3]
                //取不到就取英文
                val finalValue = if (!value.isNullOrEmpty()) {
                    value
                } else {
                    itemList[3]
                }
                Pair(realKey, finalValue)
            }
            val pairList2StringXml = if (iosType) iOSPairList2StringXml(pairList) else pairList2StringXml(pairList)
            fullPatFile.writeText(pairList2StringXml)
            LogWrapper.loggerWrapper(XmlCore.javaClass).debug("输入路径：" + fullPatFile.path)
        }

    }

    fun iOSLbkExcel2StringXmlDebug() {
        iosLbkExcel2StringXml(
            "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/temp",
            "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/RDLocalizable2Excel/iOS多语言自动化抽取转Excel_2022-03-11_15-05-36.xlsx"
        )
    }

    /**
     * 将Excel到处为项目用的语言配置 Excel=>strings.xml List
     */
    fun iosLbkExcel2StringXml(languageDir: String, excelPath: String) {
        absLbkExcel2StringXml(languageDir, excelPath, true)
    }

    fun androidLbkExcel2StringXml(languageDir: String, excelPath: String) {
        absLbkExcel2StringXml(languageDir, excelPath, false)
    }

    fun stringsXml2SortedMap(stringsXmlPath: String): Map<String, String> {
        val file = File(stringsXmlPath)
        if (!file.exists()) {
            throw IllegalAccessException("stringsXml2Map文件不存在:" + stringsXmlPath)
        }
        val sortedMap = LinkedHashMap<String, String>()
        val readText = file.readText()
        val matches = RegexUtilsWrapper.lines2StringXmlLineList(readText)
        matches.forEachIndexed { index, s ->
            //if (index > 10) return@forEachIndexed
            val k = RegexUtilsWrapper.line2StringXmlLineKey(s)
            val v = RegexUtilsWrapper.line2StringXmlLineValue(s)
            //LogUtils.loggerWrapper(this).debug(k + "==>" + v)
            sortedMap[k] = v
        }
        return sortedMap
    }

    fun asbStringsXml2SortKeyList(stringsXmlPath: String, iosType: Boolean): Set<String> {
        return absStringXml2SortMap(stringsXmlPath, iosType).keys
    }

    fun sortMap2StringXml(map: Map<String, String>): String {
        val list = ArrayList<Pair<String, String>>()
        map.forEach {
            list.add(Pair(it.key, it.value))
        }
        return pairList2StringXml(list)
    }

    fun iOSSortMap2StringXml(map: Map<String, String>): String {
        val list = ArrayList<Pair<String, String>>()
        map.forEach {
            list.add(Pair(it.key, it.value))
        }
        return iOSPairList2StringXml(list)
    }

    fun iOSPairList2StringXml(list: List<Pair<String, String?>>): String {
        val map = LinkedHashMap<String, String>()
        list.forEach {
            map[it.first] = it.second ?: ""
        }
        return map2RDLocalizableStr(map)
    }

    fun asbMap2StringXml(sortMap: Map<String, String>, iosType: Boolean): String {
        return if (iosType) iOSSortMap2StringXml(sortMap) else sortMap2StringXml(sortMap)
    }

    fun pairList2StringXml(list: List<Pair<String, String?>>): String {
        //AS默认的格式化格式
        return """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    ${pairList2Lines(list)}
</resources> 
        """.trimIndent()
    }

    private fun pairList2Lines(list: List<Pair<String, String?>>): String {
        //AS默认的格式化格式
        return list.joinToString(separator = "\n" + "    ") { pair2StringLine(it) }
    }

    private fun pair2StringLine(pair: Pair<String, String?>): String {
        return """<string name="${pair.first}">${pair.second}</string>""".trimIndent()
    }

    fun absStringXml2SortMap(filePath: String, iosType: Boolean = false): Map<String, String> {
        return if (iosType) rDLocalizable2SortMap(filePath) else stringsXml2SortedMap(filePath)
    }

    /**
     * iOS配置文件转化为排好序的map
     */
    fun rDLocalizable2SortMap(rDLocalizablePath: String): Map<String, String> {
        val readLines = File(rDLocalizablePath).readLines()
        val map = LinkedHashMap<String, String>()
        readLines.forEach {
            if (it.trim().isNotEmpty()) {
                val keyList = RegexUtils.getMatches(RegexUtilsWrapper.iosRDLocalizableKey, it)
                val valueList = RegexUtils.getMatches(RegexUtilsWrapper.iosRDLocalizableValue, it)
                if (keyList.size != 1 || valueList.size != 1) throw IllegalAccessException("解析错误请排查：" + keyList + "，" + valueList + "====>>>" + it)
                map[keyList.first()] = valueList.first()
            }
        }
        return map
    }

    fun map2RDLocalizableStr(map: Map<String, String>): String {
        var msg = ""
        map.keys.forEachIndexed { index, key ->
            msg += kv2RDLocalizableLine(key, map[key])
            if (index != map.size - 1) {
                msg += "\n"
            }
        }
        return msg
    }

    fun kv2RDLocalizableLine(k: String, v: String?): String {
        return """
            "$k"="$v";
        """.trimIndent()
    }
}