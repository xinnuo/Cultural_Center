package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.OrganData
import com.ruanmeng.model.OrganModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class OfficeActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title(intent.getStringExtra("title"))

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无部门成员信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<OrganData>(R.layout.item_office_list) { data, injector ->
                    injector.text(R.id.item_office_name, data.staffName)
                            .text(R.id.item_office_job, data.duty)
                            .text(R.id.item_office_label, data.honor)

                            .visibility(R.id.item_office_divider1, if (list.indexOf(data) == 0) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_office_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.stuffHead)
                                        .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                        .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_office) {
                                intent.setClass(baseContext, OfficeDetailActivity::class.java)
                                intent.putExtra("staffId", data.staffId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<OrganModel>(BaseHttp.dept_staff)
                .tag(this@OfficeActivity)
                .params("page", pindex)
                .params("deptId", intent.getStringExtra("deptId"))
                .execute(object : JacksonDialogCallback<OrganModel>(baseContext, OrganModel::class.java) {

                    override fun onSuccess(response: Response<OrganModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().staff)
                            if (count(response.body().staff) > 0) pageNum++
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
