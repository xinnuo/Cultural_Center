package com.ruanmeng.cultural_center

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_society_detail.*

class VolunteerTeamDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_society_detail)
        init_title("优秀志愿团队详情")
    }

    override fun init_title() {
        super.init_title()
        society_tab.apply {
            addTab(this.newTab().setText("首页"))
            addTab(this.newTab().setText("成员"))
            addTab(this.newTab().setText("资讯"))

            post { Tools.setIndicator(this, 30, 30) }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) { }

            })
        }
    }
}
