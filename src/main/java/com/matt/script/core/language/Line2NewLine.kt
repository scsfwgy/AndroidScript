package com.matt.script.core.language

import java.io.File

interface Line2NewLine {
    fun action(file: File, line: String, lineFilter: LineFilter): String
}