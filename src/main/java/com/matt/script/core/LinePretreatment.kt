package com.matt.script.core

import java.io.File

interface LinePretreatment {
    /**
     * 根据文件行信息返回新的行
     */
    fun line2NewLine(file: File, fileContent: String, line: String, lineIndex: Int, lineSize: Int): String
}