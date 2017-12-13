package com.ruanmeng.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.MultiGapDecoration
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CultureModel
import com.ruanmeng.share.BaseHttp
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class SpecialSecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater!!.inflate(R.layout.fragment_special_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        empty_hint.text = "暂无视频信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Grid(swipe_refresh, {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }) {
            layoutManager = GridLayoutManager(activity, 2)
            addItemDecoration(MultiGapDecoration().apply { isOffsetTopEnabled = true })
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_music_grid) { data, injector ->
                    injector.text(R.id.item_music_title, data.culturevideoName)

                            .with<ImageView>(R.id.item_music_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.culturevideoHead)
                                        .placeholder(R.mipmap.not_2) // 等待时的图片
                                        .error(R.mipmap.not_2)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_music_img) {
                                JCVideoPlayerStandard.startFullscreen(
                                        activity,
                                        JCVideoPlayerStandard::class.java,
                                        BaseHttp.baseImg + data.video,
                                        data.culturevideoName)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CultureModel>(BaseHttp.culture_video)
                .tag(this@SpecialSecondFragment)
                .params("cultureId", arguments.getString("cultureId"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CultureModel>(activity, CultureModel::class.java) {

                    override fun onSuccess(response: Response<CultureModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().cultureVideoList)
                            if (count(response.body().cultureVideoList) > 0) pageNum++
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
