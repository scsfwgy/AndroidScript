package com.matt.script.core.language

import com.matt.script.utils.FileUtilsWrapper
import java.io.File

class ScanFileLanguageImpl : ScanFileLanguage {
    override fun scanAndReplace(file: File, realMutableLanguageMap: Map<String, String>): Map<String, String>? {
        val splitFileByDot = FileUtilsWrapper.splitFileByDot(file)
        val readLines = file.readLines()
        readLines.forEach {

        }
        return mapOf()
    }
}