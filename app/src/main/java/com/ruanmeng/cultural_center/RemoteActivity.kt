package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.MultiGapDecoration
import com.ruanmeng.base.*
import com.ruanmeng.model.RemoteData
import com.ruanmeng.model.RemoteModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class RemoteActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var foreignKey = ""
    private var courseTypeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote)
        init_title("远程辅导", "筛选")

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
        recycle_list.load_Grid(swipe_refresh, {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }) {
            layoutManager = GridLayoutManager(baseContext, 2)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<RemoteData>(R.layout.item_remote_grid) { data, injector ->
                    injector.text(R.id.item_remote_title, data.courseTitle)

                            .with<ImageView>(R.id.item_remote_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.courseHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_remote) {
                                val intent = Intent(baseContext, RemoteDetailActivity::class.java)
                                intent.putExtra("courseId", data.courseId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<RemoteModel>(BaseHttp.search_course)
                .tag(this@RemoteActivity)
                .params("foreignKey", foreignKey)
                .params("courseTypeId", courseTypeId)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<RemoteModel>(baseContext, RemoteModel::class.java) {

                    override fun onSuccess(response: Response<RemoteModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().courseList)
                            if (count(response.body().courseList) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> {
                OkGo.post<RemoteModel>(BaseHttp.big_type)
                        .tag(this@RemoteActivity)
                        .execute(object : JacksonDialogCallback<RemoteModel>(baseContext, RemoteModel::class.java, true) {

                            override fun onSuccess(response: Response<RemoteModel>) {
                                if (response.body().bigTypeList != null && response.body().bigTypeList!!.isNotEmpty()) {

                                    val list_left = ArrayList<RemoteData>()
                                    list_left.apply {
                                        add(RemoteData().apply {
                                            courseTypeId = ""
                                            courseTypeName = "全部"
                                        })
                                        addItems(response.body().bigTypeList)
                                    }

                                    PopupWindowUtils.showTypePopWindow(baseContext,
                                            divider,
                                            list_left,
                                            foreignKey,
                                            courseTypeId,
                                            object : PopupWindowUtils.PopupWindowTypeCallBack {

                                                override fun getSecondList(foreignKeyId: String, handler: Handler) {
                                                    if (foreignKeyId.isNotEmpty()) {
                                                        OkGo.post<RemoteModel>(BaseHttp.small_type)
                                                                .tag(this@RemoteActivity)
                                                                .params("foreignKey", foreignKeyId)
                                                                .execute(object : JacksonDialogCallback<RemoteModel>(baseContext, RemoteModel::class.java, true) {

                                                                    override fun onSuccess(response: Response<RemoteModel>) {
                                                                        if (response.body().smallTypeList != null) {
                                                                            val msg = Message()
                                                                            msg.obj = response.body().smallTypeList
                                                                            msg.what = 1
                                                                            handler.sendMessage(msg)
                                                                        }
                                                                    }

                                                                })
                                                    }
                                                }

                                                @SuppressLint("SetTextI18n")
                                                override fun doWork(first: RemoteData?, second: RemoteData?) {
                                                    foreignKey = first?.courseTypeId ?: ""
                                                    courseTypeId = second?.courseTypeId ?: ""

                                                    updateList()
                                                }
                                            })
                                }
                            }

                        })
            }
        }
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
