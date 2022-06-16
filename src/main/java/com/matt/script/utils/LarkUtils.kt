package com.matt.script.utils

import com.matt.script.config.LogWrapper
import com.matt.script.model.lark.ApiLarkBase
import com.matt.script.model.lark.ApiLarkText
import com.matt.script.model.lark.LarkMsgType
import com.matt.script.net.DefOkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.function.Consumer

fun main() {
    LarkUtils.autoSendMsgText("来自自动化测试消息") {
        LogWrapper.loggerWrapper("this").debug(it)
    }
}

//https://open.larksuite.com/open-apis/bot/v2/hook/
object LarkUtils {

    val defOkhttp by lazy {
        DefOkHttpClient.mDefaultApiOkHttpClient.build()
    }

    val larkKey by lazy {
        val file = File("/Users/matt.wang/IdeaProjects/AndroidScript/BackUpFiles/config/larkkey.txt")
        file.readText().trim()
    }

    fun autoSendMsgText(msg: String, consumer: Consumer<Pair<Boolean, String>>? = null) {
        val apiLarkBase = ApiLarkBase(LarkMsgType.text, ApiLarkText(msg))
        baseAutoBot(apiLarkBase, consumer)
    }

//    fun autoSenMsgPost(){
//        val apiLarkBase = ApiLarkBase(LarkMsgType.text, ApiLarkText(msg))
//        baseAutoBot(apiLarkBase, consumer)
//    }


    /**
     * 发送自定义消息
     */
    fun <T> baseAutoBot(apiLarkBase: ApiLarkBase<T>, consumer: Consumer<Pair<Boolean, String>>? = null) {
        val postUrl = "https://open.larksuite.com/open-apis/bot/v2/hook/$larkKey"
        val request = Request.Builder().addHeader("Content-Type", "application/json")
            .url(postUrl)
            .post(apiLarkBase.any2RequestBody()).build()
        defOkhttp.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    consumer?.accept(Pair(false, e.message ?: ""))
                }

                override fun onResponse(call: Call, response: Response) {
                    val msg = response.body?.string() ?: ""
                    LogWrapper.loggerWrapper(this).debug(msg)
                    consumer?.accept(Pair(true, msg))
                }

            })
    }
}