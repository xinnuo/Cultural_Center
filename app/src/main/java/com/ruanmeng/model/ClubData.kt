package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-02 10:47
 */
data class ClubData(
        var clubHead: String = "",
        var clubId: String = "",
        var clubTitle: String = "",
        var introduce: String = "",

        var clubMemberHead: String = "",
        var clubMemberId: String = "",
        var clubMemberName: String = "",
        var duty: String = "",
        var honor: String = ""
) : Serializable