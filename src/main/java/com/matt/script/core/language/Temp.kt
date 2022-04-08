package com.matt.script.core.language

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2022/4/8 14:46
 * 描 述 ：
 * ============================================================
 */
fun main() {
    Temp.ss()
}

class Temp {
    var order: String? = null
    var all: String? = null
    var 中文名称: String? = null
    var 对齐方式: String? = null
    var 英文名称: String? = null
    var 显示名称: String? = null
    var web: String? = null
    var android: String? = null
    var newhome: String? = null

    companion object {
        fun ss() {
            val json = """
                [
                {" order":"","all":"key","中文名称":"KEY","对齐方式":"/","英文名称":"/","显示名称":"/","web":"KEY","android":"key(不要动这一列内容)","newhome":"key"},
                {" order":1,"all":"en-US","中文名称":"英文-美国","对齐方式":"左对齐","英文名称":"English","显示名称":"English","web":"en-US","android":"values-en(英文)","newhome":"en_US"},
                {" order":2,"all":"ru","中文名称":"俄语","对齐方式":"左对齐","英文名称":"Russian","显示名称":"Pусский","web":"ru-RU","android":"values-ru-rRU(俄语)","newhome":"ru_RU"},
                {" order":3,"all":"es","中文名称":"西班牙语","对齐方式":"左对齐","英文名称":"Spanish","显示名称":"Español","web":"es-ES","android":"values-es-rES(西班牙文)","newhome":"es_ES"},
                {" order":4,"all":"pt","中文名称":"葡萄牙语","对齐方式":"左对齐","英文名称":"Portuguese","显示名称":"Português","web":"pt-PT","android":"values-pt-rPT(葡萄牙文)","newhome":"pt_PT"},
                {" order":5,"all":"tr","中文名称":"土耳其语","对齐方式":"左对齐","英文名称":"Turkish","显示名称":"Türk","web":"tr-TR","android":"values-tr-rTR(土耳其文)","newhome":"tr_TR"},
                {" order":6,"all":"fr","中文名称":"法语","对齐方式":"左对齐","英文名称":"French","显示名称":"Français","web":"----","android":"----","newhome":"----"},
                {" order":7,"all":"de","中文名称":"德语","对齐方式":"左对齐","英文名称":"German","显示名称":"Deutsch","web":"德语","android":"values-de(德语)","newhome":"----"},
                {" order":8,"all":"it","中文名称":"意大利语","对齐方式":"左对齐","英文名称":"Italian","显示名称":"Italiano","web":"意大利语","android":"values-it(意大利语)","newhome":"----"},
                {" order":9,"all":"pl","中文名称":"波兰语","对齐方式":"左对齐","英文名称":"Polish","显示名称":"Polski","web":"----","android":"----","newhome":"----"},
                {" order":10,"all":"id","中文名称":"印尼语","对齐方式":"左对齐","英文名称":"Indonesian","显示名称":"Bahasa Indonesia","web":"id-ID","android":"values-kn(印尼文)","newhome":"in_ID"},
                {" order":11,"all":"fil","中文名称":"菲律宾语","对齐方式":"左对齐","英文名称":"Filipino","显示名称":"Filipino","web":"----","android":"----","newhome":"----"},
                {" order":12,"all":"vi-VN","中文名称":"越南语-越南","对齐方式":"左对齐","英文名称":"Vietnamese","显示名称":"Tiếng Việt","web":"vi-VN","android":"values-vi(越南文)","newhome":"vi_VN"},
                {" order":13,"all":"th","中文名称":"泰语","对齐方式":"左对齐","英文名称":"Thai","显示名称":"ไทย","web":"----","android":"----","newhome":"----"},
                {" order":14,"all":"hi","中文名称":"印地语","对齐方式":"左对齐","英文名称":"Hindi","显示名称":"Hindi","web":"印地语","android":"values-hi(印地语)","newhome":"----"},
                {" order":15,"all":"ro","中文名称":"罗马尼亚语","对齐方式":"左对齐","英文名称":"Romanian","显示名称":"Română","web":"----","android":"----","newhome":"----"},
                {" order":16,"all":"fa","中文名称":"波斯语","对齐方式":"右对齐","英文名称":"Persian","显示名称":"اللغة الفارسية","web":"波斯语","android":"values-fa(波斯语)","newhome":"----"},
                {" order":17,"all":"ar","中文名称":"阿拉伯语","对齐方式":"右对齐","英文名称":"Arabic","显示名称":"العربية","web":"阿拉伯语","android":"values-ar(阿拉伯语)","newhome":"----"},
                {" order":18,"all":"he","中文名称":"希伯来语","对齐方式":"右对齐","英文名称":"Hebrew","显示名称":"עִברִית","web":"----","android":"----","newhome":"----"},
                {" order":19,"all":"ur","中文名称":"乌尔都语","对齐方式":"右对齐","英文名称":"Urdu","显示名称":"اردو","web":"----","android":"----","newhome":"----"},
                {" order":20,"all":"sv","中文名称":"瑞典语","对齐方式":"左对齐","英文名称":"Swedish","显示名称":"Svenska","web":"----","android":"----","newhome":"----"},
                {" order":21,"all":"sl","中文名称":"斯洛文尼亚语","对齐方式":"左对齐","英文名称":"Slovenian","显示名称":"Slovenščina","web":"----","android":"----","newhome":"----"},
                {" order":22,"all":"sk","中文名称":"斯洛伐克语","对齐方式":"左对齐","英文名称":"Slovak","显示名称":"Slovenský","web":"----","android":"----","newhome":"----"},
                {" order":23,"all":"lv","中文名称":"拉脱维亚语","对齐方式":"左对齐","英文名称":"Latvian","显示名称":"Latviešu Valoda","web":"----","android":"----","newhome":"----"},
                {" order":24,"all":"cs","中文名称":"捷克语","对齐方式":"左对齐","英文名称":"Czech","显示名称":"Čeština","web":"----","android":"----","newhome":"----"},
                {" order":25,"all":"el","中文名称":"希腊语","对齐方式":"左对齐","英文名称":"Greek","显示名称":"Ελληνικά","web":"----","android":"----","newhome":"----"},
                {" order":26,"all":"uk","中文名称":"乌克兰语","对齐方式":"左对齐","英文名称":"Ukrainian","显示名称":"Український","web":"----","android":"----","newhome":"----"},
                {" order":27,"all":"bg","中文名称":"保加利亚语","对齐方式":"左对齐","英文名称":"Bulgarian","显示名称":"Български","web":"----","android":"----","newhome":"----"},
                {" order":28,"all":"sq","中文名称":"阿尔巴尼亚语","对齐方式":"左对齐","英文名称":"Albanian","显示名称":"Shqiptare","web":"----","android":"----","newhome":"----"},
                {" order":29,"all":"uz","中文名称":"乌兹别克语","对齐方式":"左对齐","英文名称":"Uzbek","显示名称":"O'Zbek","web":"----","android":"----","newhome":"----"},
                {" order":30,"all":"bn","中文名称":"孟加拉语","对齐方式":"左对齐","英文名称":"Bengali","显示名称":"বাংলা","web":"----","android":"----","newhome":"----"},
                {" order":31,"all":"nl","中文名称":"荷兰语","对齐方式":"左对齐","英文名称":"Dutch","显示名称":"Nederlands","web":"----","android":"----","newhome":"----"},
                {" order":32,"all":"sw","中文名称":"斯瓦西里语","对齐方式":"左对齐","英文名称":"Swahili","显示名称":"Kiswahili","web":"----","android":"----","newhome":"----"},
                {" order":33,"all":"ko-KR","中文名称":"韩语-韩国","对齐方式":"左对齐","英文名称":"Korean","显示名称":"한국어","web":"ko-KR","android":"values-ko(韩文)","newhome":"ko_KR"},
                {" order":34,"all":"ja","中文名称":"日语","对齐方式":"左对齐","英文名称":"Japanese","显示名称":"日本語","web":"----","android":"----","newhome":"----"},
                {" order":35,"all":"zh-CN","中文名称":"中文-中国","对齐方式":"左对齐","英文名称":"Simplified Chinese","显示名称":"简体中文","web":"zh-CN","android":"values(默认语言，中文)","newhome":"zh_CN"},
                {" order":36,"all":"zh-TW","中文名称":"中文-中国台湾","对齐方式":"左对齐","英文名称":"Traditional Chinese","显示名称":"繁體中文","web":"zh-TW","android":"values-zh-rTW(繁体中文)","newhome":"zh_TW"}
                ]
            """.trimIndent()

            val fromJson = Gson().fromJson<List<Temp>>(json, object : TypeToken<List<Temp>>() {}.type)
            val list = ArrayList<LocalLanguage>()
            fromJson.forEachIndexed { index, it ->
                if (index == 0) return@forEachIndexed
                list.add(
                    LocalLanguage(
                        it.all ?: "",
                        it.中文名称 ?: "",
                        it.英文名称 ?: "",
                        it.显示名称 ?: "",
                        it.all ?: "",
                        it.all ?: "",
                        it.all ?: "",
                        it.对齐方式 == "右对齐",
                    )
                )
            }
            println(Gson().toJson(list))

        }
    }
}