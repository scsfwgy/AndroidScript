fun main(args: Array<String>) {
    /**
     * 把项目中语言配置文件string.xml文件抽取成Excel
     */

    //ExcelCore.androidLbkXml2Excel(FileConfig.languageResRootDir(), "BackUpFiles/Xml2Excel")

    /**
     * 将Excel回写成string.xml
     */
//    XmlCore.androidLbkExcel2StringXml(
//        FileConfig.languageResRootDir(),
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/Android2023_01_31_17_34.xlsx"
//    )

    /**
     * 自动扫描出代码中文案并生成strings.xml
     */
    //Code2StringXmlCore.lbkAndroidDemo()

    /**
     * 项目中旧key替换成新key
     */
//    KeyConvertCore.oldKey2NewKey(
//        FileConfig.fullMainRootDir(),
//        FileConfig.languageResRootDir(),
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/Xml2Excel/android-新版合约.xlsx",
//        false
//    )

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


    /**
     * 删除无用的key
     */
//    FindUselessCore.findUselessStringXmlWrapper(
//        FindUselessCore.scanDirList(),
//        "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res/values/strings.xml",
//        "/Users/matt.wang/AsProject/Android-LBK/lib_wrapper/src/main/res",
//        iosType = false,
//        onlyFind = false
//    )

    /**
     * 删除无用的key
     */
//    FindUselessCore.findUselessStringXmlWrapper(
//        listOf("/Users/matt.wang/iOSProjects/iOS-LBK"),
//        "/Users/matt.wang/iOSProjects/iOS-LBK/LBankApp/Sources/language/zh-Hans.lproj/RDLocalizable.strings",
//        "/Users/matt.wang/iOSProjects/iOS-LBK/LBankApp/Sources/language",
//        iosType = true,
//        onlyFind = true
//    )

    /**
     * ios:扫描整个代码库，找到所有不存在在语言配置中的文案列出来，同时列出来已在语言配置中存在的文案（不需要替换）。
     */
//    IosScanCore.findNoKeyListWrapper(
//        listOf("/Users/matt.wang/iOSProjects/iOS-LBK"),
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language/zh-Hans.lproj/RDLocalizable.strings",
//        RegexUtilsWrapper.iosPureKeyRegex2
//    )

    /**
     * ios:用最新的语言配置扫描代码库，替换文案为最新的key
     */
//    IosScanCore.replaceNewValueByKeyWrapper(
//        listOf("/Users/matt.wang/iOSProjects/iOS-LBK"),
//        "/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/ios/language/zh-Hans.lproj/RDLocalizable.strings",
//        RegexUtilsWrapper.iosPureKeyRegex2
//    )


    //ExcelCore.loadDefaultLanguageList()
}