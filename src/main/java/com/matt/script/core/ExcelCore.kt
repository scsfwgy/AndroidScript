package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.config.LogWrapper
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import java.io.File
import java.io.FileInputStream

fun main() {
    ExcelCore.iOSLbkRDLocalizable2Excel("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language")
}

object ExcelCore {


    /**
     * iOS将语言导出为标准的产品需要的格式
     */
    fun iOSLbkRDLocalizable2Excel(languagePath: String) {
        println("===========将语言导出为标准的产品需要的格式============")
        val stringListMap = FileConfig.languageDirNameListIOS.map { languageTriple ->
            val fullPath = languagePath + "/" + languageTriple.first + "/" + FileConfig.stringsXmlFileNameIOS

            //追加后缀
//            val second = languageTriple.second
//            val suffix = second.substring(second.length - 2)
//            val realSuffix = "_" + suffix


            val map = XmlCore.rDLocalizable2SortMap(fullPath)
            val newMap = LinkedHashMap<String, String>()
            map.forEach {
                newMap[it.key] = it.value
            }
            newMap
        }

        //默认
        val keyList = stringListMap[0].keys

        val realList = ArrayList<List<String?>>()

        val headList = ArrayList<String?>()
        headList.add("key")
        headList.add("defaultKey")
        headList.add("newkey")
        FileConfig.languageDirNameListIOS.forEach {
            headList.add(it.second)
        }
        realList.add(headList)

        keyList.forEach { key ->
            val isNewKey = key.startsWith("L0")
            val itemList = ArrayList<String?>()
            itemList.add(if (isNewKey) key.filterIndexed { index, _ -> index < 8 } else null)
            itemList.add(if (isNewKey) null else key)
            itemList.add(if (isNewKey) key else null)
            stringListMap.forEach {
                itemList.add(it[key])
            }
            realList.add(itemList)
        }
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug("==>" + realList.size + "," + realList.firstOrNull()?.size)
        val excelFileName = "iOS多语言自动化抽取转Excel_" + FileUtilsWrapper.defaultTimeName() + ".xlsx"
        ExcelUtils.baseXml2Excel(
            FileUtilsWrapper.getFileByCreate("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/RDLocalizable2Excel/" + excelFileName),
            realList,
            "iOS语言集合"
        )
    }

    /**
     * 将语言导出为标准的产品需要的格式 strings.xml List=>Excel
     */
    fun lbkXml2Excel() {
        println("===========将语言导出为标准的产品需要的格式============")
        val stringListMap = FileConfig.languageDirNameList.map { languageTriple ->
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
                newMap[it.key] = it.value.replace(realSuffix, "")
            }

            newMap
        }

        //默认
        val keyList = stringListMap[0].keys

        val realList = ArrayList<List<String?>>()

        val headList = ArrayList<String?>()
        headList.add("key")
        headList.add("defaultKey")
        headList.add("newkey")
        FileConfig.languageDirNameList.forEach {
            headList.add(it.second)
        }
        realList.add(headList)

        keyList.forEach { key ->
            val isNewKey = key.startsWith("L0")
            val itemList = ArrayList<String?>()
            itemList.add(if (isNewKey) key.filterIndexed { index, _ -> index < 8 } else null)
            itemList.add(if (isNewKey) null else key)
            itemList.add(if (isNewKey) key else null)
            stringListMap.forEach {
                itemList.add(it[key])
            }
            realList.add(itemList)
        }
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug("==>" + realList.size + "," + realList.firstOrNull()?.size)
        val excelFileName = "Android多语言自动化抽取转Excel_" + FileUtilsWrapper.defaultTimeName() + ".xlsx"
        ExcelUtils.baseXml2Excel(
            FileUtilsWrapper.getFileByCreate("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/" + excelFileName),
            realList,
            "Android语言集合"
        )
    }


    /**
     * 将excel文件生成Map<模块名，Map<语言，List<文案的key,文案>>>的形式
     */
    fun excel2StringXml(excelPath: String): Map<String, MutableMap<String, MutableList<Pair<String, String>>>> {
        val finalDataMap = HashMap<String, MutableMap<String, MutableList<Pair<String, String>>>>()

        val file = File(excelPath)
        if (!file.exists()) throw IllegalAccessException("excel2StringXml excel文件不存在")
        val excel2Map = excel2Map(file)

        FileConfig.moduleList.forEach { moduleName ->
            var languageMap = finalDataMap[moduleName]
            if (languageMap == null) {
                languageMap = HashMap()
                finalDataMap[moduleName] = languageMap
            }
            val fullDefaultValuesPath = FileConfig.getFullDefaultValuesPath(moduleName, FileConfig.defaultValuesName)
            val keyList = XmlCore.stringsXml2SortKeyList(fullDefaultValuesPath)
            FileConfig.languageDirNameList.forEachIndexed { index, pair ->
                var list = languageMap[pair.first]
                if (list == null) {
                    list = ArrayList()
                    languageMap[pair.first] = list
                }
                keyList.forEach { key ->
                    val value = excel2Map[key]?.getOrNull(index + 1)
                    list.add(Pair(key, value ?: ""))
                }
            }
        }
        return finalDataMap
    }


    /**
     * 把excel转化为map形式，key=第一列 values:行的每一列
     */
    fun excel2Map(excelFile: File, sheetIndex: Int = 0, beginRow: Int = 2): Map<String, Array<String>> {
        val fileInputStream = FileInputStream(excelFile)
        val wb = HSSFWorkbook(fileInputStream)
        val sheet = wb.getSheetAt(sheetIndex)
        val map = HashMap<String, Array<String>>()
        sheet.forEachIndexed { index, row ->
            if (index < beginRow) return@forEachIndexed
            val lastCellNum = row.lastCellNum.toInt()
            val array = Array(lastCellNum) {
                val cell = row.getCell(it)
                cell?.cellType = CellType.STRING
                cell?.stringCellValue ?: ""
            }
            map[array[0]] = array
        }
        fileInputStream.close()
        return map
    }
}