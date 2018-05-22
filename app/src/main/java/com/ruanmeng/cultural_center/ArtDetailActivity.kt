package com.ruanmeng.cultural_center

import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.*
import com.ruanmeng.model.CollectMessageEvent
import com.ruanmeng.model.OnlineData
import com.ruanmeng.model.OnlineModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_art_detail.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class ArtDetailActivity : BaseActivity() {

    private val list = ArrayList<OnlineData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art_detail)
        init_title("作品展示")

        swipe_refresh.isRefreshing = true
        getData()
        getCollectData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<OnlineData>(R.layout.item_art_list) { data, injector ->
                    injector.text(R.id.item_art_title, data.title)
                            .gone(R.id.item_art_desc)

                            .with<ImageView>(R.id.item_art_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.imgs)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)

                                view.setOnClickListener {
                                    val imgs = ArrayList<String>()
                                    list.mapTo(imgs) { BaseHttp.baseImg + it.imgs }

                                    MNImageBrowser.showImageBrowser(baseContext, view, list.indexOf(data), imgs)
                                }
                            }
                }
                .attachTo(recycle_list)

        art_detail_collect.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return@setOnTouchListener true
                } else {
                    if (!art_detail_collect.isChecked) {
                        OkGo.post<String>(BaseHttp.collect_business)
                                .tag(this@ArtDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("onlineArtId"))
                                .params("type", "3")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("收藏成功！")
                                        art_detail_collect.isChecked = true

                                        EventBus.getDefault().post(CollectMessageEvent("网上展厅"))
                                    }

                                })
                    } else {
                        OkGo.post<String>(BaseHttp.cancel_collect_sub)
                                .tag(this@ArtDetailActivity)
                                .headers("token", getString("token"))
                                .params("busId", intent.getStringExtra("onlineArtId"))
                                .params("type", "3")
                                .execute(object : StringDialogCallback(baseContext) {

                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                        toast("取消成功！")
                                        art_detail_collect.isChecked = false

                                        EventBus.getDefault().post(CollectMessageEvent("网上展厅"))
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
                    .tag(this@ArtDetailActivity)
                    .headers("token", getString("token"))
                    .params("busId", intent.getStringExtra("onlineArtId"))
                    .execute(object : StringDialogCallback(baseContext, false) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            val obj = JSONObject(response.body()).getBoolean("collected")

                            art_detail_collect.isChecked = obj
                        }

                    })
        }
    }

    override fun getData() {
        OkGo.post<OnlineModel>(BaseHttp.onlinehall_detail)
                .tag(this@ArtDetailActivity)
                .params("onlineArtId", intent.getStringExtra("onlineArtId"))
                .execute(object : JacksonDialogCallback<OnlineModel>(baseContext, OnlineModel::class.java) {

                    override fun onSuccess(response: Response<OnlineModel>) {
                        list.apply {
                            clear()
                            addItems(response.body().onlineartList)
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }
}
