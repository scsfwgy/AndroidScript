package com.matt.script.model.lark

data class ApiLarkBase<T>(val msg_type: LarkMsgType, val content: T)