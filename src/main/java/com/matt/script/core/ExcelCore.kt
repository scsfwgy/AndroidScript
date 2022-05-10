package com.matt.script.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.matt.script.config.FileConfig
import com.matt.script.config.LogWrapper
import com.matt.script.core.language.LocalLanguage
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import java.io.File
import java.io.FileInputStream


fun main() {
    ExcelCore.iOSLbkRDLocalizable2Excel(
        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language",
        FileUtilsWrapper.getDirByCreate("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/RDLocalizable2Excel")
    )
}

object ExcelCore {
    fun asbLbkLanguage2Excel(excelOutPathDir: String, mapList: List<Map<String, String>>, defaultIndex: Int = 34) {
        //默认,中文拍34位
        val keyList = mapList[defaultIndex].keys

        val realList = ArrayList<List<String?>>()

        val headList = ArrayList<String?>()
        headList.add("key")
        headList.add("defaultKey")
        headList.add("newkey")
        loadDefaultLanguageList().forEach {
            headList.add(it.key)
        }
        realList.add(headList)

        keyList.forEach { key ->
            val isNewKey = key.startsWith("L0")
            if (!isNewKey) {
                LogWrapper.loggerWrapper(this).debug("===>>" + key)
            }
            val itemList = ArrayList<String?>()
            itemList.add(if (isNewKey) key.filterIndexed { index, _ -> index < 8 } else null)
            itemList.add(if (isNewKey) null else key)
            itemList.add(if (isNewKey) key else null)
            mapList.forEach {
                itemList.add(it[key])
            }
            realList.add(itemList)
        }
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug("==>" + realList.size + "," + realList.firstOrNull()?.size)
        val excelFileName = "多语言自动化抽取转Excel_" + FileUtilsWrapper.defaultTimeName() + ".xlsx"
        val fileByCreate =
            FileUtilsWrapper.getFileByCreate("$excelOutPathDir/$excelFileName")
        ExcelUtils.baseXml2Excel(
            fileByCreate,
            realList,
            "语言集合"
        )
        LogWrapper.loggerWrapper(KeyConvertCore::class.java)
            .debug("最终输出位置" + fileByCreate.path)
    }

    fun loadDefaultLanguageList(): List<LocalLanguage> {
        val localLanguageList =
            loadLanguageListByExcel("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/language/语言名称对应表.xlsx")
        val file = File("/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/assets/api/local_language.json")
        LogWrapper.loggerWrapper("loadDefaultLanguageList").debug("开始将语言配置写到assets中：" + file.path)
        LogWrapper.loggerWrapper("loadDefaultLanguageList").debug("开始将语言配置写到assets中：" + localLanguageList.size)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val toJson = gson.toJson(localLanguageList)
        file.writeText(toJson)
        return localLanguageList
    }

    fun asbLbkLanguage2ExcelWrapper(
        languagePath: String,
        excelOutPathDir: String,
        iosType: Boolean
    ) {
        LogWrapper.loggerWrapper(this).debug("===========将语言导出为标准的产品需要的格式============")
        LogWrapper.loggerWrapper(this).debug("扫描路径：$languagePath,导出路径：$excelOutPathDir,iOS?:$iosType")
        val tripleList = loadDefaultLanguageList()
        val mapList = tripleList.map { languageTriple ->
            val fileName = if (iosType) FileConfig.stringsXmlFileNameIOS else FileConfig.stringsXmlFileName
            val fullPath = languagePath + "/" + languageTriple.languageDir(iosType) + "/" + fileName
            val map = if (iosType) XmlCore.rDLocalizable2SortMap(fullPath) else XmlCore.stringsXml2SortedMap(fullPath)
            val newMap = LinkedHashMap<String, String>()
            map.forEach {
                newMap[it.key] = it.value
            }
            newMap
        }
        asbLbkLanguage2Excel(excelOutPathDir, mapList)
    }


    /**
     * iOS将语言导出为标准的产品需要的格式
     */
    fun iOSLbkRDLocalizable2Excel(languagePath: String, excelOutPathDir: String) {
        asbLbkLanguage2ExcelWrapper(languagePath, excelOutPathDir, true)
    }

    /**
     * Android将语言导出为标准的产品需要的格式 strings.xml List=>Excel
     */
    fun androidLbkXml2Excel(languagePath: String, excelOutPathDir: String) {
        asbLbkLanguage2ExcelWrapper(languagePath, excelOutPathDir, false)
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
            val keyList = XmlCore.asbStringsXml2SortKeyList(fullDefaultValuesPath, false)
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

    fun loadLanguageListByExcel(excelPath: String): List<LocalLanguage> {
        val baseExcel2StringXml = ExcelUtils.baseExcel2StringXml(excelPath, 0, 1)
        val list = ArrayList<LocalLanguage>()
        baseExcel2StringXml.forEach {
            list.add(
                LocalLanguage(
                    it[0]?.toIntOrNull() ?: 0,
                    it[1] ?: "",
                    it[2] ?: "",
                    it[3] ?: "",
                    it[4] ?: "",
                    it[5] ?: "",
                    it[6] ?: "",
                    it[7] ?: "",
                    it[8] ?: "",
                    it[9].equals("true", true),
                    it[10] ?: "",
                )
            )
        }
        LogWrapper.loggerWrapper(this).debug("===>>>" + Gson().toJson(list))
        return list
    }
}