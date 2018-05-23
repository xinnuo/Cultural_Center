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
import com.ruanmeng.model.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CollectActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    private var stypeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)
        init_title("我的收藏")

        EventBus.getDefault().register(this@CollectActivity)

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无收藏信息！"

        collect_tab.apply {
            addTab(this.newTab().setText("新闻文章"))
            addTab(this.newTab().setText("网上展厅"))
            addTab(this.newTab().setText("非遗"))

            post { Tools.setIndicator(this, 30, 30) }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> stypeId = 0
                        1 -> stypeId = 2
                        2 -> stypeId = 1
                    }

                    window.decorView.postDelayed({ runOnUiThread {
                        OkGo.getInstance().cancelTag(this@CollectActivity)
                        updateList()
                    } }, 300)
                }
            })
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_first_list) { data, injector ->
                    injector.text(R.id.item_first_title, data.newsTitle)
                            .text(R.id.item_first_content, data.contentIntro)
                            .text(R.id.item_first_time, data.createDate)

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_first_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.newsHead)
                                        .placeholder(R.mipmap.not_2) //等待时的图片
                                        .error(R.mipmap.not_2)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_first) {
                                val intent = Intent(baseContext, NoticeDetailActivity::class.java)
                                intent.putExtra("newsId", data.businessKey)
                                startActivity(intent)
                            }
                }
                .register<OnlineData>(R.layout.item_art_list) { data, injector ->
                    injector.text(R.id.item_art_title, data.title)
                            .text(R.id.item_art_desc, "作品集共${data.sum}件作品")

                            .with<ImageView>(R.id.item_art_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.onlineArtHead)
                                        .placeholder(R.mipmap.not_4) // 等待时的图片
                                        .error(R.mipmap.not_4)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_art) {
                                val intent = Intent(baseContext, ArtDetailActivity::class.java)
                                intent.putExtra("onlineArtId", data.businessKey)
                                startActivity(intent)
                            }
                }
                .register<CollectData>(R.layout.item_union_list) { data, injector ->
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
                                intent.putExtra("heritageId", data.businessKey)
                                intent.putExtra("title", data.heritageTitle)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CollectModel>(BaseHttp.user_collect)
                .tag(this@CollectActivity)
                .headers("token", getString("token"))
                .params("stypeId", stypeId)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CollectModel>(baseContext, CollectModel::class.java) {

                    override fun onSuccess(response: Response<CollectModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            when (stypeId) {
                                0 -> {
                                    addItems(response.body().newsList)
                                    if (count(response.body().newsList) > 0) pageNum++
                                }
                                1 -> {
                                    addItems(response.body().heritageList)
                                    if (count(response.body().heritageList) > 0) pageNum++
                                }
                                2 -> {
                                    addItems(response.body().onlineartList)
                                    if (count(response.body().onlineartList) > 0) pageNum++
                                }
                            }
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

    @Subscribe
    fun onMessageEvent(event: CollectMessageEvent) {
        updateList()
    }
}
