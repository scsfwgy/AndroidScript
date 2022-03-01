package com.matt.script.utils

import com.matt.script.config.LogWrapper
import com.matt.script.core.interfaces.FileFilter
import com.matt.script.core.interfaces.LinePretreatment
import com.matt.script.utils.blankj.FileUtils
import com.matt.script.utils.blankj.TimeUtils
import java.io.File
import java.util.function.Consumer

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2020/8/14 10:50 AM
 * 描 述 ：
 * ============================================================
 **/
object FileUtilsWrapper {
    val TAG = FileUtilsWrapper::class.java.simpleName

    fun scanDirList(dirPath: List<String>, fileFilter: FileFilter): List<File> {
        val listFileByPath = listFileByPathList(dirPath)
        val realList = ArrayList<File>()
        listFileByPath.forEach { file ->
            if (fileFilter.filter(file)) {
                realList.add(file)
            }
        }
        return realList
    }

    /**
     * @param dirPath 扫描的文件夹
     * @param linePretreatment 要处理的行，不需要处理直接返回入参即可
     * @param fileFilter 处理哪些文件
     */
    fun scanDirListByLine(
        dirPath: List<String>,
        linePretreatment: LinePretreatment,
        fileFilter: FileFilter,
        scanFinishConsumer: Consumer<Boolean>
    ) {
        val scanDirList = scanDirList(dirPath, fileFilter)
        scanDirList.forEach { file ->
            replaceFileByLine(file, linePretreatment)
        }
        scanFinishConsumer.accept(true)
    }

    /**
     * 一行一行扫描文件，调用者完成替换规则，然后覆写文件
     */
    fun replaceFileByLine(file: File, linePretreatment: LinePretreatment) {
        if (!file.exists()) {
            throw IllegalAccessException("要操作的文件不存在：$file")
        }
        val backUpSuffix = ".backUp"
        val tempFile = File(file.parentFile.path, file.name + backUpSuffix)
        //清空
        tempFile.writeText("")
        val content = file.readText()
        val beforeReadLines = file.readLines()
        beforeReadLines.forEachIndexed { index, line1 ->
            val newLine = linePretreatment.line2NewLine(file, content, line1, index, beforeReadLines.size)
            //println(newLine)
            tempFile.appendText(newLine)
            //写入默认,最后一行就不要写入换行符
            if (index != beforeReadLines.size - 1) {
                tempFile.appendText("\n")
            }
        }

        replaceFile(file, tempFile)
    }

    fun compatibilityFile(oldContent: String, newContent: String): Boolean {
        return oldContent.trim() == newContent.trim()
    }

    fun replaceFile(oldFile: File, newFile: File) {
        //兼容逻辑:部分file操作完，只多（少）了一个空行："",这个时候就不替换了。暂时不知道原因。
//        val beforeReadLines = oldFile.readLines()
//        val tempReadLines = newFile.readLines()
//
//        val compatibility = tempReadLines == beforeReadLines
//                ||
//                (abs(tempReadLines.size - beforeReadLines.size) == 1 && (tempReadLines.last()
//                    .isEmpty() || beforeReadLines.last().isEmpty()))
        val compatibility = compatibilityFile(oldFile.readText(), newFile.readText())
        if (compatibility) {
            //if (tempReadLines.last() == "\n" || beforeReadLines.last() == "\n") {
            LogWrapper.loggerWrapper(this).debug("兼容逻辑:部分file操作完，只多（少）了一个空行,这个时候就不替换了。暂时不知道原因。不再替换的文件：" + oldFile.path)
            FileUtils.delete(newFile)
            return
            //}
        }
        //覆盖老文件
        val name = oldFile.name
        FileUtils.delete(oldFile)
        FileUtils.rename(newFile, name)
    }

    fun listFileByPathList(
        pathList: List<String>, filterSuffix: String? = null, filterPrefix: String? = null
    ): List<File> {
        val map = pathList.map { listFileByPath(it, filterSuffix, filterPrefix) }
        val list = ArrayList<File>()
        map.forEach {
            list.addAll(it)
        }
        return list
    }

    /**
     * 列出文件夹内的文件，支持前后缀过滤
     * @param filterPrefix 前缀 规则同[filterSuffix]
     * @param filterSuffix 后缀 ""：只显示没有后缀的文件 ；null:不过滤
     */
    fun listFileByPath(
        path: String, filterSuffix: String? = null, filterPrefix: String? = null
    ): List<File> {
        return FileUtils.listFilesInDirWithFilter(
            path, { file -> file != null && !file.isDirectory }, true
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
                        LogWrapper.loggerWrapper(FileUtilsWrapper.javaClass).debug("$filterSuffix,$suffixName" + false)
                        filterSuffix == suffixName
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    fun getFilterDirPath(dirPath: List<String>, childPath: String, fileFilter: FileFilter): List<File> {
        val map = dirPath.map { dir ->
            FileUtils.listFilesInDirWithFilter(
                "$dir/$childPath", { file ->
                    fileFilter.filter(file)
                }, true
            )
        }
        val list = ArrayList<File>()
        map.forEach {
            list.addAll(it)
        }
        return list
    }

    fun splitFileByDot(file: File): Pair<String, String?> {
        val name = file.name
        val index = name.lastIndexOf(".")
        if (index == -1 || index == name.length - 1) return Pair(name, null)
        val first = name.substring(0, index)
        val second = name.substring(index + 1)
        return Pair(first, second)
    }

    fun getPureName(file: File): String {
        val name = file.name
        if (!name.contains(".")) return name
        val indexOfFirst = name.indexOfFirst { it == '.' }
        return name.substring(0, indexOfFirst)
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
        FileUtils.createOrExistsFile(path)
        return File(path)
    }

    fun defaultTimeName(): String {
        return TimeUtils.millis2String(System.currentTimeMillis(), TimeUtils.getSafeDateFormat("yyyy-MM-dd_HH-mm-ss"))
    }
}