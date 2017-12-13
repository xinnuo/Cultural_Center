package com.ruanmeng.cultural_center

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.fragment.SpecialFirstFragment
import com.ruanmeng.fragment.SpecialFourthFragment
import com.ruanmeng.fragment.SpecialSecondFragment
import com.ruanmeng.fragment.SpecialThirdFragment
import com.ruanmeng.utils.Tools
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer
import kotlinx.android.synthetic.main.activity_special_detail.*

class SpecialDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_detail)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        val fragments = ArrayList<Fragment>()
        val titles = listOf("首页", "视频", "相册", "资讯")

        fragments.add(SpecialFirstFragment().apply {
            arguments = Bundle().apply {
                putString("cultureId", intent.getStringExtra("cultureId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        fragments.add(SpecialSecondFragment().apply {
            arguments = Bundle().apply {
                putString("cultureId", intent.getStringExtra("cultureId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        fragments.add(SpecialThirdFragment().apply {
            arguments = Bundle().apply {
                putString("cultureId", intent.getStringExtra("cultureId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        fragments.add(SpecialFourthFragment().apply {
            arguments = Bundle().apply {
                putString("cultureId", intent.getStringExtra("cultureId"))
                putString("title", intent.getStringExtra("title"))
            }
        })

        special_tab.apply {
            post { Tools.setIndicator(this, 20, 20) }
            removeAllTabs()
        }
        special_pager.adapter = TabFragmentAdapter(supportFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        special_tab.setupWithViewPager(special_pager)
    }

    override fun onBackPressed() {
        if (JCVideoPlayer.backPress()) return
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        JCVideoPlayer.releaseAllVideos()
    }
}
