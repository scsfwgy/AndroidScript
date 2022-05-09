package com.matt.script.core

import com.matt.script.config.LogWrapper
import com.matt.script.core.interfaces.*
import com.matt.script.utils.FileUtilsWrapper
import com.matt.script.utils.RegexUtilsWrapper
import com.matt.script.utils.blankj.RegexUtils
import java.io.File
import java.io.FileFilter

fun main() {
    Code2StringXmlCore.lbkAndroidDemo()
}

/**
 * 扫描代码（java、kotlin、xml）中的文案字符串，并抽取到strings.xml中
 */
object Code2StringXmlCore {
    var count = 0

    fun lbkAndroidDemo() {
        val defaultTimeName = FileUtilsWrapper.defaultTimeName()
        val basePath = "/Users/matt.wang/AsProject/Android-LBK"
        androidDemo(
            listOf(
                "${basePath}/app/src/main",
                "${basePath}/lib_wrapper/src/main",
                "${basePath}/im/lib_im/src/main",
                //"/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/temp"
            ),
            XmlCore.stringsXml2SortedMap("${basePath}/lib_wrapper/src/main/res/values/strings.xml"),
            //"/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/values/${defaultTimeName}/strings.xml"
            "${basePath}/lib_wrapper/src/main/res/values/strings.xml"
        )
    }


    fun androidDemo(scanDirList: List<String>, sortMap: Map<String, String>, generateStringXmlPath: String) {
        LogWrapper.loggerWrapper(this).debug("处理文件：" + scanDirList + "," + sortMap.size + "," + generateStringXmlPath)
        LogWrapper.loggerWrapper(this).debug("开始扫描对应文件夹")
        code2StringXmlCore(scanDirList, sortMap, generateStringXmlPath, filePretreatment = object : FilePretreatment {
            override fun parse(file: File): Triple<String?, String?, PlaceHolderFilter?>? {
                val fileByDot = FileUtilsWrapper.splitFileByDot(file)
                return when (fileByDot.second) {
                    "java", "kt" -> {
                        return Triple(RegexUtilsWrapper.containsZhRegex,
                            """MyContextUtils2.getString(R.string.%s)""".trimIndent(),
                            object : PlaceHolderFilter {
                                override fun placeholder2RealWords(placeholder: String): String {
                                    return placeholder.replace(
                                        """
                                            "
                                        """.trimIndent(), ""
                                    )
                                }

                            })
                    }
                    "xml" -> {
                        return Triple(RegexUtilsWrapper.xmlContainerZhContentRegex,
                            """@string/%s""".trimIndent(),
                            object : PlaceHolderFilter {
                                override fun placeholder2RealWords(placeholder: String): String {
                                    return placeholder
                                }

                            })
                    }
                    else -> {
                        null
                    }
                }
            }

        }, importClass = object : ImportClass {
            override fun importAction(file: File): Boolean {
                val fileByDot = FileUtilsWrapper.splitFileByDot(file)
                val ext = fileByDot.second
                return when (ext) {
                    "java", "kt" -> {
                        val fileContent = file.readText()
                        //判定是否需要导包
                        if (RegexUtils.getMatches(RegexUtilsWrapper.javaOrKtPureStringKeyRegex, fileContent).size > 0) {
                            val rClass = "import com.lbk.lib_wrapper.R"
                            val stringRClass = "import com.lbk.lib_wrapper.widget.language.MyContextUtils2"
                            val appendList = ArrayList<String>()
                            if (RegexUtils.getMatches(
                                    RegexUtilsWrapper.javaOrKtContainerR, fileContent
                                ).size == 0
                            ) {
                                appendList.add(rClass)
                            } else if (!fileContent.contains("import com.yzj.mycommonlib.Common.MyContextUtils")
                                && !fileContent.contains("import com.lbk.lib_wrapper.utils.ResourcesUtils")
                                && !fileContent.contains("import com.lbk.lib_wrapper.widget.language.MyContextUtils2")
                            ) {
                                appendList.add(stringRClass)
                            }
                            val newAppend = appendList.joinToString(separator = "\n")
                            val newContent = file.readLines().mapIndexed { index, s ->
                                if (index == 1 && newAppend.isNotEmpty()) {
                                    s + "\n" + newAppend + (if (ext == "java") ";" else "") + "\n"
                                } else {
                                    s
                                }
                            }.joinToString(separator = "\n")
                            val compatibilityFile = FileUtilsWrapper.compatibilityFile(file.readText(), newContent)
                            if (!compatibilityFile) {
                                file.writeText(newContent)
                            }
                            return true
                        }
                        return false
                    }
                    else -> {
                        false
                    }
                }
            }
        }, linePretreatmentHook = object : LinePretreatmentHook {
            override fun line2NewLineHook(file: File, line: String): Pair<Boolean, String> {
                fun hook(list: List<String>): Boolean {
                    list.forEach {
                        if (line.contains(it)) {
                            return true
                        }
                    }
                    return false
                }

                //原有过滤逻辑
                val list = listOf(
                    //app
                    "HelpActivity",
                    "MyAvgResponseTimeMonitor",
                    "Common/Debug",
                    "modules/Demo",
                    "IDCardUtils",
                    "MyLanguageManager",
                    "MyLanguageUtils",
                    "MyCrashHandler",
                    "SampleApplicationLike",
                    "MyDebugView",
                    "hit_new_helper_activity_layout",
                    "FutureManager",
                    "UserInfoUtils",
                    "LocalWalletWithdraw",
                    "AppWalletServiceV2",
                    //wrapper
                    //"widget",
                    "base",
                    "utils",
                    "exception",
                    "config",
                    "cache",
                    "HitNewManager",
                    "DebugUtils",
                    "LubanSimpleObserver",
                    "SignManager",
                    "JPushManager",
                    "BuglyManager",
                    "TestThemeActivity",
                    "RouterData",
                    "ARouterWrapper",
                    "activity_test_theme",
                    "DebugActivity",
                )
                val name = file.path
                if (list.any { name.contains(it) }) {
                    LogWrapper.loggerWrapper(this).debug("过滤该文件：" + file.path)
                    return Pair(true, line)
                }

                val hook = hook(
                    listOf(
                        "const val",
                        "@Deprecated(",
                        "@SerializedName(",
                        "Log.",
                        "/*",
                        "//",
                        "*/"
                    )
                ) ||
                        //注释行
                        line.trim().startsWith("*")
                if (hook) {
                    LogWrapper.loggerWrapper(this).debug("过滤该文件：" + file.path)
                }
                return Pair(hook, line)
            }

        })
    }

