package com.matt.script.utils

import com.matt.script.utils.blankj.RegexUtils

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
}