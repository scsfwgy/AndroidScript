package com.matt.script.core.language

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2022/4/8 14:33
 * 描 述 ：
 * ============================================================
 **/
class LocalLanguage(
    val key: String,
    val chineseName: String,
    val showName: String
) {

    fun languageDir(iosType: Boolean): String {
        if (!iosType) {
            val append = "values"
            return if (key == DEFAULT_DIR_KEY) {
                append
            } else {
                val newAppend = "$append-"
                val split = key.split("-")
                if (split.size == 2) {
                    newAppend + split[0] + "-r" + split[1]
                } else {
                    newAppend + key
                }
            }
        } else {
            //根据具体需求完善
            val append = "lproj"
            return key + "." + append
        }

    }

    companion object {
        const val DEFAULT_DIR_KEY = "zh-CN"
    }
}