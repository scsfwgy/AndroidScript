package com.matt.script.test

import com.matt.script.utils.FileUtilsWrapper
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileInputStream
import java.math.BigDecimal


fun main() {

    //        //开始写入
    val fileInputStream =
        FileInputStream("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp/706048.pdf.docx")
    val doc = XWPFDocument(fileInputStream)
    val extractor = XWPFWordExtractor(doc)
    val text = extractor.text
    extractor.coreProperties
    println("=====>" + text)

//    val dir = FileUtilsWrapper.getDirByCreate("/Users/matt.wang/Desktop/word2")
//    val listFileByPath = FileUtilsWrapper.listFileByPath("/Users/matt.wang/Desktop/word", filterSuffix = "docx")
//    listFileByPath.forEach {
//        val name = it.parentFile.name
//        val dirByCreate = FileUtilsWrapper.getDirByCreate(dir + "/" + name)
//        val fileByCreate = FileUtilsWrapper.getFileByCreate(dirByCreate + "/" + it.name + ".txt")
//
//        //开始写入
//        val fileInputStream = FileInputStream(it)
//        val doc = XWPFDocument(fileInputStream)
//        val extractor = XWPFWordExtractor(doc)
//        val text = extractor.text
//        println("=====>" + it.path)
//        fileByCreate.writeText(text)
//    }
}

object FormatTest {

    fun localDepthTypeConvert(originKeyStr: String): Pair<String, String> {
        try {
            return localDepthTypeConvertLocal(originKeyStr)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair("", "")
        }
    }

    fun localDepthTypeConvertLocal(originKeyStr: String): Pair<String, String> {
        val originKey = originKeyStr.toIntOrNull() ?: 0
        val pair = if (originKey < 0) {
            Pair(BigDecimal(10).pow(-originKey), BigDecimal(1).divide(BigDecimal(10).pow(-originKey)))
        } else {
            Pair(BigDecimal(1).divide(BigDecimal(10).pow(originKey)), BigDecimal(10).pow(originKey))
        }
        return Pair(pair.first.toPlainString(), pair.second.toPlainString())
    }

    /**
     * out:
     * 第一个参数：参数a，第二个参数：参数b
     * 第一个参数：参数a，第二个参数：参数b
     * 第二个参数：我是第二个参数，第一个参数：我是第一个参数
     * 第一个参数：参数1，第二个参数：参数true
     */
    fun test() {
//        val msg = "第一个参数：参数%s，第二个参数：参数%s"
//        println(msg.format("a", "b"))
//
//        val msg2 = "第一个参数：参数{0}，第二个参数：参数{1}"
//        println(MessageFormat.format(msg2, "a", "b"))
//
//        val msg3 = "第二个参数：{1}，第一个参数：{0}"
//        println(MessageFormat.format(msg3, "我是第一个参数", "我是第二个参数"))
//
//        val msg4 = "第一个参数：参数{0}，第二个参数：参数{1}"
//        println(MessageFormat.format(msg4, 1, true))
//
//        val msg5 = "第一个参数：参数{1}，第二个参数：参数{1}"
//        println(MessageFormat.format(msg5, 1, true))
//
//        val msg7 = "第一个参数：参数{1}，第二个参数：参数{1}"
//        println(MessageFormat.format(msg7, null))
//
//        val msg8 = "第一个参数：参数{1}，第二个参数：参数{1}"
//        println(MessageFormat.format(msg8))
//
//        val msg9 = "我没有动态参数"
//        println(MessageFormat.format(msg9, null))
//
//        val msg10 = "我没有动态参数"
//        println(MessageFormat.format(msg10, "a", "b"))
//
//        //做好容错，会抛异常
//        val msg11 = "第一个参数：参数{1}，第二个参数：参数{1"
//        println(MessageFormat.format(msg11, null))

//        println(String.format("%.2f", 1.00000))
//        val data = 1 + (100 / 1000)
//        println(StringWrapper.format("{0}s后重新发送", data))
//        println(StringWrapper.format("{0}s后重新发送", "2"))
//        println(StringWrapper.format("{0}s后重新发送", 2.3f))

        val list = listOf("base", "sss.base.ssssdd")
        println(list.filter { it.contains("base") })
    }
}