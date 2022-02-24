package com.matt.script.core.interfaces

import java.io.File

interface FilePretreatment {
    /**
     * 解析文件配置，核心看这里注释。
     * Triple:
     * first:匹配上的内容
     * second：替换的内容，%s对应的文案
     * third：占位符后置处理，看PlaceHolderFilter注释
     *
     *  @return
     * 举例：
     * 1. showToast("你好啦啦啦啦xxx啦w333了12")=>first:"你好啦啦啦啦xxx啦w333了12"  second:YourContext.getString(R.string.%s)
     * 2. android:text="你好22收到的"=>first:你好22收到的  second:@string/%s
     */
    fun parse(file: File): Triple<String?, String?, PlaceHolderFilter?>?
}