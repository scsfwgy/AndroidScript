package com.matt.script.core

interface PlaceHolderFilter {
    /**
     * 将占位符对应的内容替换为真实文案，因为有些占位符对应的内容可能包含其它东西。
     * 举例：
     * 1. showToast("你好啦啦啦啦xxx啦w333了12")=>
     *  占位符：showToast(%s)
     *  占位符对应值:"你好啦啦啦啦xxx啦w333了12"
     *  真实文案：你好啦啦啦啦xxx啦w333了12，
     * 2. android:text="你好22收到的"=>
     *  占位符：android:text="%s"
     *  占位符对应值:你好22收到的
     *  真实文案:你好22收到的
     */
    fun placeholder2RealWords(placeholder: String): String
}