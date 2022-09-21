package com.matt.script.core

import com.matt.script.config.LogWrapper
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.RegexUtils

/**
 * 1. 扫描整个代码库，找到所有不存在在语言配置中的文案列出来，同时列出来已在语言配置中存在的文案（不需要替换）。
 * 2. 将新扫描的文案给运营，填写新的Key,获取到最新的语言配置
 * 3. 用最新的语言配置扫描代码库，替换文案为最新的key
 */

fun main() {
    //test1()
    test3()
}

fun test1() {
    val path =
        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language/zh-Hans.lproj/RDLocalizable.strings"
    val keyList =
        IosScanCore.findNoKeyListWrapper(listOf("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios"), path)
    println("扫描到但是已存在的文案（暂时不需要管）：" + keyList.second)
    println("最终结果：新文案：" + keyList.first)
}

fun test3() {
    val path =
        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language/zh-Hans.lproj/RDLocalizable.strings"
    IosScanCore.replaceNewValueByKeyWrapper(listOf("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios"), path)
}

object IosScanCore {

    /**
     * 1. 扫描整个代码库，找到所有不存在在语言配置中的文案列出来，同时列出来已在语言配置中存在的文案（不需要替换）。
     */
    fun findNoKeyListWrapper(
        dirList: List<String>, rDLocalizablePath: String, regex: String = RegexUtilsWrapper.iosPureKeyRegex2
    ): Pair<HashSet<String>, HashSet<String>> {
        val pair = findNoKeyList(dirList, XmlCore.rDLocalizable2SortMap(rDLocalizablePath), regex)
        println("扫描到但是已存在的文案（暂时不需要管）：" + pair.second)
        println("最终结果：新文案：" + pair.first)
        return pair
    }

    /**
     * 3. 用最新的语言配置扫描代码库，替换文案为最新的key
     */
    fun replaceNewValueByKeyWrapper(
        dirList: List<String>, rDLocalizablePath: String, regex: String = RegexUtilsWrapper.iosPureKeyRegex2
    ) {
        replaceNewValueByKey(dirList, XmlCore.rDLocalizable2SortMap(rDLocalizablePath), regex)
    }


    fun findNoKeyList(
        dirList: List<String>, sortMap: Map<String, String>, regex: String = RegexUtilsWrapper.iosPureKeyRegex2
    ): Pair<HashSet<String>, HashSet<String>> {
        val fileList = FileUtilsWrapper.listFileByPathList(dirList, "m")
        val noKeyValueList = HashSet<String>()
        val hasKeyValueList = HashSet<String>()
        fileList.filter { it.isFile }.forEach {
            val readText = it.readText()
            val valueList = RegexUtils.getMatches(regex, readText)
            valueList.forEach { value ->
                val contains = sortMap.values.contains(value)
                if (contains) {
                    hasKeyValueList.add(value)
                } else {
                    noKeyValueList.add(value)
                }
            }
        }
        return Pair(noKeyValueList, hasKeyValueList)
    }

    fun replaceNewValueByKey(
        dirList: List<String>, newKeySortMap: Map<String, String>, regex: String = RegexUtilsWrapper.iosPureKeyRegex2
    ) {
        val fileList = FileUtilsWrapper.listFileByPathList(dirList, "m")
        fileList.filter { it.isFile }.forEach {
            val readText = it.readText()
            val list = RegexUtils.getMatches(regex, readText)
            val newContent = content2NewContent(readText, regex, list, newKeySortMap)
            it.writeText(newContent)
        }
    }


    fun content2NewContent(
        contentText: String, regex: String, valueList: List<String>, newKeySortMap: Map<String, String>
    ): String {
        //防止特殊字符报错
        val placeholder = "~~~~~~~~~"
        val special = "%"
        val tempContent = contentText.replace(special, placeholder)
        val newFormatTxt = RegexUtils.getReplaceAll(tempContent, regex, "%s")
        val newKeyList = valueList.map { value ->
            val findKeyList = newKeySortMap.filter { it.value == value }.keys
            //注意：这里可以找到多个符合文案的map，取第一个
            val firstKey = findKeyList.firstOrNull()
            if (firstKey == null) {
                LogWrapper.loggerWrapper(this).error("这个文案在语言配置中没找到对应key,暂时保持原样，不替换:=========>>>" + value)
            }
            firstKey ?: value
        }
        val newContentTxt = newFormatTxt.format(*newKeyList.toTypedArray())

        //回写替换特殊字符
        return newContentTxt.replace(placeholder, special)
    }
}