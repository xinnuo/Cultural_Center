package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CollectMessageEvent
import com.ruanmeng.model.NewsModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_notice_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class NoticeDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)
        init_title("新闻详情")

        getData()
        getCollectData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        notice_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        notice_detail_collect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return@setOnTouchListener true
                } else {
                    if (!notice_detail_collect.isChecked) {
                        OkGo.post<String>(BaseHttp.collect_business)
                                .tag(this@NoticeDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("newsId"))
                                .params("type", "1")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("收藏成功！")
                                        notice_detail_collect.isChecked = true

                                        EventBus.getDefault().post(CollectMessageEvent("新闻文章"))
                                    }

                                })
                    } else {
                        OkGo.post<String>(BaseHttp.cancel_collect_sub)
                                .tag(this@NoticeDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("newsId"))
                                .params("type", "1")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("取消成功！")
                                        notice_detail_collect.isChecked = false

                                        EventBus.getDefault().post(CollectMessageEvent("新闻文章"))
                                    }

                                })
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.iv_nav_right -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_detail_share, null) as View
                val ll_wechat = view.findViewById(R.id.ll_dialog_share_wechat) as LinearLayout
                val ll_circle = view.findViewById(R.id.ll_dialog_share_circle) as LinearLayout
                val ll_qq = view.findViewById(R.id.ll_dialog_share_qq) as LinearLayout
                val ll_zone = view.findViewById(R.id.ll_dialog_share_zone) as LinearLayout
                val tv_cancel = view.findViewById(R.id.btn_dialog_share_cancel) as TextView

                val dialog = BottomSheetDialog(baseContext)
                dialog.setContentView(view)
                dialog.show()

                ll_wechat.setOnClickListener {
                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                ll_circle.setOnClickListener {
                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                ll_qq.setOnClickListener {
                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                ll_zone.setOnClickListener {
                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                tv_cancel.setOnClickListener { dialog.dismiss() }
            }
        }
    }

    private fun getCollectData() {
        if (getBoolean("isLogin")) {
            OkGo.post<String>(BaseHttp.is_collected)
                    .tag(this@NoticeDetailActivity)
                    .headers("token", getString("token"))
                    .params("busId", intent.getStringExtra("newsId"))
                    .execute(object : StringDialogCallback(baseContext, false) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            val obj = JSONObject(response.body()).getBoolean("collected")

                            notice_detail_collect.isChecked = obj
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<NewsModel>(BaseHttp.news_detail)
                .tag(this@NoticeDetailActivity)
                .params("newsId", intent.getStringExtra("newsId"))
                .execute(object : JacksonDialogCallback<NewsModel>(baseContext, NewsModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<NewsModel>) {
                        if (response.body().newsDetail != null) {

                            val data = response.body().newsDetail!!

                            notice_detail_title.text = data.newsTitle
                            notice_detail_time.text = data.createDate

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
                            notice_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }
                    }

                })
    }
}
