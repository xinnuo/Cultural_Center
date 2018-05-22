package com.ruanmeng.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.NoticeDetailActivity
import com.ruanmeng.cultural_center.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title.*
import net.idik.lib.slimadapter.SlimAdapter

class MainFirstFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<Any>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        tv_nav_title.text = "首页"

        swipe_refresh.refresh { getData() }
        activity?.let { recycle_list.load_Linear(it, swipe_refresh) }

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.header_first) { _, injector ->
                    injector.with<RollPagerView>(R.id.first_banner) { view ->
                        val mLoopAdapter = LoopAdapter(this@MainFirstFragment.activity, view)
                        view.apply {
                            setAdapter(mLoopAdapter)
                            setOnItemClickListener { /*//轮播图点击事件*/ }
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
                .register<CommonData>(R.layout.item_first_list) { data, injector ->
                    injector.text(R.id.item_first_title, data.newsTitle)
                            .text(R.id.item_first_content, data.contentIntro)
                            .text(R.id.item_first_time, data.createDate)

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_first_img) { view ->
                                GlideApp.with(activity!!)
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

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.index_data)
                .tag(this@MainFirstFragment)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.index_data)
                .execute(object : JacksonDialogCallback<CommonModel>(this@MainFirstFragment.activity, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_slider.apply {
                            clear()
                            addItems(response.body().slider)
                        }

                        list.apply {
                            clear()
                            add("轮播图")
                            addItems(response.body().news)
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }
}
