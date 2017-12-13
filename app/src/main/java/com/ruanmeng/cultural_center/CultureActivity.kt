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
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class CultureActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    val list_qu = ArrayList<CommonData>()
    val list_act = ArrayList<CommonData>()

    private var district = ""
    private var dateId = ""
    private var programaId = ""
    private var stypeId = ""
    private var pos_qu = 0
    private var pos_time = -1
    private var pos_act = 0
    private var pos_status = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("文化活动", "筛选")

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

        empty_hint.text = "暂无文化活动信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_culture_list) { data, injector ->
                    injector.visible(R.id.item_culture_status)
                            .text(R.id.item_culture_title, data.activityTitle)
                            .text(
                                    R.id.item_culture_time,
                                    "活动时间：" + data.activityStartDate + " - " + (data.activityEndDate.substring(11)))
                            .text(R.id.item_culture_address, "活动地点：" + data.address)
                            .text(R.id.item_culture_status, "活动状态：" + data.stype)

                            .with<ImageView>(R.id.item_culture_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.activityHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_culture) {
                                val intent = Intent(baseContext, CultureDetailActivity::class.java)
                                intent.putExtra("activityId", data.activityId)
                                intent.putExtra("stypeId", data.stypeId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.tv_nav_right -> {
                OkGo.post<CommonModel>(BaseHttp.get_district)
                        .tag(this@CultureActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                list_qu.clear()
                                list_qu.add(CommonData().apply {
                                    areaId = ""
                                    areaName = response.body().maincity
                                })

                                list_qu.addItems(response.body().las)
                            }

                            override fun onFinish() {
                                super.onFinish()
                                getPrograma()
                            }

                        })
            }
        }
    }

    private fun getPrograma() {

        OkGo.post<CommonModel>(BaseHttp.programa_list)
                .tag(this@CultureActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        list_act.clear()
                        list_act.add(CommonData().apply {
                            programaId = ""
                            programaName = "全部"
                        })
                        list_act.addItems(response.body().programa)

                        PopupWindowUtils.showFilterPopWindow(
                                baseContext,
                                divider,
                                list_qu,
                                list_act,
                                pos_qu,
                                pos_time,
                                pos_act,
                                pos_status
                        ) { qu, time, act, status, pos_1, pos_2, pos_3, pos_4 ->
                            district = qu
                            dateId = time
                            programaId = act
                            stypeId = status

                            pos_qu = pos_1
                            pos_time = pos_2
                            pos_act = pos_3
                            pos_status = pos_4

                            updateList()
                        }
                    }
                })
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

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.activity_data)
                .tag(this@CultureActivity)
                .params("page", pindex)
                .params("date", dateId)
                .params("programaId", programaId)
                .params("stypeId", stypeId)
                .params("district", district)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().las)
                            if (count(response.body().las) > 0) pageNum++
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
}
