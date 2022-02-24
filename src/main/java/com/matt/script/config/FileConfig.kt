package com.matt.script.config

object FileConfig {
    val backup = "BackUpFiles"

    //要抽取的安卓项目根地址
    val androidRootPath = "/Users/matt.wang/AndroidStudioProjects/Android-LBK"

    val moduleList = listOf(
        "app",
        "lib_wrapper"
    )

    val resRootDir = "src/main/res"

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

    //默认语言：默认全量key都在这个里面
    val defaultValuesName = "values"

    fun getFullDefaultValuesPath(module: String, valuesDirNameString: String): String {
        return listOf(androidRootPath, module, resRootDir, valuesDirNameString, stringsXmlFileName).joinToString(
            separator = "/"
        )
    }
}