package com.matt.script.core

import java.io.File

interface FileFindKey {
    fun find(file: File): Set<String>?
}