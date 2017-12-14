/**
 * created by 小卷毛, 2017/11/02
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-02 10:47
 */
data class CommonModel(
        var news: List<CommonData>? = ArrayList(),
        var slider: List<CommonData>? = ArrayList(),

        var venue: List<CommonData>? = ArrayList(),
        var hallList: List<CommonData>? = ArrayList(),
        var reserveVenueList: List<CommonData>? = ArrayList(),

        var las: List<CommonData>? = ArrayList(),
        var activity: List<CommonData>? = ArrayList(),

        var programa: List<CommonData>? = ArrayList(),

        var cultural: List<CommonData>? = ArrayList(),

        var relatedNews: List<CommonData>? = ArrayList(),

        var barList: List<CommonData>? = ArrayList(),

        var heritageList: List<CommonData>? = ArrayList(),
        var heritagetype: List<CommonData>? = ArrayList(),
        var heritagelevel: List<CommonData>? = ArrayList(),

        var maincity: String = "",
        var maincityId: String = "",
        var msgcode: String = "",
        var msg: String = "",
        var success: String = ""
) : Serializable