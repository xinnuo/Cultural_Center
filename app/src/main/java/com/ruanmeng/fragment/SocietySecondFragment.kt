package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.R
import com.ruanmeng.model.ClubData
import com.ruanmeng.model.ClubModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class SocietySecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_special_fourth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        empty_hint.text = "暂无成员信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(activity!!, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<ClubData>(R.layout.item_office_list) { data, injector ->
                    injector.text(R.id.item_office_name, data.clubMemberName)
                            .text(R.id.item_office_job, data.duty)
                            .text(R.id.item_office_label, data.honor)

                            .visibility(R.id.item_office_divider1, if (list.indexOf(data) == 0) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_office_img) { view ->
                                GlideApp.with(activity!!)
                                        .load(BaseHttp.baseImg + data.clubMemberHead)
                                        .placeholder(R.mipmap.icon_touxiang) //等待时的图片
                                        .error(R.mipmap.icon_touxiang)       //加载失败的图片
                                        .dontAnimate()
                                        .into(view)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<ClubModel>(BaseHttp.club_member)
                .tag(this@SocietySecondFragment)
                .params("clubId", arguments!!.getString("clubId"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<ClubModel>(activity, ClubModel::class.java) {

                    override fun onSuccess(response: Response<ClubModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().clubMemberList)
                            if (count(response.body().clubMemberList) > 0) pageNum++
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