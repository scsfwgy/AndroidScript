package com.matt.script

import com.matt.script.config.FileConfig
import com.matt.script.core.ExcelCore
import com.matt.script.core.XmlCore
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.blankj.TimeUtils

object AndroidScript {
    /**
     * 抽取安卓项目中的strings.xml文案到excel。
     * 使用前请配置：[FileConfig]
     */
    fun extractAndroidProject2Excel() {
        val currRootPath = FileConfig.currRootPath
        val backUpDir = FileUtilsWrapper.getDirByCreate(
            currRootPath + "/" + FileConfig.backup
        )
        val time =
            TimeUtils.millis2String(System.currentTimeMillis(), TimeUtils.getSafeDateFormat("yyyy-MM-dd_HH-mm-ss"))
        val excelPath = backUpDir + "/" + "安卓抽取${time}.xls"
        ExcelCore.xml2Excel(FileUtilsWrapper.getFileByCreate(excelPath))
    }


    /**
     * 把excel回写到备份文件夹中的strings.xml
     */
    fun excel2StringsXml() {
        val excel2StringXml = "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/安卓抽取2021-12-24_16:31:09.xls"
        XmlCore.excel2StringsXml(excel2StringXml)
    }
}