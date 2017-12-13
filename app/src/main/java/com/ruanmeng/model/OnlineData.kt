package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:02
 */
data class OnlineData(
        var onlineArtHead: String = "",
        var onlineArtId: String = "",
        var title: String = "",
        var imgs: String = "",
        var sum: String = "",

        var onlineHallId: String = "",
        var onlineHallName: String = "",
        var onlineArtTypeId: String = "",
        var businessKey: String = "",
        var onlineArtTypeName: String = ""
) : Serializable