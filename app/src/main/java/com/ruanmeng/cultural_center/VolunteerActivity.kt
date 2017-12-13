package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.ImageView
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.adapter.MultiGapDecoration
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.VolunteerData
import com.ruanmeng.model.VolunteerModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class VolunteerActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<Any>()
    private val list_act = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("志愿者")


        list.add("轮播图")
        mAdapter.updateData(list).notifyDataSetChanged()

        swipe_refresh.isRefreshing = true
        getSliderData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh {
            list.clear()
            list.add("轮播图")
            mAdapter.updateData(list).notifyDataSetChanged()

            getSliderData()
        }
        recycle_list.load_Grid(swipe_refresh) {
            @Suppress("DEPRECATION")
            setBackgroundColor(resources.getColor(R.color.white))
            layoutManager = GridLayoutManager(baseContext, 4)

            (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = if (mAdapter.getItem(position) is CommonData) 1 else 4
            }

            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_volunteer_divder) { _, injector ->
                    injector.text(R.id.volunteer_hint, "优秀志愿者")
                            .with<RollPagerView>(R.id.volunteer_banner) { view ->
                                val mLoopAdapter = LoopAdapter(baseContext, view)
                                view.apply {
                                    setAdapter(mLoopAdapter)
                                    setOnItemClickListener { position ->
                                        //轮播图点击事件
                                    }
                                }

                                val imgs = ArrayList<String>()
                                list_slider.mapTo(imgs) { (it as CommonData).sliderImg }
                                mLoopAdapter.setImgs(imgs)
                                if (imgs.size <= 1) {
                                    view.pause()
                                    view.setHintViewVisibility(false)
                                } else {
                                    view.resume()
                                    view.setHintViewVisibility(true)
                                }
                            }
                }
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
                .register<VolunteerData>(R.layout.item_volunteer_list) { data, injector ->
                    injector.text(R.id.item_volunter_title, data.activityTitle)
                            .text(R.id.item_volunter_content, data.activityIntro)

                            .visibility(R.id.item_volunter_top, if (list_act.indexOf(data) != 0) View.GONE else View.VISIBLE)
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

    private fun getSliderData() {
        OkGo.post<VolunteerModel>(BaseHttp.volunteer_slider_data)
                .tag(this@VolunteerActivity)
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {
                        list_slider.apply {
                            clear()
                            addItems(response.body().volunteerSliderList)
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        getData()
                    }

                })
    }

    override fun getData() {
        OkGo.post<VolunteerModel>(BaseHttp.volunteer_index)
                .tag(this@VolunteerActivity)
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {

                        list.addItems(response.body().volunteerList)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        getActivtyData()
                    }

                })
    }

    private fun getActivtyData() {
        OkGo.post<VolunteerModel>(BaseHttp.volunteer_activity)
                .tag(this@VolunteerActivity)
                .execute(object : JacksonDialogCallback<VolunteerModel>(baseContext, VolunteerModel::class.java) {

                    override fun onSuccess(response: Response<VolunteerModel>) {

                        list.addItems(response.body().volunteerActivityList)
                        list_act.addItems(response.body().volunteerActivityList)
                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.volunteer_type -> startActivity(VolunteerListActivity::class.java)
        }
    }
}
