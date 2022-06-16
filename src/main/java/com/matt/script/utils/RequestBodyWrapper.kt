package com.matt.script.utils

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2022/5/25 11:19
 * 描 述 ：
 * ============================================================
 **/
fun Any.any2RequestBody(): RequestBody {
    return (GsonUtils.toJson(this)
        ?: "").toRequestBody("application/json; charset=utf-8".toMediaType())
}