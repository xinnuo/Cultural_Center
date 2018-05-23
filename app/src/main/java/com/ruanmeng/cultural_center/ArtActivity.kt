package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.OnlineData
import com.ruanmeng.model.OnlineModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class ArtActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var onlineHallId = ""
    private var onlineArtTypeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_art)
        init_title("网上展厅", "筛选")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        @Suppress("DEPRECATION")
        val drawable = resources.getDrawable(R.mipmap.icon_shai)
        // 这一步必须要做,否则不会显示
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tvRight.setCompoundDrawables(null, null, drawable, null)

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<OnlineData>(R.layout.item_art_list) { data, injector ->
                    injector.text(R.id.item_art_title, data.title)
                            .text(R.id.item_art_desc, "作品集共${data.sum}件作品")

                            .with<ImageView>(R.id.item_art_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.onlineArtHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_art) {
                                val intent = Intent(baseContext, ArtDetailActivity::class.java)
                                intent.putExtra("onlineArtId", data.onlineArtId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<OnlineModel>(BaseHttp.onlinehall_data)
                .tag(this@ArtActivity)
                .params("onlineArtTypeId", onlineArtTypeId)
                .params("onlineHallId", onlineHallId)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<OnlineModel>(baseContext, OnlineModel::class.java) {

                    override fun onSuccess(response: Response<OnlineModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().artList)
                            if (count(response.body().artList) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> {
                OkGo.post<OnlineModel>(BaseHttp.onlinehall_index)
                        .tag(this@ArtActivity)
                        .execute(object : JacksonDialogCallback<OnlineModel>(baseContext, OnlineModel::class.java, true) {

                            override fun onSuccess(response: Response<OnlineModel>) {
                                if (response.body().onlineHallList != null && response.body().onlineHallList!!.isNotEmpty()) {

                                    val list_left = ArrayList<OnlineData>()
                                    list_left.apply {
                                        add(OnlineData().apply {
                                            onlineHallId = ""
                                            onlineHallName = "全部"
                                        })
                                        addItems(response.body().onlineHallList)
                                    }

                                    PopupWindowUtils.showOnlinePopWindow(
                                            baseContext,
                                            divider,
                                            list_left,
                                            onlineHallId,
                                            onlineArtTypeId,
                                            object : PopupWindowUtils.PopupWindowOnlineeCallBack {

                                                override fun getSecondList(Id: String, handler: Handler) {
                                                    if (Id.isNotEmpty()) {
                                                        OkGo.post<OnlineModel>(BaseHttp.onlinehall_artType)
                                                                .tag(this@ArtActivity)
                                                                .params("onlineHallId", Id)
                                                                .execute(object : JacksonDialogCallback<OnlineModel>(baseContext, OnlineModel::class.java, true) {

                                                                    override fun onSuccess(response: Response<OnlineModel>) {
                                                                        if (response.body().onlineHallArtTypeList != null) {
                                                                            val msg = Message()
                                                                            msg.obj = response.body().onlineHallArtTypeList
                                                                            msg.what = 1
                                                                            handler.sendMessage(msg)
                                                                        }
                                                                    }

                                                                })
                                                    }
                                                }

                                                @SuppressLint("SetTextI18n")
                                                override fun doWork(first: OnlineData?, second: OnlineData?) {
                                                    onlineHallId = first?.onlineHallId ?: ""
                                                    onlineArtTypeId = second?.onlineArtTypeId ?: ""

                                                    updateList()
                                                }
                                            })
                                }
                            }

                        })
            }
        }
    }
}
