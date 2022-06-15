package com.matt.script.test

import com.matt.script.utils.FileUtilsWrapper
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileInputStream
import java.math.BigDecimal


fun main() {
//    val text = """
//Mogstad, Magne and Matthew Wiswall. 2016. Testing the quantity-quality model of fertility: Esti-	mation using unrestricted family size models. Quantitative Economics 7, no. 1:157–192.
//Nagin, Daniel. 2013. Deterrence: A review of the evidence by a criminologist for economists. Annual 	Review of Economics 5:83–105.
//Pavan, Ronni. 2016. On the production of skills and the birth order eﬀect. Journal of Human 	Resources 51, no. 3:699–726.
//Price, Joseph. 2008. 	Parent-child quality time: Does birth order matter? 	Journal of Human 	Resources 43, no. 1:240–265.
//Qian, Nancy. 2009. 	Quantity-quality and the one child policy: The only-child disadvantage in 	school enrollment in rural China NBER Working Paper no. w14973, National Bureau of Economic 	Research, Cambridge, MA.
//Qureshi, Javaeria. 2018. Siblings, teachers and spillovers in academic achievement. Journal of Human 	Resources 53, no. 1:272–297.
//Rohrer, Julia, Boris Egloﬀ, and Stefan Schmukle. 2015. Examining the eﬀects of birth order on 	personality. Proceedings of the National Academy of Sciences 112, no. 46:14224–14229.
//Rosenzweig, Mark and Junsen Zhang. 2009. Do population control policies induce more human capital investment? Twins, birth weight and China’s one-child policy. Review of Economic Studies 76, no. 3:1149–1174.
//Rouse, Cecilia, David Figlio, Dan Goldhaber, and Jane Hannaway. 2013. Feeling the Florida heat? How low-performing schools respond to voucher and accountability pressure. American Economic Journal: Economic Policy 5, no.
//    """.trimIndent()
//    val split = text.split(". ", "? ")
//    println(split.map { it + "======" })
//
//    val realList = ArrayList<Int>()
//    split.forEach {
//        val list = it.split(" ")
//        realList.add(list.size)
//    }
//    println("每句话单词数：" + realList)
//    println("平均单词：" + realList.average())

//
//    //        //开始写入
//    val fileInputStream =
//        FileInputStream("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp/706048.pdf.docx")
//    val doc = XWPFDocument(fileInputStream)
//    val extractor = XWPFWordExtractor(doc)
//    val text = extractor.text
//    extractor.coreProperties
//    println("=====>" + text)
    //text.split()

    val dir = FileUtilsWrapper.getDirByCreate("/Users/matt.wang/Desktop/word2")
    val listFileByPath = FileUtilsWrapper.listFileByPath("/Users/matt.wang/Desktop/word", filterSuffix = "docx")
    listFileByPath.forEach {
        //开始写入
        val fileInputStream = FileInputStream(it)
        val doc = XWPFDocument(fileInputStream)
        val extractor = XWPFWordExtractor(doc)
        val text = extractor.text

        val name = it.parentFile.name
        val dirByCreate = FileUtilsWrapper.getDirByCreate(dir + "/" + name)
        val fileByCreate = FileUtilsWrapper.getFileByCreate(dirByCreate + "/" + it.name+"_" + avg(text) +"_"+ ".txt")


        println("=====>" + it.path)
        fileByCreate.writeText(text)
    }
}

fun avg(text: String): String {
    val split = text.split(". ", "? ")
    println(split.map { it + "======" })

    val realList = ArrayList<Int>()
    split.forEach {
        val list = it.split(" ")
        realList.add(list.size)
    }
    println("每句话单词数：" + realList)
    println("平均单词：" + realList.average())
    val len = realList.average().toString()
    return len
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