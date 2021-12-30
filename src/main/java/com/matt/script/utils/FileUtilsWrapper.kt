package com.matt.script.utils

import com.matt.script.log.LoggerImp
import com.matt.script.utils.blankj.FileUtils
import java.io.File
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2020/8/14 10:50 AM
 * 描 述 ：
 * ============================================================
 **/
object FileUtilsWrapper {
    val TAG = FileUtilsWrapper::class.java.simpleName

    /**
     * 列出文件夹内的文件，支持前后缀过滤
     * @param filterPrefix 前缀 规则同[filterSuffix]
     * @param filterSuffix 后缀 ""：只显示没有后缀的文件 ；null:不过滤
     */
    fun listFileByPath(
        path: String,
        filterSuffix: String? = null,
        filterPrefix: String? = null
    ): List<File> {
        return FileUtils.listFilesInDirWithFilter(
            path,
            { file -> file != null && !file.isDirectory }, true
        ).filter {
            if (filterPrefix == null && filterSuffix == null) {
                true
            } else if (filterPrefix != null && filterSuffix != null) {
                val fileName = it.name
                fileName == "$filterPrefix.$filterSuffix"
            } else {
                val fileName = it.name
                val dotIndex = fileName.indexOfLast { char ->
                    char == '.'
                }
                val prefixName = if (dotIndex != -1) {
                    fileName.substring(0, dotIndex)
                } else {
                    ""
                }
                val suffixName = if (dotIndex != -1) {
                    fileName.substring(dotIndex + 1)
                } else {
                    ""
                }
                when {
                    filterPrefix != null -> {
                        filterPrefix == prefixName
                    }
                    filterSuffix != null -> {
                        LoggerImp.logger.d(TAG, "$filterSuffix,$suffixName", false)
                        filterSuffix == suffixName
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    fun listFileListByPath(path: String) {

    }

    fun splitFileByDot(file: File): Pair<String, String?> {
        val name = file.name
        val index = name.lastIndexOf(".")
        if (index == -1 || index == name.length - 1) return Pair(name, null)
        val first = name.substring(0, index)
        val second = name.substring(index + 1)
        return Pair(first, second)
    }

    fun readLine(file: File): List<String> {
        if (FileUtils.isDir(file)) throw IllegalArgumentException("读取的文件必须是文件，不能是文件夹")
        return file.readLines()
    }

    fun writeFile(file: File, lines: List<String>) {
        if (FileUtils.isDir(file)) throw IllegalArgumentException("写入的文件必须是文件，不能是文件夹")
        val msg = StringBuilder()
        lines.forEach {
            msg.append(it)
        }
        file.writeText(msg.toString())
    }

    fun getDirByCreate(path: String): String {
        val file1 = File(path)
        if (!file1.exists()) {
            file1.mkdirs()
        }
        return path
    }

    fun getFileByCreate(path: String): File {
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}