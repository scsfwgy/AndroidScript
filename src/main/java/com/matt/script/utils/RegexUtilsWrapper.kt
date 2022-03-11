package com.matt.script.utils

import com.matt.script.core.interfaces.FormatLineConvert
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
    println(RegexUtilsWrapper.hasChinese("你好"))
    println(RegexUtilsWrapper.hasChinese("22你21312好"))
    println(RegexUtilsWrapper.hasChinese("123"))
    println(RegexUtilsWrapper.hasChinese("啥啥啥"))
}

object RegexUtilsWrapper {

    val stringXmlLineRegex = """
            <string\s*name="(.*)"\s*>((?!</string>)[\s\S\n])*</string>
            """.trimIndent()


    val stringXmlKeyRegex = """
            (?<=<string\s{0,10000000}name=")((?!")(.))*(?="\s*>)
        """.trimIndent()

    val stringXmlValueRegex = """
            (?<=<string\s{0,10000000}name="(.{0,10000000})"\s{0,10000000}>)((?!</string>)[\s\S\n])*(?=</string>)
        """.trimIndent()

    val pureKeyRegex = """
        [a-zA-Z0-9_.\u4e00-\u9fa5]+
    """.trimIndent()

    val javaOrKtPureStringKeyRegex = """
        (?<=R.string\.)$pureKeyRegex
    """.trimIndent()

    val javaOrKtPureDrawableKeyRegex = """
        (?<=R.drawable\.)$pureKeyRegex
    """.trimIndent()

    val xmlPureStringKeyRegex = """
        (?<=@string/)$pureKeyRegex
    """.trimIndent()

    val xmlPureDrawableKeyRegex = """
        (?<=@drawable/)$pureKeyRegex
    """.trimIndent()

    val stringXmlPureKeyRegex = """
        (?<=name=")$pureKeyRegex
    """.trimIndent()

    //(?<=RDLocalizedString\(@")((?!").)+(?="\))
    val iosPureKeyRegex = """
        (?<=RDLocalizedString\(@")[a-zA-Z0-9\u4e00-\u9fa5_.\\、。"]+(?="\))
    """.trimIndent()

    //包含%，不包含%s
    val iosSpecialRegex = """
        (?!%s)%
    """.trimIndent()

    val iosContainerPlaceholderRegex = """
        (?<=RDLocalizedString\(@")((?!")(.*(%[df])+.*))(?="\))
    """.trimIndent()

    /**
     * 包含中文的字符串，包含前后",举例：xxx"xxx你好xxx"生生世世=>"xxx你好xxx"
     */
    val containsZhRegex = """
        "(.(?!"))*[\u4e00-\u9fa5]+((?!").)*"
    """.trimIndent()

    /**
     * 包含中文的字符串，不包含前后",举例：xxx"xxx你好xxx"生生世世=>xxx你好xxx
     */
    val containsZhRegex2 = """
        (?<=")(.(?!"))*[\u4e00-\u9fa5]+((?!").)*(?=")
    """.trimIndent()

    val xmlAllContentRegex = """
        (?<=android:text=")((?!@string/).*)(?=")
    """.trimIndent()

    val xmlContainerZhContentRegex = """
        (?<=android:text=")((?!@string/).)*[\u4e00-\u9fa5]+((?!").)*(?=")
    """.trimIndent()

    val javaOrKtContainerR = """
        import(.)*\.R;*\n
    """.trimIndent()

    val iosRDLocalizableKey="""
        (?<=").+(?="=)
    """.trimIndent()

    val iosRDLocalizableValue="""
        (?<==").+(?=")
    """.trimIndent()

    /**
     * 输入多行数据，匹配strings.xml中单行文案:<string name="Home">首页</string>
     */
    fun lines2StringXmlLineList(lines: String): List<String> {
        return RegexUtils.getMatches(stringXmlLineRegex, lines)
    }

    /**
     * 匹配strings.xml中单行的key:<string name="Home">首页</string> 中的 Home
     */
    fun line2StringXmlLineKey(line: String): String {
        val keys = RegexUtils.getMatches(stringXmlKeyRegex, line)
        if (keys.size != 1) {
            throw IllegalArgumentException("line2StringXmlLineKey参数错误:$line")
        }
        return keys[0]
    }

    /**
     * 匹配strings.xml中单行的value:匹配strings.xml中单行的key:<string name="Home">首页</string> 中的 首页
     */
    fun line2StringXmlLineValue(line: String): String {
        val values = RegexUtils.getMatches(stringXmlValueRegex, line)
        if (values.size != 1) {
            throw IllegalArgumentException("line2StringXmlLineKey参数错误:$line")
        }
        return values[0]
    }

    fun findRStringKey() {
        val str =
            """return if (mCancelType) MyContextUtils.getString(R.string.account_status_finish_layout4) else MyContextUtils.getString(R.string.account_appeal_layout1)"""
        val matches = RegexUtils.getMatches("(?<=R.string.)(a-zA-Z0-9_.)+", str)
        println(matches)
    }


    fun line2FormatLine(
        line: String,
        valueRegex: String?,
        replace: String = "%s",
        formatLineConvert: FormatLineConvert,
    ): String {
        if (valueRegex == null) {
            return formatLineConvert.formatLine2NewLine(line, null)
        }
        val keyList = RegexUtils.getMatches(valueRegex, line)
        return if (keyList.isNotEmpty()) {
            val newFormatLine = RegexUtils.getReplaceAll(line, valueRegex, replace)
            formatLineConvert.formatLine2NewLine(newFormatLine, keyList)
        } else {
            formatLineConvert.formatLine2NewLine(line, null)
        }
    }

    fun test() {
        // <string\s*name="(.*)"\s*>((?!</string>)[\s\S\n])*</string>
        // @"((?!")[\s\S\n])*"
        val readLine =
            FileUtilsWrapper.readLine(File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/WithdrawalApplySuccessViewCell.m"))
        //val regex = """@"((?!")[\s\S\n])*""""
        val regex = """@"((?!")[(\s\S\n)*(\u4e00-\u9fa5)+(\s\S\n)*]*)""""
        readLine.forEach {
            val matches = RegexUtils.getMatches(regex, it)
            if (matches.isNotEmpty()) {
                println(matches)
            }
        }
    }

    fun hasChinese(data: String): Boolean {
        return RegexUtils.isMatch(
            """
            .*[\u4e00-\u9fa5]+.*
        """.trimIndent(), data
        )
    }
}