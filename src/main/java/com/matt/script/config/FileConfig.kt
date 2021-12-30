package com.matt.script.config

object FileConfig {
    //当前项目跟地址
    val currRootPath = "/Users/matt.wang/IdeaProjects/AndroidScript"

    val backup = "BackUpFiles"

    //要抽取的安卓项目根地址
    val androidRootPath = "/Users/matt.wang/AsProject/Android-LBK"

    val moduleList = listOf(
        "app",
        "lib_wrapper"
    )

    val resRootDir = "src/main/res"

    val languageDirNameList = listOf(
        Pair("values", "values(默认语言，中文)"),
        Pair("values-ar", "values-ar(阿拉伯语)"),
        Pair("values-de", "values-de(德语)"),
        Pair("values-en", "values-en(英文)"),
        Pair("values-es-rES", "values-es-rES(西班牙文)"),
        Pair("values-fa", "values-fa(波斯语)"),
        Pair("values-hi", "values-hi(印地语)"),
        Pair("values-it", "values-it(意大利语)"),
        Pair("values-kn", "values-kn(印尼文)"),
        Pair("values-ko", "values-ko(韩文)"),
        Pair("values-pt-rPT", "values-pt-rPT(葡萄牙文)"),
        Pair("values-ru-rRU", "values-ru-rRU(俄语)"),
        Pair("values-tr-rTR", "values-tr-rTR(土耳其文)"),
        Pair("values-vi", "values-vi(越南文)"),
        Pair("values-zh-rTW", "values-zh-rTW(繁体中文)"),
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