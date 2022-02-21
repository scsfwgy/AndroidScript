package com.matt.script.core

import com.matt.script.log.LogUtils
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.FileUtils
import com.matt.script.utils.blankj.RegexUtils
import java.io.File

fun main() {
    //FindUselessCore.findUselessStringXml()
    FindUselessCore.findUselessDrawable()
    //println(FileUtilsWrapper.splitFileByDot(File("/Users/matt.wang/AsProject/Android-LBK/app/src/main/res/drawable-xxhdpi/common_shadow_blur_18_y_4.9.png")))
}

object FindUselessCore {
    fun findUselessStringXml() {
        val dirPath = listOf(
            "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/MPChartLib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/mycommonlib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/faceidmodule/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lbankcorelib/src/main",
        )
        val stringXmlPath =
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/mycommonlib/src/main/res/values/strings.xml"
        val allExistSet = XmlCore.stringsXml2SortKeyList(stringXmlPath).toMutableSet()
        val uselessSet = find(dirPath, allExistSet, object : FileFindKey {
            override fun find(file: File): Set<String>? {
                val readText = file.readText()
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                return when (splitFileByDot.second) {
                    "java", "kt" -> {
                        RegexUtils.getMatches(RegexUtilsWrapper.javaOrKtPureStringKeyRegex, readText).toSet()
                    }
                    "xml" -> {
                        RegexUtils.getMatches(RegexUtilsWrapper.xmlPureStringKeyRegex, readText).toSet()
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
        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表：" + uselessSet)
        //后续怎么处理，自己定
    }


    fun findUselessDrawable() {
        val dirPath = listOf(
            "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/MPChartLib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/mycommonlib/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/third_part_lib/faceidmodule/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lbankcorelib/src/main",
        )

        val realList = FileUtilsWrapper.getFilterDirPath(dirPath, "res", object : FileFilter {
            override fun filter(file: File): Boolean {
                return file.isDirectory && file.name.startsWith("drawable")
            }
        }).map { it.path }

        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug(realList)

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

        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug(allExistSet.size.toString() + "===>>" + allExistSet)
        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug(allExistSet.size.toString())

        val uselessSet = find(dirPath, allExistSet, object : FileFindKey {
            override fun find(file: File): Set<String>? {
                val readText = file.readText()
                val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
                return when (splitFileByDot.second) {
                    "java", "kt" -> {
                        RegexUtils.getMatches(RegexUtilsWrapper.javaOrKtPureDrawableKeyRegex, readText).toSet()
                    }
                    "xml" -> {
                        RegexUtils.getMatches(RegexUtilsWrapper.xmlPureDrawableKeyRegex, readText).toSet()
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
        LogUtils.loggerWrapper(FindUselessCore.javaClass)
            .debug("未使用列表：" + uselessSet.size.toString() + "===>>" + uselessSet)
        //后续怎么处理，自己定
        val delFileList = originKeyList.filter {
            //不取后缀
            val splitFileByDot = FileUtilsWrapper.getPureName(it)
            uselessSet.contains(splitFileByDot)
        }
        LogUtils.loggerWrapper(FindUselessCore.javaClass)
            .debug("待删除文件：" + delFileList.size.toString() + "===>>" + delFileList)
        val sum = delFileList.sumOf { it.length() }
        LogUtils.loggerWrapper(FindUselessCore.javaClass)
            .debug("待删除文件大小（总计）：" + FileUtils.byte2FitMemorySize(sum, 2))
//        delFileList.forEach {
//            it.deleteOnExit()
//        }
    }


    fun find(
        dirPath: List<String>,
        allExistSet: Set<String>,
        fileFindKey: FileFindKey,
        fileFilter: FileFilter
    ): Set<String> {
        val usageKeySet = HashSet<String>()
        FileUtilsWrapper.scanDirList(dirPath, fileFilter).forEach {
            val findKeySet = fileFindKey.find(it) ?: return@forEach
            if (findKeySet.isNotEmpty()) {
                //LogUtils.loggerWrapper(FindUselessCore.javaClass).debug(findKeySet)
                findUsageKey(findKeySet, usageKeySet, allExistSet)
            }
        }
        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug("使用列表：" + usageKeySet.size)
        val uselessSet = allExistSet.toMutableSet()
        uselessSet.removeAll(usageKeySet)
        LogUtils.loggerWrapper(FindUselessCore.javaClass).debug("未使用列表：" + uselessSet.size)

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
}