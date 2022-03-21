package com.matt.script.core

import com.matt.script.config.LogWrapper
import com.matt.script.core.interfaces.FileFilter
import com.matt.script.core.interfaces.FileFindKey
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.FileUtils
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
    FindUselessCore.findUselessStringXml()
    //FindUselessCore.findUselessDrawable()
    //println(FileUtilsWrapper.splitFileByDot(File("/Users/matt.wang/AsProject/Android-LBK//app/src/main/res/drawable-xxhdpi/common_shadow_blur_18_y_4.9.png")))
}

object FindUselessCore {
    var count = 0
    fun findUselessStringXml() {
        val dirPath = scanDirList()
        val stringXmlPath =
            "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res/values/strings.xml"
        val allExistSet = XmlCore.stringsXml2SortKeyList(stringXmlPath).toMutableSet()
        val uselessSet = findUselessSetWrapper(
            dirPath,
            allExistSet,
            RegexUtilsWrapper.javaOrKtPureStringKeyRegex,
            RegexUtilsWrapper.xmlPureStringKeyRegex
        )
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表：" + uselessSet)
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表个数：" + uselessSet.size)
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("在使用列表个数：" + (allExistSet.size - uselessSet.size))

        val languageDir = "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res"
        //后续逻辑自己定
        //删除无用key
        val stringsXml2SortedMap =
            XmlCore.stringsXml2SortedMap("$languageDir/values/strings.xml")
        val newMap = LinkedHashSet<String>()
        stringsXml2SortedMap.forEach {
            val contains = uselessSet.contains(it.key)
            if (!contains) {
                newMap.add(it.key)
            }
        }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("在使用列表个数：" + newMap.size)

        XmlCore.absStringXml2NewStringXml(languageDir, newMap, false)
    }


    fun findUselessDrawable() {
        val dirPath = scanDirList()
        val realList = FileUtilsWrapper.getFilterDirPath(dirPath, "res", object : FileFilter {
            override fun filter(file: File): Boolean {
                return file.isDirectory && file.name.startsWith("drawable")
            }
        }).map { it.path }
        val originKeyList = FileUtilsWrapper.scanDirList(realList, object : FileFilter {
            override fun filter(file: File): Boolean {
                return true
            }
        })
        val allExistSet = originKeyList.map {
            //val splitFileByDot = FileUtilsWrapper.splitFileByDot(it)
            //注意.9图，比如"common_shadow_blur_18_y_4.9.png"
            //splitFileByDot.first
            FileUtilsWrapper.getPureName(it)
        }.toMutableSet()

        val uselessSet = findUselessSetWrapper(
            dirPath,
            allExistSet,
            RegexUtilsWrapper.javaOrKtPureDrawableKeyRegex,
            RegexUtilsWrapper.xmlPureDrawableKeyRegex
        )
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表：" + uselessSet)
        //后续怎么处理，自己定
        val delFileList = originKeyList.filter {
            //不取后缀
            val splitFileByDot = FileUtilsWrapper.getPureName(it)
            uselessSet.contains(splitFileByDot)
        }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass)
            .debug("待删除文件：" + delFileList.size.toString() + "===>>" + delFileList)
        val sum = delFileList.sumOf { it.length() }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass)
            .debug("待删除文件大小（总计）：" + FileUtils.byte2FitMemorySize(sum, 2))

        val delDirs = "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/delDrawable"
        FileUtils.deleteAllInDir(delDirs)
        delFileList.forEach {
            FileUtils.copy(
                it.path,
                "$delDirs/" + ((count++).toString() + "_" + it.name
                        )
            )
        }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass)
            .debug("待删除文件已整理到：$delDirs")

        delFileList.forEach {
            it.deleteOnExit()
        }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass)
            .debug("无用文件已删除：${delFileList.size}")
    }

    fun findUselessSetWrapper(
        dirPath: List<String>,
        allExistSet: Set<String>,
        javaOrKtRegex: String,
        xmlRegex: String,
    ): Set<String> {
        val uselessSet = findUselessSet(dirPath, allExistSet, object : FileFindKey {
            override fun find(file: File): Set<String>? {
                val readText = file.readText()
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                return when (splitFileByDot.second) {
                    "java", "kt" -> {
                        RegexUtils.getMatches(javaOrKtRegex, readText).toSet()
                    }
                    "xml" -> {
                        RegexUtils.getMatches(xmlRegex, readText).toSet()
                    }
                    else -> {
                        null
                    }
                }
            }
        }, object : FileFilter {
            override fun filter(file: File): Boolean {
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                val second = splitFileByDot.second
                return second == "java" || second == "kt" || second == "xml" || second == "m"
            }
        })
        LogWrapper.loggerWrapper(FindUselessCore.javaClass)
            .debug("未使用列表：" + uselessSet.size.toString() + "===>>" + uselessSet)
        return uselessSet
    }

    fun findUselessSet(
        dirPath: List<String>,
        allExistSet: Set<String>,
        fileFindKey: FileFindKey,
        fileFilter: FileFilter
    ): Set<String> {
        val usageKeySet = HashSet<String>()
        FileUtilsWrapper.scanDirList(dirPath, fileFilter).forEach {
            val findKeySet = fileFindKey.find(it) ?: return@forEach
            if (findKeySet.isNotEmpty()) {
                LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug(findKeySet)
                findUsageKey(findKeySet, usageKeySet, allExistSet)
            }
        }
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("使用列表：" + usageKeySet.size)
        val uselessSet = allExistSet.toMutableSet()
        uselessSet.removeAll(usageKeySet)
        LogWrapper.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表：" + uselessSet.size)

        //可以删除未使用列表
        return uselessSet
    }

    fun findUsageKey(
        findKeySet: Set<String>,
        usageKeySet: MutableSet<String>,
        allExistSet: Set<String>
    ) {
        allExistSet.forEach {
            val contains = findKeySet.contains(it)
            if (contains) {
                usageKeySet.add(it)
                //LogUtils.loggerWrapper(FindUselessCore.javaClass).debug("==>" + it)
            }
        }
    }

    fun scanDirList(): List<String> {
        return listOf(
            "/Users/matt.wang/AsProject/Android-LBK//app/src/main",
            "/Users/matt.wang/AsProject/Android-LBK//lib_wrapper/src/main",
            "/Users/matt.wang/AsProject/Android-LBK//third_part_lib/MPChartLib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK//third_part_lib/mycommonlib/src/main",
            //"/Users/matt.wang/AsProject/Android-LBK//third_part_lib/faceidmodule/src/main",
            "/Users/matt.wang/AsProject/Android-LBK//lbankcorelib/src/main",
        )
    }
}