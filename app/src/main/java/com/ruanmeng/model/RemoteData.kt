package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:02
 */
data class RemoteData(
        var courseTypeId: String = "",
        var courseTypeName: String = "",
        var isChecked: Boolean = false,

        var courseHead: String = "",
        var courseId: String = "",
        var courseTitle: String = "",
        var courseIntroduce: String = "",
        var video: String = ""
) : Serializable