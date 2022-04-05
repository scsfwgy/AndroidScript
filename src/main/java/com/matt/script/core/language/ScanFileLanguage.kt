package com.matt.script.core.language

import java.io.File

interface ScanFileLanguage {
    fun scanAndReplace(file: File, realMutableLanguageMap: Map<String, String>): Map<String, String>?
}