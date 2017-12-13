package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.model.OrganModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_office_detail.*

class OfficeDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_office_detail)
        init_title(intent.getStringExtra("title"))

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        office_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
    }

    override fun getData() {
        OkGo.post<OrganModel>(BaseHttp.staff_detail)
                .tag(this@OfficeDetailActivity)
                .params("staffId", intent.getStringExtra("staffId"))
                .execute(object : JacksonDialogCallback<OrganModel>(baseContext, OrganModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<OrganModel>) {
                        if (response.body().staffDetail != null) {

                            val data = response.body().staffDetail!!
                            office_detail_name.text = data.staffName
                            office_detail_job.text = data.duty
                            office_detail_label.text = data.honor

                            GlideApp.with(baseContext)
                                    .load(BaseHttp.baseImg + data.stuffHead)
                                    .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                    .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                    .dontAnimate()
                                    .into(office_detail_img)

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"

                            val info = data.dutyDescribe
                            office_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }
                    }

                })
    }
}
