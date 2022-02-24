package com.matt.script.core.interfaces

import java.io.File

interface FileFindKey {
    fun find(file: File): Set<String>?
}