package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
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
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_deliver.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class DeliverActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var heritagectId = 1
    private var heritageTypeId = ""
    private var heritageLevelId = ""
    private var pos_type = 0
    private var pos_level = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliver)
        init_title("非遗传承", "筛选")

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

        deliver_tab.apply {
            addTab(this.newTab().setText("非遗项目"))
            addTab(this.newTab().setText("传承人"))

            post { Tools.setIndicator(this, 40, 40) }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    heritagectId = tab.position + 1
                    heritageTypeId = ""
                    heritageLevelId = ""
                    pos_type = 0
                    pos_level = 0
                    tvRight.visibility = if (heritagectId == 1) View.VISIBLE else View.INVISIBLE

                    list.clear()
                    mAdapter.updateData(list).notifyDataSetChanged()

                    swipe_refresh.isRefreshing = true
                    OkGo.getInstance().cancelTag(this@DeliverActivity)
                    pageNum = 1
                    getData(pageNum)
                }

            })
        }

        empty_hint.text = "暂无非遗传承信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_union_list) { data, injector ->
                    injector.text(R.id.item_union_title, data.heritageTitle)

                            .with<ImageView>(R.id.item_union_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.heritageHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_union) {
                                val intent = Intent(baseContext, DeliverDetailActivity::class.java)
                                intent.putExtra("heritageId", data.heritageId)
                                intent.putExtra("title", data.heritageTitle)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.heritage_index)
                .tag(this@DeliverActivity)
                .params("heritagectId", heritagectId)
                .params("heritageTypeId", heritageTypeId)
                .params("heritageLevelId", heritageLevelId)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().heritageList)
                            if (count(response.body().heritageList) > 0) pageNum++
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
                OkGo.post<CommonModel>(BaseHttp.heritage_screen)
                        .tag(this@DeliverActivity)
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                            override fun onSuccess(response: Response<CommonModel>) {

                                val list_type = ArrayList<CommonData>()
                                list_type.add(CommonData().apply {
                                    heritageTypeId = ""
                                    heritageTypeName = "全部"
                                })
                                list_type.addItems(response.body().heritagetype)

                                val list_level = ArrayList<CommonData>()
                                list_level.add(CommonData().apply {
                                    heritagelevelId = ""
                                    heritagelevelName = "全部"
                                })
                                list_level.addItems(response.body().heritagelevel)

                                PopupWindowUtils.showDeliverFilterPopWindow(
                                        baseContext,
                                        divider,
                                        list_type,
                                        list_level,
                                        pos_type,
                                        pos_level
                                ) { type, level, pos_1, pos_2 ->
                                    heritageTypeId = type
                                    heritageLevelId = level

                                    pos_type = pos_1
                                    pos_level = pos_2

                                    updateList()
                                }
                            }

                        })
            }
        }
    }
}
