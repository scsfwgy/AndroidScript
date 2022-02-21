package com.matt.script.core

import com.matt.script.config.FileConfig
import com.matt.script.log.LogUtils
import com.matt.script.utils.ExcelUtils
import com.matt.script.utils.FileUtilsWrapper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ExcelCore {

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
            val map = XmlCore.stringsXml2SortMap(stringsXmlPath)
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
        headList.add("defaultKey")
        headList.add("key")
        headList.add("newkey")
        FileConfig.languageDirNameList.forEach {
            headList.add(it.second)
        }
        realList.add(headList)

        keyList.forEach { key ->
            val isNewKey = key.startsWith("L0")
            val itemList = ArrayList<String?>()
            itemList.add(if (isNewKey) null else key)
            itemList.add(if (isNewKey) key.filterIndexed { index, _ -> index < 8 } else null)
            itemList.add(if (isNewKey) key else null)
            stringListMap.forEach {
                itemList.add(it[key])
            }
            realList.add(itemList)
        }
        LogUtils.loggerWrapper(KeyConvertCore::class.java)
            .debug("==>" + realList.size + "," + realList.firstOrNull()?.size)
        val excelFileName = "Android多语言自动化抽取转Excel_" + FileUtilsWrapper.defaultFileSuffixName() + ".xlsx"
        ExcelUtils.baseXml2Excel(
            FileUtilsWrapper.getFileByCreate("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/" + excelFileName),
            realList,
            "Android语言集合"
        )
    }

    /**
     * 将安卓项目中的xml抽取生成为[generateExcelFile]的excel
     */
    fun xml2Excel(generateExcelFile: File): Boolean {
        if (!generateExcelFile.exists()) {
            throw IllegalAccessException("xml2Excel失败，文件不存在")
        }
        val foreachModuleSortKeyList = XmlCore.foreachModuleSortKeyList()
        val foreachModuleLanguageXml = XmlCore.foreachModuleLanguageXml()

        //聚合多个模块的语言key
        val allKeyList = ArrayList<String>()
        foreachModuleSortKeyList.forEach { pair ->
            pair.second.forEach {
                allKeyList.add(it)
            }
        }
        //聚合多个模块的语言k/v
        val mapMap = HashMap<String, MutableMap<String, String>>()
        foreachModuleLanguageXml.forEach {
            it.second.forEach { triple ->
                val first = triple.first
                var map = mapMap[first]
                if (map == null) {
                    map = HashMap()
                    mapMap[first] = map
                }
                val third = triple.third
                map.putAll(third)
            }
        }


        val wb = HSSFWorkbook() //创建Excel工作簿对象
        val sheet = wb.createSheet("安卓翻译") //创建Excel工作表对象
        //备注
        val remark = sheet.lastRowNum
        val remarkRow = sheet.createRow(remark + 1)
        remarkRow.createCell(0).setCellValue(
            """
              不要删除本行，有各种需要说明的信息放这里。备注信息,注意：
1.`->\`,举例：It`s me->It\`s me,必须加转义字符\  
2. &->&amp; 
3. 保持原有\n换行符，不要主观加或者删减\n
4.%s、%d、%f  请保持，不要修改
           """.trimIndent()
        )
        val lastRowNum1 = sheet.lastRowNum
        val headRow = sheet.createRow(lastRowNum1 + 1)
        headRow.createCell(0).setCellValue("key(不要动这一列内容)")
        FileConfig.languageDirNameList.forEachIndexed { index, it ->
            val cell = headRow.createCell(index + 1)
            cell.setCellValue(it.second)
        }
        //开始处理key
        allKeyList.forEach {
            val item = sheet.lastRowNum
            val itemRow = sheet.createRow(item + 1)
            itemRow.createCell(0).setCellValue(it)

            //开始处理值
            FileConfig.languageDirNameList.forEachIndexed { index, pair ->
                val value = mapMap[pair.first]?.get(it)
                itemRow.createCell(index + 1).setCellValue(value ?: "")
            }
        }
        wb.write(FileOutputStream(generateExcelFile))
        return true
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