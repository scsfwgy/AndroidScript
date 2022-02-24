package com.matt.script.core.interfaces

import java.io.File

interface FileFilter {
    fun filter(file: File): Boolean
}