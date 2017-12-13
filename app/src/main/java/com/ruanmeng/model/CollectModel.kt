package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:03
 */
data class CollectModel(
        var newsList: List<CommonData>? = ArrayList(),
        var heritageList: List<CollectData>? = ArrayList(),
        var onlineartList: List<OnlineData>? = ArrayList(),

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable