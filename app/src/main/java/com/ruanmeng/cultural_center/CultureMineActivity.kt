package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import com.lzy.extend.StringDialogCallback
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

class CultureMineActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var stypeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_mine)
        init_title("我的活动")

        radio_check1.performClick()
    }

    override fun init_title() {
        super.init_title()
        radio_check1.text = "已报名"
        radio_check2.text = "已取消"
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            OkGo.getInstance().cancelTag(this@CultureMineActivity)

            when(checkedId) {
                R.id.radio_check1 -> {
                    stypeId = 2
                    updateList()
                }
                R.id.radio_check2 -> {
                    stypeId = 3
                    updateList()
                }
            }
        }

        empty_hint.text = "暂无活动信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_culture_mine_list) { data, injector ->
                    injector.text(R.id.item_culture_title, data.activityTitle)
                            .text(R.id.item_culture_time, "时间：${data.activityStartDate} - ${data.activityEndDate.substring(11)}")
                            .text(R.id.item_culture_address, "地点：" + data.address)
                            .visibility(R.id.item_culture_cancel, if (data.stypeId == "0" && stypeId != 3) View.VISIBLE else View.GONE)

                            .with<ImageView>(R.id.item_culture_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.activityHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_culture_cancel) {
                                AlertDialog.Builder(baseContext)
                                        .setTitle("温馨提示")
                                        .setMessage("确定要取消报名吗？")
                                        .setPositiveButton("确定") { dialog, _ ->
                                            dialog.dismiss()

                                            OkGo.post<String>(BaseHttp.un_useractivity)
                                                    .tag(this@CultureMineActivity)
                                                    .params("userActivityId", data.userActivityId)
                                                    .execute(object : StringDialogCallback(baseContext) {

                                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                            toast(msg)
                                                            val position = list.indexOf(data)
                                                            list.remove(data)
                                                            mAdapter.notifyItemRemoved(position)
                                                        }

                                                    })
                                        }
                                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                                        .create()
                                        .show()
                            }

                            .clicked(R.id.item_culture) {
                                val intent = Intent(baseContext, CultureDetailActivity::class.java)
                                intent.putExtra("activityId", data.activityId)
                                intent.putExtra("stypeId", data.stypeId)
                                intent.putExtra("isMine", false)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.user_activity)
                .tag(this@CultureMineActivity)
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
                            addItems(response.body().activity)
                            if (count(response.body().activity) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
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
            mAdapter.notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
