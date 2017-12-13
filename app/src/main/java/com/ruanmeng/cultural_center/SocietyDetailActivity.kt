package com.ruanmeng.cultural_center

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.SocietyFirstFragment
import com.ruanmeng.fragment.SocietySecondFragment
import com.ruanmeng.fragment.SocietyThirdFragment
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_society_detail.*

class SocietyDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_society_detail)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        val fragments = ArrayList<Fragment>()
        val titles = listOf("首页", "成员", "资讯")

        fragments.add(SocietyFirstFragment().apply {
            arguments = Bundle().apply {
                putString("clubId", intent.getStringExtra("clubId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        fragments.add(SocietySecondFragment().apply {
            arguments = Bundle().apply {
                putString("clubId", intent.getStringExtra("clubId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        fragments.add(SocietyThirdFragment().apply {
            arguments = Bundle().apply {
                putString("clubId", intent.getStringExtra("clubId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        society_tab.apply {
            post { Tools.setIndicator(this, 30, 30) }
            removeAllTabs()
        }
        society_pager.adapter = TabFragmentAdapter(supportFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        society_tab.setupWithViewPager(society_pager)
    }
}
