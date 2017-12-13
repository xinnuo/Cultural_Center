package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.GlideApp
import com.ruanmeng.cultural_center.R
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_title.*

class MainSecondFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_second, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        tv_nav_title.text = "文化"

        Glide.with(activity)

        GlideApp.with(activity)
                .load(R.mipmap.datu_tu)
                .placeholder(R.mipmap.not_4) //等待时的图片
                .error(R.mipmap.not_4)       //加载失败的图片
                .dontAnimate()
                .into(second_img)
    }
}
