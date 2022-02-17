package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.config.GlobalConfig
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.TimeUtils
import java.io.File

object XmlCore {

    const val TAG = "CoreWrapper"


    /**
     * 把指定语言xml提取为map、排好序的key列表
     */
    fun stringsXml2CacheKV(stringsXmlPath: String): Pair<Map<String, String>, List<String>> {
        val file = File(stringsXmlPath)
        if (!file.exists()) {
            throw IllegalAccessException("stringsXml2Map文件不存在:" + stringsXmlPath)
        }
        val map = LinkedHashMap<String, String>()
        val sortKeyList = ArrayList<String>()
        val readText = file.readText()
        val matches = RegexUtilsWrapper.lines2StringXmlLineList(readText)
        matches.forEachIndexed { index, s ->
            //if (index > 10) return@forEachIndexed
            val k = RegexUtilsWrapper.line2StringXmlLineKey(s)
            val v = RegexUtilsWrapper.line2StringXmlLineValue(s)
            if (GlobalConfig.debug) {
                //LoggerImp.logger.d("stringsXml2Map", key + "==>" + value)
            }
            sortKeyList.add(k)
            map[k] = v
        }
        return Pair(map, sortKeyList)
    }

    fun stringsXml2SortMap(stringsXmlPath: String): Map<String, String> {
        return stringsXml2CacheKV(stringsXmlPath).first
    }

    fun stringsXml2SortKeyList(stringsXmlPath: String): List<String> {
        return stringsXml2CacheKV(stringsXmlPath).second
    }

    fun getResFullPathListPair(): List<Pair<String, String>> {
        return FileConfig.moduleList.map {
            Pair(it, FileConfig.androidRootPath + "/" + it + "/" + FileConfig.resRootDir)
        }
    }

    /**
     * FileConfig.languageDirNameList:Pair(strings.xml所在的文件夹名，别名)=>
     * Triple(strings.xml所在的文件夹名，别名,该文件内国际化转化为map)
     */
    fun foreachModuleLanguageXml(): List<Pair<String, List<Triple<String, String, Map<String, String>>>>> {
        val resFullPathList = getResFullPathListPair()
        return resFullPathList.map { pathPair ->
            val map = FileConfig.languageDirNameList.map {
                val stringXmlPath = pathPair.second + "/" + it.first + "/" + FileConfig.stringsXmlFileName
                //LoggerImp.logger.d(TAG, "foreachModuleLanguageXml:" + stringXmlPath)
                val stringsXml2Map = stringsXml2SortMap(stringXmlPath)
                Triple(it.first, it.second, stringsXml2Map)
            }
            //模块名，各级的翻译
            Pair(pathPair.first, map)
        }
    }

    /**
     * 根据Module获取排好序（本身strings.xml的key的顺序）的key列表
     */
    fun foreachModuleSortKeyList(): List<Pair<String, List<String>>> {
        val resFullPathListPair = getResFullPathListPair()
        return resFullPathListPair.map {
            val path = it.second + "/" + FileConfig.defaultValuesName + "/" + FileConfig.stringsXmlFileName
            //LoggerImp.logger.d(TAG, "foreachModuleSortKeyList：" + path)
            Pair(it.first, stringsXml2SortKeyList(path))
        }
    }

    fun excel2StringsXml(excelFilePath: String) {
        val currRootPath = FileConfig.currRootPath
        val time =
            TimeUtils.millis2String(System.currentTimeMillis(), TimeUtils.getSafeDateFormat("yyyy-MM-dd_HH:mm:ss"))
        val backUpDir = FileUtilsWrapper.getDirByCreate(
            currRootPath + "/" + FileConfig.backup + "/" + time
        )
        val excel2StringXml =
            ExcelCore.excel2StringXml(excelFilePath)

        excel2StringXml.forEach { module ->
            val moduleDir = FileUtilsWrapper.getDirByCreate(backUpDir + "/" + module.key)
            val value = module.value
            value.forEach {
                val lanDir = FileUtilsWrapper.getDirByCreate(moduleDir + "/" + it.key)
                val stringKV = it.value
                val stringXmlLines = pairList2StringXml(stringKV)
                val fileByCreate = FileUtilsWrapper.getFileByCreate(lanDir + "/" + FileConfig.stringsXmlFileName)
                fileByCreate.writeText(stringXmlLines)
            }
        }
    }

    fun sortMap2StringXml(map: Map<String, String>): String {
        val list = ArrayList<Pair<String, String>>()
        map.forEach {
            list.add(Pair(it.key, it.value))
        }
        return pairList2StringXml(list)
    }

    fun pairList2StringXml(list: List<Pair<String, String?>>): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    ${pairList2Lines(list)}
</resources> 
        """.trimIndent()
    }

    private fun pairList2Lines(list: List<Pair<String, String?>>): String {
        return list.joinToString(separator = "\n" + "    ") { pair2StringLine(it) }
    }

    private fun pair2StringLine(pair: Pair<String, String?>): String {
        return """<string name="${pair.first}">${pair.second}</string>""".trimIndent()
    }

    /**
     * 去除strings.xml重复的key
     */
    fun stringXmlList2Set(stringsXml: File): String {
        val stringsXml2Map = stringsXml2SortMap(stringsXml.path)
        val list = ArrayList<Pair<String, String>>()
        stringsXml2Map.forEach {
            list.add(Pair(it.key, it.value))
        }
        return pairList2StringXml(list)
    }
}