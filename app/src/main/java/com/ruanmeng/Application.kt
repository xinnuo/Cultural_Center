/**
 * created by 小卷毛, 2017/10/12
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
package com.ruanmeng

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.DBCookieStore
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.cultural_center.BuildConfig
import com.ruanmeng.service.LoginCheckService
import com.umeng.socialize.Config
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-10-12 17:43
 */
class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        initOkGo()

        //友盟分享
        PlatformConfig.setWeixin("", "")
        PlatformConfig.setQQZone("", "")
        PlatformConfig.setSinaWeibo("", "", "")
        UMShareAPI.get(this@Application)
        Config.DEBUG = BuildConfig.LOG_DEBUG
        Config.isJumptoAppStore = true

        // 绑定服务
        bindService(Intent(this, LoginCheckService::class.java), object : ServiceConnection {
            // 当与service的连接建立后被调用
            override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
                if (iBinder is LoginCheckService.TimeStampBinder) {
                    val mTimeStampTimer = iBinder.timeStampTimer
                    if (mTimeStampTimer == null) {
                        iBinder.service.setTimeStampTimer()
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {}

        }, Context.BIND_AUTO_CREATE)
    }

    private fun initOkGo() {
        val builder = OkHttpClient.Builder()

        OkLogger.debug("Cultural_Center", BuildConfig.LOG_DEBUG)

        //log相关
        val loggingInterceptor = HttpLoggingInterceptor("OkGo")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO)                        //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor)                          //添加OkGo默认debug日志

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)    //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)   //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS) //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        builder.cookieJar(CookieJarImpl(DBCookieStore(this))) //使用数据库保持cookie，如果cookie不过期，则一直有效

        //https相关设置，以下几种方案根据需要自己设置：信任所有证书,不安全有风险
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)

        // 其他统一的配置
        OkGo.getInstance().init(this@Application)              //必须调用初始化
                .setOkHttpClient(builder.build())              //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)              //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)  //全局统一缓存时间，默认永不过期，可以不传
                .retryCount = 3                                //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }
}