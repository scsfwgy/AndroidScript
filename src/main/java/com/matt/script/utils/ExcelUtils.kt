package com.matt.script.utils

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object ExcelUtils {
    fun baseXml2Excel(
        generateExcelFile: File,
        rowList: List<List<String?>>,
        sheetName: String? = "DefaultSheetName"
    ): Boolean {
        //创建Excel工作簿对象
        val wb = XSSFWorkbook()
        //创建Excel工作表对象
        val sheet = wb.createSheet(sheetName)

        rowList.forEach { row ->
            val lastRow = sheet.lastRowNum
            val currentRowIndex = lastRow + 1
            val hssfRow = sheet.createRow(currentRowIndex)
            row.forEachIndexed { index, cell ->
                hssfRow.createCell(index).setCellValue(cell ?: "")
            }
        }
        wb.write(FileOutputStream(generateExcelFile))
        return true
    }

    fun baseExcel2StringXml(excelPath: String, sheetIndex: Int = 0, beginRow: Int = 0): List<List<String?>> {
        val fileInputStream = FileInputStream(excelPath)
        val wb = XSSFWorkbook(fileInputStream)
        val sheet = wb.getSheetAt(sheetIndex)
        val finalList = ArrayList<List<String?>>()
        sheet.forEachIndexed { index, row ->
            if (index < beginRow) return@forEachIndexed
            val itemList = ArrayList<String?>()
            val lastCellNum = row.lastCellNum.toInt()
            for (itemIndex in 0 until lastCellNum) {
                val cell = row.getCell(itemIndex)
                if (cell == null) {
                    //LogWrapper.loggerWrapper(this).debug("$index==>>>$itemIndex==>$row")
                    itemList.add("")
                } else {
                    val cellValue = getCellValue(cell)
                    itemList.add(cellValue)
                }
            }
            finalList.add(itemList)
        }
        fileInputStream.close()
        return finalList
    }

    /**
     * https://poi.apache.org/components/spreadsheet/quick-guide.html#CellContents
     */
    fun getCellValue(cell: Cell): String {
        val cellType = cell.cellType
        val data = when (cell.cellType) {
            CellType.STRING -> cell.richStringCellValue.string
            CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
                cell.dateCellValue
            } else {
                cell.numericCellValue
            }
            CellType.BOOLEAN -> cell.booleanCellValue
            CellType.FORMULA -> cell.cellFormula
            CellType.BLANK -> ""
            else -> ""
        }
        return data.toString()
    }
}