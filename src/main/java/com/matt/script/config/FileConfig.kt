package com.matt.script.config

object FileConfig {
    val backup = "BackUpFiles"

    //要抽取的安卓项目根地址
    val androidRootPath = "/Users/matt.wang/AsProject/Android-LBK"

    val moduleList = listOf(
        "app",
        "lib_wrapper"
    )

    val resRootDir = "src/main/res"

    fun fullModuleList(): List<String> {
        return moduleList.map { "$androidRootPath/$it" }
    }

    fun fullResRootDir(): List<String> {
        return fullModuleList().map { "$it/$resRootDir" }
    }

    /**
     * 获取项目语言所在的根目录
     */
    fun languageResRootDir(): String {
        return fullResRootDir()[1]
    }

    val languageDirNameList = listOf(
        Triple("values", "zh-CN", "默认语言，中文"),
        Triple("values-en", "en-US", "英文"),
        Triple("values-zh-rTW", "zh-TW", "繁体中文"),
        Triple("values-ko", "ko-KR", "韩文"),
        Triple("values-vi", "vi-VN", "越南文"),
        Triple("values-pt-rPT", "pt", "葡萄牙文"),
        Triple("values-es-rES", "es", "西班牙文"),
        Triple("values-kn", "id", "印尼文"),
        Triple("values-ru-rRU", "ru", "俄语"),
        Triple("values-tr-rTR", "tr", "土耳其文"),
        Triple("values-de", "de", "德语"),
        Triple("values-it", "it", "意大利语"),
        Triple("values-hi", "hi", "印地语"),
        Triple("values-ar", "ar", "阿拉伯语"),
        Triple("values-fa", "fa", "波斯语"),
    )
    val stringsXmlFileName = "strings.xml"

    val languageDirNameListIOS = listOf(
        Triple("zh-Hans.lproj", "zh-CN", "默认语言，中文"),
        Triple("en.lproj", "en-US", "英文"),
        Triple("zh-HK.lproj", "zh-TW", "繁体中文"),
        Triple("ko-KR.lproj", "ko-KR", "韩文"),
        Triple("vi-VN.lproj", "vi-VN", "越南文"),
        Triple("pt.lproj", "pt", "葡萄牙文"),
        Triple("es.lproj", "es", "西班牙文"),
        Triple("id.lproj", "id", "印尼文"),
        Triple("ru.lproj", "ru", "俄语"),
        Triple("tr.lproj", "tr", "土耳其文"),
        Triple("de.lproj", "de", "德语"),
        Triple("it.lproj", "it", "意大利语"),
        Triple("hi.lproj", "hi", "印地语"),
        Triple("ar.lproj", "ar", "阿拉伯语"),
        Triple("fa.lproj", "fa", "波斯语"),
    )
    val stringsXmlFileNameIOS = "RDLocalizable.strings"

    //默认语言：默认全量key都在这个里面
    val defaultValuesName = "values"

    fun getFullDefaultValuesPath(module: String, valuesDirNameString: String): String {
        return listOf(androidRootPath, module, resRootDir, valuesDirNameString, stringsXmlFileName).joinToString(
            separator = "/"
        )
    }
}