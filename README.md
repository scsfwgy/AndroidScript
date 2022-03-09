## AndroidScript

安卓自动化脚本，辅助开发者处理国际化问题。

#### 功能
* 支持代码中（Java、Kotlin、xml）扫描并自动生成（添加）到strings.xml中。可扩展为支持iOS文案扫描。
* 支持string.xml转换为Excel文件
* 支持Excel转换为各个语言string.xml
* 支持全局key替换
* 支持通用字符串替换，高度定制化
* 支持扫描并删除无用Drawable
* 支持扫描并删除无用strings.xml的语言配置

#### 常用正则
* ``<string\s*name="(.*)"\s*>((?!</string>)[\s\S\n])*</string>`` 匹配strings.xml中的单行文案。例如：``<string name="key1">value1</string>`` 
* ``(?<=<string\s{0,10000000}name=")((?!")(.))*(?="\s*>)`` 匹配strings.xml中的单行文案中的key。例如：``<string name="key1">value1</string>``中的 `key1`
* ``(?<=name=")[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 匹配strings.xml中的单行文案中的key。例如：``<string name="key1">value1</string>``中的 `key1`
* ``(?<=<string\s{0,10000000}name="(.{0,10000000})"\s{0,10000000}>)((?!</string>)[\s\S\n])*(?=</string>)`` 匹配strings.xml中的单行文案中的value。例如：``<string name="key1">value1</string>``中的 `value1`
* ``[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 字符串key的规则，需要配合其它前缀使用
* ``(?<=R.string\.)[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 匹配Java或者Kotlin中字符串key。例如：``R.string.L0001名字``中的 `L0001名字`
* ``(?<=R.drawable\.)[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 匹配Java或者Kotlin中Drawable资源文件的key。例如：``R.drawable.L0001名字``中的 `L0001名字`
* ``(?<=@string/)[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 匹配xml中字符串key。例如：``android:text="@string/L0001名字"``中的 `L0001名字`
* ``(?<=@drawable/)[a-zA-Z0-9_.\u4e00-\u9fa5]+`` 匹配xml中Drawable资源文件的key。例如：``android:src="@drawable/L0001名字"``中的 `L0001名字`
* ``"(.(?!"))*[\u4e00-\u9fa5]+((?!").)*"`` 匹配带双引号且包含中文的字符串，会包含前后双引号。例如：``xxx"xxx你好xxx"生生世世`` 中的 `"xxx你好xxx"`
* ``(?<=")(.(?!"))*[\u4e00-\u9fa5]+((?!").)*(?=")`` 匹配带双引号且包含中文的字符串，不会包含前后双引号。例如：``xxx"xxx你好xxx"生生世世`` 中的 `xxx你好xxx`
* ``(?<=android:text=")((?!@string/).*)(?=")`` 匹配xml中文案的字符串（需要抽取的文案,不管是否包含中文）。例如：``android:text="xxx"``中的 `xxx`
* ``(?<=android:text=")((?!@string/).)*[\u4e00-\u9fa5]+((?!").)*(?=")`` 匹配xml中包含中文文案的字符串（需要抽取的文案）。例如：``android:text="xxx你好xxx"``中的 `xxx你好xxx`
* ``import(.)*\.R;*\n`` 匹配Java或者Kotlin中的import R资源。例如：``import com.demo.app.R``、``import com.demo.app.R;``
* ``(?<=RDLocalizedString\(@")[a-zA-Z0-9\u4e00-\u9fa5_.\\、。"]+(?="\))`` 匹配iOS代码中的key。例如：``RDLocalizedString(@"key1")``中的 `key1`
* ``(?!%s)%`` 匹配包含%且不包含%s。例如：``%snn%``中不匹配第一个%，但是匹配到第二个%

#### 参考
* [https://github.com/cdoco/learn-regex-zh](https://github.com/cdoco/learn-regex-zh)
* [https://github.com/Blankj/AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)