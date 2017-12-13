package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.VolunteerData
import com.ruanmeng.model.VolunteerModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class VolunteerMineActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_mine)
        init_title("我是志愿者", "修改信息")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无参与活动信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        tvRight.setOnClickListener {
            val intent = Intent(baseContext, VolunteerBecomeActivity::class.java)
            intent.putExtra("isModify", true)
            startActivity(intent)
        }

        mAdapter = SlimAdapter.create()
                .register<VolunteerData>(R.layout.item_volunteer_list) { data, injector ->
                    injector.text(R.id.item_volunter_title, data.activityTitle)
                            .text(R.id.item_volunter_content, data.activityIntro)

                            .visibility(R.id.item_volunter_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_volunter_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_volunter_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.activityHead)
                                        .placeholder(R.mipmap.not_2) //等待时的图片
                                        .error(R.mipmap.not_2)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_volunter) {
                                val intent = Intent(baseContext, CultureDetailActivity::class.java)
                                intent.putExtra("activityId", data.activityId)
                                intent.putExtra("isVolunteer", true)
                                intent.putExtra("isMine", true)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<VolunteerModel>(BaseHttp.user_volunteerActivity)
                .tag(this@VolunteerMineActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().activity)
                            if (count(response.body().activity) > 0) pageNum++
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
