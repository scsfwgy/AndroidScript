package com.matt.script.core.language

import java.io.File

interface LineFilter {
    fun accept(file: File, line: String): Boolean
}