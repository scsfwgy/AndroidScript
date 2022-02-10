package com.matt.script.core

import java.io.File

interface KeyConvert {
    /**
     * @param file 要操作的文件
     * @param pureKeyRegular 提取代码中key的正则，比如"...getKString(R.string.account_appeal_layout7)...",则该正则获取的是：account_appeal_layout7
     * @param oldKey2NewKeyMap 根据旧key获取新key的字典
     * @param noNewKeyValue 获取不到新key时显示的内容。null:则继续显示旧key。
     */
    fun convert(
        file: File,
        pureKeyRegular: String,
        oldKey2NewKeyMap: Map<String, String>,
        noNewKeyValue: String? = null,
        fileFilter: FileFilter? = null,
    )
}