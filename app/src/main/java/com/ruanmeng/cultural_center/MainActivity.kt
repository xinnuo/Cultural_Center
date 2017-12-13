package com.ruanmeng.cultural_center

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getBoolean
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainFourthFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.model.MainMessageEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@MainActivity)

        main_check1.performClick()
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)
        main_check4.setOnCheckedChangeListener(this)

        main_check4.setOnTouchListener { _, event ->
            if (!getBoolean("isLogin")) {
                if (event.action == MotionEvent.ACTION_UP) startActivity(LoginActivity::class.java)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            R.id.main_check3 -> MainThirdFragment()
            R.id.main_check4 -> MainFourthFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 4
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.first_venue ->   startActivity(VenueActivity::class.java)
            R.id.first_culture -> startActivity(CultureActivity::class.java)
            R.id.second_img ->    startActivity(UnionActivity::class.java)
            R.id.second_yuan ->   startActivity(RemoteActivity::class.java)
            R.id.second_fei ->    startActivity(DeliverActivity::class.java)
            R.id.second_wang ->   startActivity(ArtActivity::class.java)
            R.id.second_zhuan ->  startActivity(SpecialActivity::class.java)
            R.id.second_qun ->    startActivity(SocietyActivity::class.java)
            R.id.second_zhi ->    startActivity(VolunteerActivity::class.java)
        }
    }

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                toast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        EventBus.getDefault().unregister(this@MainActivity)
        super.onBackPressed()
    }

    @Subscribe
    fun onMessageEvent(event: MainMessageEvent) {
        when(event.name) {
            "退出登录" -> main_check1.performClick()
            "异地登录" -> {
                main_check1.performClick()
                window.decorView.postDelayed({
                    val intent = Intent(baseContext, LoginActivity::class.java)
                    intent.putExtra("offLine", true)
                    startActivity(intent)
                }, 300)
            }
        }
    }
}
