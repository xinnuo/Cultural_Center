package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 10:30
 */
data class NewsModel(
        var newsTypeList: List<CommonData>? = ArrayList(),
        var newsList: List<CommonData>? = ArrayList(),
        var newsDetail: CommonData? = null,

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable