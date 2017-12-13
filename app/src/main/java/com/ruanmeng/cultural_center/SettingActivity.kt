package com.ruanmeng.cultural_center

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.ruanmeng.base.*
import com.ruanmeng.model.MainMessageEvent
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus


class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_feedback.setOnClickListener { startActivity(FeedbackActivity::class.java) }
        setting_problem.setOnClickListener  {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "常见问题")
            startActivity(intent)
        }
        setting_about.setOnClickListener    {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "关于我们")
            startActivity(intent)
        }
        setting_password.setOnClickListener { startActivity(PasswordActivity::class.java) }
        setting_tel.setOnClickListener      {
            AlertDialog.Builder(this)
                    .setTitle("客服电话")
                    .setMessage("拨打客服电话 " + getString("hotline"))
                    .setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()

                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("hotline")))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
        bt_quit.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("退出") { dialog, _ ->
                        dialog.dismiss()

                        putBoolean("isLogin", false)
                        putBoolean("voulunteer", false)
                        putString("hotline", "")
                        putString("nickName", "")
                        putString("sex", "")
                        putString("token", "")
                        putString("userhead", "")

                        EventBus.getDefault().post(MainMessageEvent("退出登录"))
                        onBackPressed()
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
    }
}
