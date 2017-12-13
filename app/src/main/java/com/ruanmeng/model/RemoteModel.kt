package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:03
 */
data class RemoteModel(
        var bigTypeList: List<RemoteData>? = ArrayList(),
        var smallTypeList: List<RemoteData>? = ArrayList(),
        var courseList: List<RemoteData>? = ArrayList(),

        var course: RemoteData? = null,

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable