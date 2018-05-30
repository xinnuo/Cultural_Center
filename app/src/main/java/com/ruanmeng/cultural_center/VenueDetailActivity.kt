package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.MainMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_venue_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class VenueDetailActivity : BaseActivity() {

    lateinit var mLoopAdapter: LoopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue_detail)
        init_title(intent.getStringExtra("title"))

        getData()
    }

    override fun onStart() {
        super.onStart()
        venue_detail_rating.isTouchable = getBoolean("isLogin")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        mLoopAdapter = LoopAdapter(baseContext, venue_detail_banner)
        venue_detail_banner.setAdapter(mLoopAdapter)
        venue_detail_banner.setOnItemClickListener {  }

        venue_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        venue_detail_rating.setOnRatingChangeListener { _, rating ->
            window.decorView.postDelayed({
                OkGo.post<String>(BaseHttp.user_insertGrade)
                        .tag(this@VenueDetailActivity)
                        .headers("token", getString("token"))
                        .params("busskey", intent.getStringExtra("venueId"))
                        .params("type", "1")
                        .params("grade", rating)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast("评分成功！")
                                EventBus.getDefault().post(MainMessageEvent("场馆评分"))
                            }

                        })
            }, 300)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.venue_detail_book -> {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return
                }
                intent.setClass(baseContext, BookActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.venue_details)
                .tag(this@VenueDetailActivity)
                .params("venueId", intent.getStringExtra("venueId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body()).getJSONObject("venueDetails")

                        venue_detail_title.text = obj.optString("venueName")
                        venue_detail_scan.text = "浏览（${obj.optString("readCount", "0")}）"
                        venue_detail_addr.text = "地址：" + obj.optString("venueName")

                        val imgs = obj.optString("venueHead").split(",")
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size <= 1) {
                            venue_detail_banner.pause()
                            venue_detail_banner.setHintViewVisibility(false)
                        } else {
                            venue_detail_banner.resume()
                            venue_detail_banner.setHintViewVisibility(true)
                        }

                        val str = "<meta name=\"viewport\" " +
                                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\" />\n" +
                                "<style>\n" +
                                ".con{ width:100%; margin:0 auto; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ max-width: 100% !important; display:block; height:auto !important; }\n" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>"

                        val info = obj.optString("introduce")
                        venue_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                    }

                })
    }
}
