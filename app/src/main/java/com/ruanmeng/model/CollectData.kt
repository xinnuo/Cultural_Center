package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:02
 */
data class CollectData(
        var heritageHead: String = "",
        var heritageId: String = "",
        var heritageTitle: String = "",
        var content: String = "",
        var videos: String = "",
        var heritagelevelId: String = "",
        var heritagelevelName: String = "",
        var heritageTypeId: String = "",
        var heritageTypeName: String = "",
        var businessKey: String = ""
) : Serializable