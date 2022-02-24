package com.matt.script.core.interfaces

interface FormatLineConvert {
    /**
     * 示例：你好包子,我是饺子=>
     * @param formatLine:你好%s,我是%s
     * @param placeholderList listof("包子","饺子")
     * @return 使用者处理后的line:你好包子,我是饺子
     */
    fun formatLine2NewLine(formatLine: String, placeholderList: List<String>?): String
}