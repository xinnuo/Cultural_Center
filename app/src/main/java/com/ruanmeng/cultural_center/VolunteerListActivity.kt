package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.MultiGapDecoration
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.VolunteerModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class VolunteerListActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("优秀志愿者")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无志愿者信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Grid(swipe_refresh, {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }) {
            @Suppress("DEPRECATION")
            setBackgroundColor(resources.getColor(R.color.white))
            layoutManager = GridLayoutManager(baseContext, 4)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_volunteer_grid) { data, injector ->
                    injector.text(R.id.item_volunteer_name, data.name)
                            .with<RoundedImageView>(R.id.item_volunteer_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.volunteerHead)
                                        .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                        .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }
                            .clicked(R.id.item_volunteer_img) {
                                val intent = Intent(baseContext, VolunteerDetailActivity::class.java)
                                intent.putExtra("volunteerId", data.volunteerId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<VolunteerModel>(BaseHttp.volunteer_all)
                .tag(this@VolunteerListActivity)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().volunteerList)
                            if (count(response.body().volunteerList) > 0) pageNum++
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
