import com.matt.script.AndroidScript
import com.matt.script.core.KeyConvertCore

fun main(args: Array<String>) {
    /**
     * 把项目中语言配置文件string.xml文件抽取成Excel
     */
    AndroidScript.extractAndroidProject2Excel()
    /**
     * 将Excel回写成string.xml
     */
    //AndroidScript.excel2StringsXml()

    /**
     * 项目中旧key替换成新key
     */
    KeyConvertCore.scanCore()
}