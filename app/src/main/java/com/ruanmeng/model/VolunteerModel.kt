package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-04 15:03
 */
data class VolunteerModel(
        var volunteerSliderList: List<CommonData>? = ArrayList(),
        var volunteerList: List<CommonData>? = ArrayList(),
        var volunteerActivityList: List<VolunteerData>? = ArrayList(),
        var activity: List<VolunteerData>? = ArrayList(),

        var volunteerDetail: VolunteerData? = null,

        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable