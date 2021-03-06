package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.ClubData
import com.ruanmeng.model.ClubModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class SocietyActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("群文社团")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无社团信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<ClubData>(R.layout.item_union_list) { data, injector ->
                    injector.gone(R.id.item_union_host)
                            .text(R.id.item_union_title, data.clubTitle)

                            .with<ImageView>(R.id.item_union_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.clubHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_union) {
                                val intent = Intent(baseContext, SocietyDetailActivity::class.java)
                                intent.putExtra("clubId", data.clubId)
                                intent.putExtra("title", data.clubTitle)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<ClubModel>(BaseHttp.club_index)
                .tag(this@SocietyActivity)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<ClubModel>(baseContext, ClubModel::class.java) {

                    override fun onSuccess(response: Response<ClubModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().clubList)
                            if (count(response.body().clubList) > 0) pageNum++
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
