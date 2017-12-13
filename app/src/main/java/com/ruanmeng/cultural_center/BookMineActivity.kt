package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_culture_mine.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class BookMineActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var stypeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_mine)
        init_title("场地预约")

        radio_check1.performClick()
    }

    override fun init_title() {
        super.init_title()
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            OkGo.getInstance().cancelTag(this@BookMineActivity)

            when(checkedId) {
                R.id.radio_check1 -> {
                    stypeId = 0
                    updateList()
                }
                R.id.radio_check2 -> {
                    stypeId = 1
                    updateList()
                }
            }
        }

        empty_hint.text = "暂无场地预约信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_book_list) { data, injector ->
                    injector.text(R.id.item_book_title, data.venueName)
                            .text(R.id.item_book_content, "地址：" + data.hallName)
                            .text(R.id.item_book_time, data.reserveStartDate + " - " + data.reserveEndDate)

                            .with<ImageView>(R.id.item_book_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.venueHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.user_appointVenue)
                .tag(this@BookMineActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("stypeId", stypeId)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().reserveVenueList)
                            if (count(response.body().reserveVenueList) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    private fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
