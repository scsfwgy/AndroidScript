package com.matt.script.net

import com.matt.script.config.LogWrapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 更新时间 ：2019/06/20 15:02
 * 描 述 ：
 * ============================================================
 */
open class DefOkHttpClient private constructor() {

    /**
     * common api
     *
     * @return
     */
    fun defOkHttpClient(ws: Boolean = false): OkHttpClient.Builder {
        return defOkHttpClientBuilder(ws)
    }

    /**
     * 构造一个通用的builder
     *
     * @return
     */
    //给后端传递head参数
    fun defOkHttpClientBuilder(ws: Boolean = false): OkHttpClient.Builder {
        /**
         * 全局日志控制
         */
        //系统默认的日志系统，打印不没关，但是便于复制。
        val defLoggingInterceptor =
            HttpLoggingInterceptor { message -> LogWrapper.loggerWrapper(TAG).debug(message) }
        //https://github.com/square/okhttp/issues/2363
        defLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val addInterceptor = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_CONN, TimeUnit.SECONDS)
            .readTimeout(READ_CONN, TimeUnit.SECONDS)
            .writeTimeout(WRITE_CONN, TimeUnit.SECONDS)
            //.addInterceptor(HeaderInterceptor())
        proxyDisEnable(addInterceptor)

        /**
         * 只有非ws才添加下面参数
         */
        if (!ws) {
            //API允许重试
//            addInterceptor.retryOnConnectionFailure(GlobalConfig.isDebug())
//            addInterceptor.addInterceptor(UrlSelectInterceptor())
                //日志系统二选一：是要格式化的还是要原始格式数据
                //.addInterceptor(jsonLoggingInterceptor)
            addInterceptor.addInterceptor(defLoggingInterceptor)
        } else {
            //ws不允许重试
            addInterceptor.retryOnConnectionFailure(false)
        }
        return addInterceptor
    }

    companion object {
        val TAG = "DefOkHttpClient"
        val TIMEOUT_CONN: Long = 15
        val READ_CONN: Long = 15
        val WRITE_CONN: Long = 15

        private val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DefOkHttpClient()
        }

        val mDefaultApiOkHttpClient by lazy {
            instance.defOkHttpClient(false)
        }

        val mDefaultWsOkHttpClient by lazy {
            instance.defOkHttpClient(true)
        }

        fun proxyDisEnable(httpClientBuilder: OkHttpClient.Builder): OkHttpClient.Builder? {
            if (true) return null
            return httpClientBuilder.proxy(Proxy.NO_PROXY)
        }
    }
}