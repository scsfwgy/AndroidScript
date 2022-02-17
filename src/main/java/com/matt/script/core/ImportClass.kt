package com.matt.script.core

import java.io.File

/**
 * 是否需要导包处理
 */
interface ImportClass {
    fun importAction(file: File, fileContent: String, line: String, lineIndex: Int): String?
}