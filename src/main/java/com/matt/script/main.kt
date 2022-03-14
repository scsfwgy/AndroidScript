import com.matt.script.core.KeyConvertCore

fun main(args: Array<String>) {
    /**
     * 把项目中语言配置文件string.xml文件抽取成Excel
     */
    //ExcelCore.androidLbkXml2Excel("/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res", "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel")
    /**
     * 将Excel回写成string.xml
     */
//    XmlCore.androidLbkExcel2StringXml(
//        "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res",
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/多语言自动化抽取转Excel_2022-03-14_16-44-32.xlsx"
//    )

    /**
     * 自动扫描出代码中文案并生成strings.xml
     */
    //Code2StringXmlCore.lbkAndroidDemo()

    /**
     * 项目中旧key替换成新key
     */
    KeyConvertCore.oldKey2NewKey(
        listOf(
            "/Users/matt.wang/AsProject/Android-LBK/app/src/main",
            "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main"
        ),
        "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res",
        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/多语言自动化抽取转Excel_2022-03-14_16-44-32.xlsx",
        false
    )

    /**
     * 将语言配置文件导入到Excel
     * 参数：语言所在文件夹：/Users/matt.wang/iOSProjects/iOS-LBK/LBankApp/Sources/language
     */
//    ExcelCore.iOSLbkRDLocalizable2Excel(
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language",
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/RDLocalizable2Excel"
//    )

    /**
     * 将Excel导出为语言配置文件
     * 参数：语言所在文件夹：/Users/matt.wang/iOSProjects/iOS-LBK/LBankApp/Sources/language
     */
//    XmlCore.iosLbkExcel2StringXml(
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language/temp",
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/RDLocalizable2Excel/iOS多语言自动化抽取转Excel_2022-03-11_15-05-36.xlsx"
//    )
}