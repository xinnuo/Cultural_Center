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
data class CommonData(
        //场馆列表、详情
        var address: String = "",
        var cityName: String = "",
        var districtName: String = "",
        var provinceName: String = "",
        var readCount: String = "",
        var venueHead: String = "",
        var venueId: String = "",
        var grade: String = "",
        var venueName: String = "",
        var introduce: String = "",

        //活动栏目
        var programaId: String = "",
        var programaName: String = "",

        //我的场地预约
        var reserveEndDate: String = "",
        var reserveStartDate: String = "",

        //展厅列表
        var hallId: String = "",
        var hallName: String = "",

        //志愿者列表
        var name: String = "",
        var volunteerId: String = "",
        var volunteerHead: String = "",

        //文化馆联盟
        var culturalId: String = "",
        var culturalName: String = "",
        var imgs: String = "",
        var culturalIntroduce: String = "",

        //非遗传承列表
        var heritageHead: String = "",
        var heritageId: String = "",
        var heritageTitle: String = "",
        var content: String = "",
        var videos: String = "",
        var heritagelevelId: String = "",
        var heritagelevelName: String = "",
        var heritageTypeId: String = "",
        var heritageTypeName: String = "",

        //文化专题
        var cultureHead: String = "",
        var cultureId: String = "",
        var cultureTitle: String = "",
        var culturevideoId: String = "",
        var culturevideoHead: String = "",
        var culturevideoName: String = "",
        var video: String = "",
        var hostUnit: String = "",
        var cultureimgId: String = "",

        //首页新闻、文化馆新闻、新闻类型
        var contentIntro: String = "",
        var createDate: String = "",
        var newsHead: String = "",
        var newsId: String = "",
        var newsTitle: String = "",
        var newsTypeId: String = "",
        var newTypeName: String = "",
        var businessKey: String = "",

        //文化活动
        var activityEndDate: String = "",
        var activityHead: String = "",
        var activityId: String = "",
        var activityStartDate: String = "",
        var activityTitle: String = "",
        var activityContent: String = "",
        var signUpLimit: String = "",
        var peopleCount: String = "",
        var stype: String = "",
        var stypeId: String = "",

        //地区筛选
        var areaId: String = "",
        var areaName: String = "",

        //首页轮播图
        var href: String = "",
        var sliderId: String = "",
        var sliderImg: String = "",
        var title: String = ""
) : Serializable