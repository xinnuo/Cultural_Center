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
import com.ruanmeng.model.CultureModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class SpecialActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("文化专题")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无文化专题信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_union_list) { data, injector ->
                    injector.visible(R.id.item_union_host)
                            .text(R.id.item_union_title, data.cultureTitle)
                            .text(R.id.item_union_host, "主办方：" + data.hostUnit)

                            .with<ImageView>(R.id.item_union_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.cultureHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_union) {
                                val intent = Intent(baseContext, SpecialDetailActivity::class.java)
                                intent.putExtra("cultureId", data.cultureId)
                                intent.putExtra("title", data.cultureTitle)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CultureModel>(BaseHttp.culture_index)
                .tag(this@SpecialActivity)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CultureModel>(baseContext, CultureModel::class.java) {

                    override fun onSuccess(response: Response<CultureModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().cultureList)
                            if (count(response.body().cultureList) > 0) pageNum++
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
