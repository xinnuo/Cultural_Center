package com.ruanmeng.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.NoticeDetailActivity
import com.ruanmeng.cultural_center.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.NewsModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.fragment_main_third.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class MainThirdFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private val list_item = ArrayList<CommonData>()

    private var newsTypeId = ""

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_third, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        getTabData()
    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        tv_nav_title.text = "资讯"
        empty_hint.text = "暂无资讯信息！"

        third_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    newsTypeId = list_item[tab.position].newsTypeId

                    activity.window.decorView.postDelayed({ activity.runOnUiThread {
                        OkGo.getInstance().cancelTag(this@MainThirdFragment)
                        updateList()
                    } }, 300)
                }

            })

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(activity, swipe_refresh) {
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
                            .visibility(R.id.item_first_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_first_img) { view ->
                                GlideApp.with(context)
                                        .load(BaseHttp.baseImg + data.newsHead)
                                        .placeholder(R.mipmap.not_2) //等待时的图片
                                        .error(R.mipmap.not_2)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_first) {
                                val intent = Intent(activity, NoticeDetailActivity::class.java)
                                intent.putExtra("newsId", data.newsId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    private fun getTabData() {
        OkGo.post<NewsModel>(BaseHttp.news_type)
                .tag(this@MainThirdFragment)
                .execute(object : JacksonDialogCallback<NewsModel>(activity, NewsModel::class.java, true) {

                    override fun onSuccess(response: Response<NewsModel>) {
                        list_item.addItems(response.body().newsTypeList)
                        for (item in list_item) third_tab.addTab(third_tab.newTab().setText(item.newTypeName))
                        third_tab.post { Tools.setIndicator(third_tab, 30, 30) }
                    }

                })
    }

    override fun getData(pindex: Int) {
        OkGo.post<NewsModel>(BaseHttp.news_list)
                .tag(this@MainThirdFragment)
                .params("page", pindex)
                .params("newsTypeId", newsTypeId)
                .execute(object : JacksonDialogCallback<NewsModel>(activity, NewsModel::class.java) {

                    override fun onSuccess(response: Response<NewsModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().newsList)
                            if (count(response.body().newsList) > 0) pageNum++
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

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
