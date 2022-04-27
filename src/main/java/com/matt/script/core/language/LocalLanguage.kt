package com.matt.script.core.language

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2022/4/8 14:33
 * 描 述 ：
 * ============================================================
 **/
class LocalLanguage(
    val id: Int,
    val key: String,
    val chineseName: String,
    val englishName: String,
    val showName: String,
    val androidLocal: String,
    val iOSLocal: String,
    val webLocal: String,
    val newHomeLocal: String,
    val rtl: Boolean = false,
    val apiKey: String = "en-US",
) {

    fun languageDir(iosType: Boolean): String {
        if (!iosType) {
            val append = "values"
            if (key == DEFAULT_DIR_KEY) return append
            if (key == DEFAULT_LANGUAGE_KEY) return append + "-en"
            return append + "-" + androidLocal
        } else {
            //根据具体需求完善
            val append = "lproj"
            return key + "." + append
        }

    }

    companion object {
        const val DEFAULT_DIR_KEY = "zh-CN"
        const val DEFAULT_LANGUAGE_KEY = "en-US"
    }
}