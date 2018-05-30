package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CultureModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_culture_detail.*
import org.json.JSONObject

class CultureDetailActivity : BaseActivity() {

    private var startDate = ""
    private var endDate = ""
    private var signUpLimit = ""
    private var peopleCount = ""
    private var stypeId = ""
    private var externalLink = ""
    private var isVolunteer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_detail)
        isVolunteer = intent.getBooleanExtra("isVolunteer", false)
        stypeId = intent.getStringExtra("stypeId") ?: ""

        init_title(if (isVolunteer) "活动详情" else "文化活动详情")

        if (isVolunteer) getVolunteerData()
        else getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        culture_detail_web.apply {
            settings.javaScriptEnabled = true                     //设置WebView属性，能够执行Javascript脚本
            settings.javaScriptCanOpenWindowsAutomatically = true //自动打开窗口
            settings.loadWithOverviewMode = true                  //设置WebView可以加载更多格式页面
            settings.useWideViewPort = true

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }

        culture_detail_book.visibility = if (intent.getBooleanExtra("isMine", false)) View.GONE else View.VISIBLE

        if (!isVolunteer) {
            when (stypeId) {
                "-1" -> culture_detail_book.text = "活动未开始"
                "0" -> culture_detail_book.text = "活动报名"
                "1" -> culture_detail_book.text = "报名截止"
                "2" -> culture_detail_book.text = "活动进行中"
                "3" -> culture_detail_book.text = "活动结束"
            }
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
            R.id.culture_detail_book -> {
                if (startDate == "" || endDate == "") return

                if (culture_detail_book.text != "活动报名") {

                    if (culture_detail_book.text == "已报名") toast("活动已报名！")
                    else {
                        when (stypeId) {
                            "-1" -> toast("活动报名未开始！")
                            "1", "2" -> toast("活动报名已截止！")
                            "3" -> toast("活动已结束！")
                        }
                    }
                    return
                }

                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return
                }

                // val start = TimeHelper.getInstance().stringToLong(startDate)
                val end = TimeHelper.getInstance().stringToLong(endDate)
                val current = System.currentTimeMillis() / 1000

                /*if (current < start) {
                    toast("该活动尚未开始！")
                    return
                }*/

                if (current > end) {
                    toast("该活动已结束！")
                    return
                }

                if (isVolunteer) {
                    if (!getBoolean("voulunteer")) {
                        toast("您现在还不是志愿者，无法报名！")
                        return
                    }

                    OkGo.post<String>(BaseHttp.volunteer_applyForActivity)
                            .tag(this@CultureDetailActivity)
                            .headers("token", getString("token"))
                            .params("activityId", intent.getStringExtra("activityId"))
                            .execute(object : StringDialogCallback(baseContext) {

                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                    toast(msg)

                                    onBackPressed()
                                }

                            })
                } else {
                    if (externalLink.isEmpty()) {
                        intent.setClass(baseContext, CultureEditActivity::class.java)
                        intent.putExtra("signUpLimit", signUpLimit)
                        intent.putExtra("peopleCount", peopleCount)
                        intent.putExtra("address", culture_address.text.toString())
                        intent.putExtra("time", culture_time.text.toString())
                        startActivity(intent)
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(externalLink)))
                    }
                }
            }
        }
    }

    override fun getData() {
        OkGo.post<CultureModel>(BaseHttp.activity_detil)
                .tag(this@CultureDetailActivity)
                .params("activityId", intent.getStringExtra("activityId"))
                .execute(object : JacksonDialogCallback<CultureModel>(baseContext, CultureModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<CultureModel>) {
                        if (response.body().activityDetil != null) {

                            val data = response.body().activityDetil!!

                            startDate = data.activityStartDate
                            endDate = data.activityEndDate
                            signUpLimit = data.signUpLimit
                            peopleCount = data.peopleCount
                            externalLink = data.externalLink

                            culture_time.text = "活动时间：$startDate - ${endDate.substring(11)}"
                            culture_address.text = "活动地点：" + data.address

                            GlideApp.with(baseContext)
                                    .load(BaseHttp.baseImg + data.activityHead)
                                    .placeholder(R.mipmap.not_4) // 等待时的图片
                                    .error(R.mipmap.not_4)       // 加载失败的图片
                                    .centerCrop()
                                    .dontAnimate()
                                    .into(culture_detail_img)

                            val str = "<meta " +
                                    "name=\"viewport\" " +
                                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                    "<style>" +
                                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                    "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                    "*{ max-width:100% !important; }\n" +
                                    "</style>"

                            val info = data.activityContent
                            culture_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                        }
                    }

                })
    }

    private fun getVolunteerData() {
        OkGo.post<String>(BaseHttp.volunteeractivity_detil)
                .tag(this@CultureDetailActivity)
                .params("activityId", intent.getStringExtra("activityId"))
                .params("userinfoId", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val data = JSONObject(response.body()).optJSONObject("object")

                        startDate = data.optString("activityStartDate")
                        endDate = data.optString("activityEndDate")
                        val volunteer = data.optString("isvolunteer")
                        val isbm = data.optString("isbm")
                        stypeId = data.optString("stypeId")

                        culture_time.text = "活动时间：$startDate - ${endDate.substring(11)}"
                        culture_address.text = "活动地点：" + data.optString("address")
                        if (volunteer != "1") culture_detail_book.visibility = View.GONE
                        else {
                            if (isbm == "1") culture_detail_book.text = "已报名"
                            else {
                                when (stypeId) {
                                    "-1" -> culture_detail_book.text = "活动未开始"
                                    "0" -> culture_detail_book.text = "活动报名"
                                    "1" -> culture_detail_book.text = "报名截止"
                                    "2" -> culture_detail_book.text = "活动进行中"
                                    "3" -> culture_detail_book.text = "活动结束"
                                }
                            }
                        }

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + data.optString("activityHead"))
                                .placeholder(R.mipmap.not_4) // 等待时的图片
                                .error(R.mipmap.not_4)       // 加载失败的图片
                                .centerCrop()
                                .dontAnimate()
                                .into(culture_detail_img)

                        val str = "<meta " +
                                "name=\"viewport\" " +
                                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                "<style>" +
                                ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ max-width: 100% !important; display:block; height:auto !important; }" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>"

                        val info = data.optString("activityContent")
                        culture_detail_web.loadDataWithBaseURL(BaseHttp.baseImg, "$str<div class=\"con\">$info</div>", "text/html", "utf-8", "")
                    }

                })
    }
}
