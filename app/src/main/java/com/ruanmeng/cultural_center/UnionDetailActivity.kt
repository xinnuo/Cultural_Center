package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CultureModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_union_detail.*

class UnionDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_union_detail)
        init_title(intent.getStringExtra("title"))

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        union_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.union_detail_setting -> {
                intent.setClass(baseContext, OrganActivity::class.java)
                startActivity(intent)
            }
            R.id.union_detail_notice -> {
                intent.setClass(baseContext, NoticeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun getData() {
        OkGo.post<CultureModel>(BaseHttp.cultural_detil)
                .tag(this@UnionDetailActivity)
                .params("culturalId", intent.getStringExtra("culturalId"))
                .execute(object : JacksonDialogCallback<CultureModel>(baseContext, CultureModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<CultureModel>) {
                        if (response.body().culturalDetil != null) {

                            val data = response.body().culturalDetil!!

                            val mLoopAdapter = LoopAdapter(baseContext, union_detail_banner)
                            union_detail_banner.apply {
                                setAdapter(mLoopAdapter)
                                setOnItemClickListener { position ->
                                    //轮播图点击事件
                                }
                            }

                            val imgs = data.imgs.split(",")
                            mLoopAdapter.setImgs(imgs)
                            if (imgs.size <= 1) {
                                union_detail_banner.pause()
                                union_detail_banner.setHintViewVisibility(false)
                            } else {
                                union_detail_banner.resume()
                                union_detail_banner.setHintViewVisibility(true)
                            }

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"

                            val info = data.culturalIntroduce
                            union_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }
                    }

                })
    }
}
