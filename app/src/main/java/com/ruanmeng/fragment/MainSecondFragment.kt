package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_title.*
import net.idik.lib.slimadapter.SlimAdapter

class MainSecondFragment : BaseFragment() {

    private val items = ArrayList<CommonData>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_second, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        if ((activity as MainActivity).list.isEmpty()) {
            items.add(CommonData().apply {
                title = "远程辅导"
                content = "Remote tutor"
                resId = R.mipmap.bigicon_yuancheng
            })
            items.add(CommonData().apply {
                title = "非遗传承"
                content = "Inheriting the culture"
                resId = R.mipmap.bigicon_chuancheng
            })
            items.add(CommonData().apply {
                title = "网上展厅"
                content = "Online exhibition hall"
                resId = R.mipmap.bigicon_wangshang
            })
            items.add(CommonData().apply {
                title = "文化专题"
                content = "Special culture"
                resId = R.mipmap.bigicon_zhuanti
            })
            items.add(CommonData().apply {
                title = "群文社团"
                content = "community"
                resId = R.mipmap.bigicon_shetuan
            })
            items.add(CommonData().apply {
                title = "文化志愿"
                content = "Volunteer activities"
                resId = R.mipmap.bigicon_zhiyuan
            })
        } else {
            val mlist = (activity as MainActivity).list

            if (mlist.any { it.barName == "远程辅导" })
                items.add(CommonData().apply {
                    title = "远程辅导"
                    content = "Remote tutor"
                    resId = R.mipmap.bigicon_yuancheng
                })

            if (mlist.any { it.barName == "非遗传承" })
            items.add(CommonData().apply {
                title = "非遗传承"
                content = "Inheriting the culture"
                resId = R.mipmap.bigicon_chuancheng
            })

            if (mlist.any { it.barName == "网上展厅" })
            items.add(CommonData().apply {
                title = "网上展厅"
                content = "Online exhibition hall"
                resId = R.mipmap.bigicon_wangshang
            })

            if (mlist.any { it.barName == "文化专题" })
            items.add(CommonData().apply {
                title = "文化专题"
                content = "Special culture"
                resId = R.mipmap.bigicon_zhuanti
            })

            if (mlist.any { it.barName == "群文社团" })
            items.add(CommonData().apply {
                title = "群文社团"
                content = "community"
                resId = R.mipmap.bigicon_shetuan
            })

            if (mlist.any { it.barName == "文化志愿" })
            items.add(CommonData().apply {
                title = "文化志愿"
                content = "Volunteer activities"
                resId = R.mipmap.bigicon_zhiyuan
            })
        }

        (second_list.adapter as SlimAdapter).updateData(items).notifyDataSetChanged()
    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        tv_nav_title.text = "文化"

        second_img.setOnClickListener {
            val mlist = (activity as MainActivity).list

            if (mlist.isEmpty()) startActivity(UnionActivity::class.java)
            else {
                if (mlist.any { it.barName == "文化联盟" }) {
                    startActivity(UnionActivity::class.java)
                } else {
                    toast("该功能暂未开放！")
                }
            }
        }

        GlideApp.with(activity!!)
                .load(R.mipmap.datu_tu)
                .placeholder(R.mipmap.not_4) //等待时的图片
                .error(R.mipmap.not_4)       //加载失败的图片
                .dontAnimate()
                .into(second_img)

        second_list.load_Grid {
            layoutManager = FullyGridLayoutManager(activity, 2)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_second_grid) { data, injector ->
                        injector.text(R.id.item_second_title, data.title)
                                .text(R.id.item_second_en, data.content)
                                .background(R.id.item_second_bg, data.resId)

                                .clicked(R.id.item_second_bg) {
                                    when (data.title) {
                                        "远程辅导" -> startActivity(RemoteActivity::class.java)
                                        "非遗传承" -> startActivity(DeliverActivity::class.java)
                                        "网上展厅" -> startActivity(ArtActivity::class.java)
                                        "文化专题" -> startActivity(SpecialActivity::class.java)
                                        "群文社团" -> startActivity(SocietyActivity::class.java)
                                        "文化志愿" -> startActivity(VolunteerActivity::class.java)
                                    }
                                }
                    }
                    .attachTo(second_list)
        }
    }
}
