package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-02 10:47
 */
data class CityData(
        var areaCode: String = "",
        var areaId: String = "",
        var areaName: String = ""
) : Serializable