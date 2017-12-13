package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import com.allen.library.SuperTextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.OrganData
import com.ruanmeng.model.OrganModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DensityUtil
import kotlinx.android.synthetic.main.header_organ.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx

class OrganActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("组织架构")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .addHeader(baseContext, R.layout.header_organ)
                .register<OrganData>(R.layout.item_organ_list) { data, injector ->
                    injector.with<SuperTextView>(R.id.item_organ_title) { view ->
                        view.setLeftString(data.deptName)
                        view.setBottomMargin(if (list.indexOf(data) == list.size - 1) 0 else DensityUtil.dp2px(10f))

                        view.setOnClickListener {
                            val intent = Intent(baseContext, OfficeActivity::class.java)
                            intent.putExtra("deptId", data.culturalDeptId)
                            intent.putExtra("title", data.deptName)
                            startActivity(intent)
                        }
                    }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<OrganModel>(BaseHttp.cultural_dept)
                .tag(this@OrganActivity)
                .params("page", pindex)
                .params("culturalId", intent.getStringExtra("culturalId"))
                .execute(object : JacksonDialogCallback<OrganModel>(baseContext, OrganModel::class.java) {

                    override fun onSuccess(response: Response<OrganModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().culturalDept)
                            if (count(response.body().culturalDept) > 0) pageNum++
                        }

                        mAdapterEx.updateData(list).notifyDataSetChanged()

                        if (response.body().staffHeader != null) {
                            val data = response.body().staffHeader!!
                            organ_name.text = data.staffName
                            organ_job.text = data.duty
                            organ_label.text = data.honor

                            GlideApp.with(baseContext)
                                    .load(BaseHttp.baseImg + data.stuffHead)
                                    .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                    .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                    .dontAnimate()
                                    .into(organ_img)
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }
}
