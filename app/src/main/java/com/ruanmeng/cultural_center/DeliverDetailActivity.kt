package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CollectMessageEvent
import com.ruanmeng.model.DeliverModel
import com.ruanmeng.share.BaseHttp
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import kotlinx.android.synthetic.main.activity_deliver_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class DeliverDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliver_detail)
        init_title(intent.getStringExtra("title"))

        getData()
        getCollectData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        deliver_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        deliver_video.thumbImageView.setImageResource(R.mipmap.not_4)

        deliver_detail_collect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return@setOnTouchListener true
                } else {
                    if (!deliver_detail_collect.isChecked) {
                        OkGo.post<String>(BaseHttp.collect_business)
                                .tag(this@DeliverDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("heritageId"))
                                .params("type", "2")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("收藏成功！")
                                        deliver_detail_collect.isChecked = true

                                        EventBus.getDefault().post(CollectMessageEvent("非遗"))
                                    }

                                })
                    } else {
                        OkGo.post<String>(BaseHttp.cancel_collect_sub)
                                .tag(this@DeliverDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("heritageId"))
                                .params("type", "2")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("取消成功！")
                                        deliver_detail_collect.isChecked = false

                                        EventBus.getDefault().post(CollectMessageEvent("非遗"))
                                    }

                                })
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun getCollectData() {
        if (getBoolean("isLogin")) {
            OkGo.post<String>(BaseHttp.is_collected)
                    .tag(this@DeliverDetailActivity)
                    .headers("token", getString("token"))
                    .params("busId", intent.getStringExtra("heritageId"))
                    .execute(object : StringDialogCallback(baseContext, false) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            val obj = JSONObject(response.body()).getBoolean("collected")

                            deliver_detail_collect.isChecked = obj
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<DeliverModel>(BaseHttp.heritage_detail)
                .tag(this@DeliverDetailActivity)
                .params("heritageId", intent.getStringExtra("heritageId"))
                .execute(object : JacksonDialogCallback<DeliverModel>(baseContext, DeliverModel::class.java, true) {

                    override fun onSuccess(response: Response<DeliverModel>) {
                        if (response.body().heritageDetail != null) {

                            val data = response.body().heritageDetail!!

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"

                            val info = data.content
                            deliver_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")

                            if (data.videos == "") {
                                deliver_banner.visibility = View.VISIBLE
                                deliver_video.visibility = View.GONE

                                val mLoopAdapter = LoopAdapter(baseContext, deliver_banner)
                                deliver_banner.apply {
                                    setAdapter(mLoopAdapter)
                                    setOnItemClickListener {  }
                                }
                                val imgs = data.imgs.split(",")
                                mLoopAdapter.setImgs(imgs)
                                if (imgs.size <= 1) {
                                    deliver_banner.pause()
                                    deliver_banner.setHintViewVisibility(false)
                                } else {
                                    deliver_banner.resume()
                                    deliver_banner.setHintViewVisibility(true)
                                }

                            } else {
                                deliver_banner.visibility = View.GONE
                                deliver_video.visibility = View.VISIBLE

                                deliver_video.setUp(BaseHttp.baseImg + data.videos, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")

                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.heritageHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(deliver_video.thumbImageView)
                            }
                        }
                    }

                })
    }

    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) return
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }
}
