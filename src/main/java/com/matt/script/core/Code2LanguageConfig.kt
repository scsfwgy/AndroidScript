package com.matt.script.core

import com.matt.script.core.interfaces.FileFilter
import com.matt.script.core.language.ScanFileLanguage
import com.matt.script.utils.FileUtilsWrapper

object Code2LanguageConfig {
    /**
     * @param scanDirList 要扫描的文件件
     * @param alreadyExistLanguageSortMap 配置文件对应的排好序map
     * @param fileFilter 对扫描的文件的过滤
     * @param scanFileLanguage 核心逻辑：输入文件，已存在map=>完成key的替换同时返回新增key
     * @return 返回最新的语言配置map
     */
    fun core(
        scanDirList: List<String>,
        alreadyExistLanguageSortMap: Map<String, String>,
        fileFilter: FileFilter,
        scanFileLanguage: ScanFileLanguage,
    ): MutableMap<String, String> {
        //可变的map，最终的结果
        val realMutableLanguageMap = alreadyExistLanguageSortMap.toMutableMap()
        //扫描文件，同时对不满足条件的文件进行过滤
        val fileList = FileUtilsWrapper.scanDirList(scanDirList, fileFilter)
        fileList.forEach {
            //核心逻辑：输入文件，已存在map=>完成key的替换同时返回新增key
            val newGenerateMap = scanFileLanguage.scanAndReplace(it, realMutableLanguageMap)
            if (!newGenerateMap.isNullOrEmpty()) {
                realMutableLanguageMap.putAll(newGenerateMap)
            }
        }
        return realMutableLanguageMap
    }
}