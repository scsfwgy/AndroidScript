package com.matt.script.core.interfaces

import java.io.File

/**
 * 是否需要导包处理
 */
interface ImportClass {
    fun importAction(file: File): Boolean
}