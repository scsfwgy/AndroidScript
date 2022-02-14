package com.matt.script.utils

import com.matt.script.core.KeyConvertCore
import com.matt.script.utils.blankj.RegexUtils
import java.io.File
import kotlin.jvm.Throws

fun main() {
    RegexUtilsWrapper.test()
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
        [a-zA-Z0-9_.]+
    """.trimIndent()

    val javaOrKtPureKeyRegex = """
        (?<=R.string\.)$pureKeyRegex
    """.trimIndent()

    val xmlPureKeyRegex = """
        (?<=@string/)$pureKeyRegex
    """.trimIndent()

    val iosPureKeyRegex = """
        (?<=RDLocalizedString\(@")[a-zA-Z0-9\u4e00-\u9fa5_.\\、。"]+(?="\))
    """.trimIndent()

    //包含%，不包含%s
    val iosSpecialRegex = """
        (?!%s)%
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


    fun line2NewLine(
        line: String,
        valueRegex: String,
        oldKey2NewKeyMap: Map<String, String>,
        replace: String = "%s",
        noNewKeyValue: String = "noNewKeyValue"
    ): Pair<String, Set<String>?> {
        val keyList = RegexUtils.getMatches(valueRegex, line)
        return if (keyList.isNotEmpty()) {
            val list = mutableSetOf<String>()
            val newFormatLine = RegexUtils.getReplaceAll(line, valueRegex, replace)
            val l = String.format(
                newFormatLine,
                *keyList.map {
                    val newKey = oldKey2NewKeyMap[it]
                    if (newKey == null) {
                        list.add(it)
                    }
                    newKey ?: noNewKeyValue
                }.toTypedArray()
            )
            Pair(l, list)
        } else {
            Pair(line, null)
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
}