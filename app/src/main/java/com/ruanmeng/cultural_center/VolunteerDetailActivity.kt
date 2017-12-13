package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.VolunteerData
import com.ruanmeng.model.VolunteerModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class VolunteerDetailActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var data: VolunteerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("志愿者")

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
        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.header_volunteer) { _, injector ->
                    injector.text(R.id.volunteer_detail_name, data?.name ?: "")
                            .text(R.id.volunteer_detail_label, data?.synopsis ?: "")
                            .text(R.id.volunteer_detail_job,
                                    if (data == null) "" else when(data!!.sex) {
                                        "1" -> "男"
                                        "2" -> "女"
                                        else -> ""
                                    })

                            .with<RoundedImageView>(R.id.volunteer_detail_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + (data?.volunteerHead ?: ""))
                                        .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                        .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }
                }
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
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<VolunteerModel>(BaseHttp.volunteer_detail)
                .tag(this@VolunteerDetailActivity)
                .params("page", pindex)
                .params("volunteerId", intent.getStringExtra("volunteerId"))
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                add("header")
                                pageNum = pindex
                            }
                            addItems(response.body().volunteerActivityList)
                            if (count(response.body().volunteerActivityList) > 0) pageNum++
                        }

                        data = response.body().volunteerDetail

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false
                    }

                })
    }
}
