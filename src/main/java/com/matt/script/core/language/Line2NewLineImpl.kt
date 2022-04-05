package com.matt.script.core.language

import java.io.File

class Line2NewLineImpl : Line2NewLine {
    override fun action(file: File, line: String, lineFilter: LineFilter): String {
        if (!lineFilter.accept(file, line)) return line
        return ""
    }
}