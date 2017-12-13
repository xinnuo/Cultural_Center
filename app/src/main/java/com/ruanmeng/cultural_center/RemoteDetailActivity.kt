package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.model.RemoteModel
import com.ruanmeng.share.BaseHttp
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import kotlinx.android.synthetic.main.activity_remote_detail.*

class RemoteDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_detail)
        init_title("课程详情")

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        remote_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        remote_video.thumbImageView.setImageResource(R.mipmap.not_4)
    }

    override fun getData() {
        OkGo.post<RemoteModel>(BaseHttp.course_detail)
                .tag(this@RemoteDetailActivity)
                .params("courseId", intent.getStringExtra("courseId"))
                .execute(object : JacksonDialogCallback<RemoteModel>(baseContext, RemoteModel::class.java, true) {

                    override fun onSuccess(response: Response<RemoteModel>) {
                        if (response.body().course != null) {

                            val data = response.body().course!!

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"

                            val info = data.courseIntroduce
                            remote_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")

                            remote_video.setUp(BaseHttp.baseImg + data.video, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "")

                            GlideApp.with(baseContext)
                                    .load(BaseHttp.baseImg + data.courseHead)
                                    .placeholder(R.mipmap.not_4) // 等待时的图片
                                    .error(R.mipmap.not_4)       // 加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(remote_video.thumbImageView)
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
