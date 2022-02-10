package com.matt.script.core

import java.io.File

interface FileFilter {
    fun filter(file: File): Boolean
}