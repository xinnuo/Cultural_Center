package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:03
 */
data class OnlineModel(
        var onlineHallList: List<OnlineData>? = ArrayList(),
        var onlineHallArtTypeList: List<OnlineData>? = ArrayList(),
        var artList: List<OnlineData>? = ArrayList(),
        var onlineartList: List<OnlineData>? = ArrayList(),

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable