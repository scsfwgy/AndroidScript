import com.matt.script.core.KeyConvertCore

fun main(args: Array<String>) {
    /**
     * 把项目中语言配置文件string.xml文件抽取成Excel
     */
    //AndroidScript.extractAndroidProject2Excel()
    /**
     * 将Excel回写成string.xml
     */
    //AndroidScript.excel2StringsXml()

    /**
     * 自动扫描出代码中文案并生成strings.xml
     */
    //Code2StringXmlCore.lbkAndroidDemo()

    /**
     * 项目中旧key替换成新key
     */
    KeyConvertCore.oldKey2NewKey()
}