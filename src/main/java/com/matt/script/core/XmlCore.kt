package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.config.LogWrapper
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import java.io.File

object XmlCore {

    const val TAG = "CoreWrapper"

    fun lbkExcel2StringXmlDebug() {
        val path =
            "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Excel2Xml/" + FileUtilsWrapper.defaultTimeName()
        lbkExcel2StringXml(path)
    }

    /**
     * 将Excel到处为项目用的语言配置 Excel=>strings.xml List
     */
    fun lbkExcel2StringXml(resPathDir: String) {
        LogWrapper.loggerWrapper(XmlCore.javaClass)
            .debug("===========将Excel到处为项目用的语言配置 Excel=>strings.xml List============")
        val baseExcel2StringXml =
            ExcelUtils.baseExcel2StringXml("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/Android多语言自动化抽取转Excel_2022-03-01_15-26-04.xlsx")
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug(baseExcel2StringXml.size.toString() + "," + baseExcel2StringXml.firstOrNull()?.size)

        FileConfig.languageDirNameList.forEachIndexed { index, triple ->
            val dir = FileUtilsWrapper.getDirByCreate(resPathDir + "/" + triple.first)
            val fileName = "strings.xml"
            val fullPath = "$dir/$fileName"
            val fullPatFile = FileUtilsWrapper.getFileByCreate(fullPath)
            val pairList = baseExcel2StringXml.filterIndexed { index2, _ -> index2 > 0 }.map { itemList ->
                val defaultKey = itemList[1]
                //val key = itemList[1]
                val newKey = itemList[2]
                val realKey = if (newKey.isNullOrEmpty()) defaultKey ?: "no key" else newKey
                val value = itemList[index + 3]
                Pair(realKey, value)
            }
            val pairList2StringXml = pairList2StringXml(pairList)
            fullPatFile.writeText(pairList2StringXml)
            LogWrapper.loggerWrapper(XmlCore.javaClass).debug(fullPatFile.path)
        }

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

    fun stringsXml2SortKeyList(stringsXmlPath: String): List<String> {
        return stringsXml2SortedMap(stringsXmlPath).keys.toList()
    }

    fun sortMap2StringXml(map: Map<String, String>): String {
        val list = ArrayList<Pair<String, String>>()
        map.forEach {
            list.add(Pair(it.key, it.value))
        }
        return pairList2StringXml(list)
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
}