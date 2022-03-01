package com.matt.script

import com.matt.script.config.FileConfig
import com.matt.script.core.ExcelCore
import com.matt.script.core.XmlCore

object AndroidScript {
    /**
     * 抽取安卓项目中的strings.xml文案到excel。
     * 使用前请配置：[FileConfig]
     */
    fun extractAndroidProject2Excel() {
       ExcelCore.lbkXml2Excel()
    }


    /**
     * 把excel回写到备份文件夹中的strings.xml
     */
    fun excel2StringsXml() {
        //测试
        XmlCore.lbkExcel2StringXmlDebug()
        //正式
        //XmlCore.lbkExcel2StringXml("/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res")
    }
}