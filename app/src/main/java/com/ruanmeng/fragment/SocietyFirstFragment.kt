package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.cultural_center.R
import com.ruanmeng.model.ClubModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_special_first.*

class SocietyFirstFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_special_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        special_web.apply {
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
        OkGo.post<ClubModel>(BaseHttp.club_introduce)
                .tag(this@SocietyFirstFragment)
                .params("clubId", arguments!!.getString("clubId"))
                .execute(object : JacksonDialogCallback<ClubModel>(activity, ClubModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<ClubModel>) {
                        if (response.body().club != null) {

                            val data = response.body().club!!

                            val mLoopAdapter = LoopAdapter(activity, special_banner)
                            special_banner.apply {
                                setAdapter(mLoopAdapter)
                                setOnItemClickListener { position ->
                                    //轮播图点击事件
                                }
                            }
                            val imgs = data.clubHead.split(",")
                            mLoopAdapter.setImgs(imgs)
                            if (imgs.size <= 1) {
                                special_banner.pause()
                                special_banner.setHintViewVisibility(false)
                            } else {
                                special_banner.resume()
                                special_banner.setHintViewVisibility(true)
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

                            val info = data.introduce
                            special_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }
                    }

                })
    }
}