    /**
     * @param scanPathList 待扫描处理的文件夹，越精确越好
     * @param defaultStringXml2SortMap 默认语言文件strings.xml转化为原有文案顺序的Map。注意：我们认为默认语言strings.xml文案是最全的。
     * @param newOutputStringXmlPath 处理完的strings.xml，您希望输出到哪？测试完毕后，建议直接输出到项目默认strings.xml所在的文件夹，直接覆盖。
     * @param filePretreatment 文件预处理
     * @param importClass 导包处理，上层调用者处理
     */
    fun code2StringXmlCore(
        scanPathList: List<String>,
        defaultStringXml2SortMap: Map<String, String>,
        newOutputStringXmlPath: String,
        filePretreatment: FilePretreatment,
        importClass: ImportClass,
        linePretreatmentHook: LinePretreatmentHook,
    ) {
        val oldKey2NewKeyMap = defaultStringXml2SortMap.toMutableMap()

        //新生成的key集合
        val newKey = LinkedHashSet<String>()

        val filter = FileFilter { file ->
            val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
            val second = splitFileByDot.second
            //只替换这些文件
            //return second != null && (second == "java" || second == "kt")
            second != null && (second == "java" || second == "kt" || second == "xml" || second == "m")
            //return second != null && (second == "m")
            //return second != null && (second == "xml")
            //return true
        }

        //扫描出所有符合条件的文件
        val fileList = FileUtilsWrapper.scanDirList(scanPathList, filter)

        fileList.forEach { file ->
            //文件预处理
            val fileParseTriple = filePretreatment.parse(file)

            FileUtilsWrapper.replaceFileByLine(file, object : LinePretreatment {
                override fun line2NewLine(
                    file: File, fileContent: String, line: String, lineIndex: Int, lineSize: Int
                ): String {
                    val lineHookPair = linePretreatmentHook.line2NewLineHook(file, line)
                    val newLine = lineHookPair.second
                    if (lineHookPair.first) {
                        return newLine
                    }
                    val first = fileParseTriple?.first ?: return newLine
                    val second = fileParseTriple.second
                    val placeHolderFilter = fileParseTriple.third ?: return newLine

                    val placeholder = "~~~~~~~~~"
                    val special = "%"
                    val line2FormatLine = RegexUtilsWrapper.line2FormatLine(
                        newLine.replace(special, placeholder),
                        first,
                        formatLineConvert = object : FormatLineConvert {
                            override fun formatLine2NewLine(
                                formatLine: String, placeholderList: List<String>?
                            ): String {
                                if (placeholderList.isNullOrEmpty()) return formatLine
                                val valueList = placeholderList.map {
                                    val pureWord = placeHolderFilter.placeholder2RealWords(it)
                                    val generateNewKey = value2StringKeyAuto(file, pureWord, oldKey2NewKeyMap)
                                    val key = generateNewKey.second
                                    if (generateNewKey.first) {
                                        newKey.add(key)
                                    }
                                    second?.format(key) ?: ""
                                }.toTypedArray()
                                return formatLine.format(*valueList)
                            }
                        })
                    return line2FormatLine.replace(placeholder, special)
                }
            })
        }
        LogWrapper.loggerWrapper(this).debug("====================开始全局处理新文件导包逻辑===========================")
        val newScanDirList = FileUtilsWrapper.scanDirList(scanPathList, filter)
        newScanDirList.forEach {
            val importAction = importClass.importAction(it)
            if (importAction) {
                LogWrapper.loggerWrapper(this).debug("该文件被处理导包逻辑：" + it.path)
            }
        }


        LogWrapper.loggerWrapper(this).debug("====================解析结束===========================")
        LogWrapper.loggerWrapper(this)
            .debug("新扫描生成的key大小:" + newKey.size + "==>>" + newKey.map { it + "===>" + oldKey2NewKeyMap[it] })
        LogWrapper.loggerWrapper(this).debug("新扫描生成的key列表")
        newKey.forEach {
            println(it + "===>" + oldKey2NewKeyMap[it])
        }

        //开始写入xml
        val pairList2StringXml = XmlCore.pairList2StringXml(oldKey2NewKeyMap.toList())
        //println(pairList2StringXml)

        LogWrapper.loggerWrapper(this).debug("------")

        val stringXmlPath = FileUtilsWrapper.getFileByCreate(newOutputStringXmlPath)
        stringXmlPath.writeText(pairList2StringXml)
        LogWrapper.loggerWrapper(this).debug("操作完成：" + stringXmlPath)
    }

    fun value2StringKeyAuto(file: File, value: String, map: MutableMap<String, String>): Pair<Boolean, String> {
        val value2StringKey = value2StringKey(file, value, map)
        val key = value2StringKey.second
        if (value2StringKey.first) {
            map[key] = value
        }
        return Pair(value2StringKey.first, key)
    }

    fun value2StringKey(file: File, value: String, map: Map<String, String>): Pair<Boolean, String> {
        val key = map.filterValues { it == value }.keys.firstOrNull()
        return if (key == null) {
            val generateNewKey = generateNewKey(file, map)
            Pair(true, generateNewKey)
        } else {
            Pair(false, key)
        }
    }

    fun generateNewKey(file: File, map: Map<String, String>): String {
        val fileByDot = FileUtilsWrapper.splitFileByDot(file)
        val key = fileByDot.first + "_" + (fileByDot.second ?: "") + "_" + System.currentTimeMillis() + "_" + count++
        if (map[key] != null) {
            throw IllegalAccessException("新生成的key已存在！！！=》" + key)
        }
        return key
    }
}