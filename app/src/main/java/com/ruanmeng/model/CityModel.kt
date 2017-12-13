package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-02 10:47
 */
data class CityModel(
        var areas: List<CityData>? = ArrayList(),
        var rows: List<CityData>? = ArrayList(),

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable