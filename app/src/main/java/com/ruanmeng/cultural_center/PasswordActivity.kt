package com.ruanmeng.cultural_center

import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putBoolean
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        init_title("修改密码")
    }

    override fun init_title() {
        super.init_title()
        bt_sure.setOnClickListener {
            if (et_old.text.isBlank()) {
                toast("请输入原密码！")
                et_old.requestFocus()
                return@setOnClickListener
            }

            if (et_new.text.isBlank()) {
                toast("请输入6~20位新密码！")
                et_new.requestFocus()
                return@setOnClickListener
            }

            if (et_confirm.text.isBlank()) {
                toast("请再次确认新密码！")
                et_confirm.requestFocus()
                return@setOnClickListener
            }

            if (et_old.text.length < 6) {
                toast("原密码长度不少于6位")
                return@setOnClickListener
            }

            if (et_new.text.length < 6) {
                toast("新密码长度不少于6位")
                return@setOnClickListener
            }

            if (et_confirm.text.length < 6) {
                toast("确认密码长度不少于6位")
                return@setOnClickListener
            }

            if (et_new.text.toString() != et_confirm.text.toString()) {
                toast("密码不一致，请重新输入")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.password_change_sub)
                    .tag(this@PasswordActivity)
                    .headers("token", getString("token"))
                    .params("oldPwd", et_old.text.toString())
                    .params("newPwd", et_new.text.toString())
                    .params("confirmPwd", et_confirm.text.toString())
                    .execute(object : StringDialogCallback(this@PasswordActivity) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast(msg)
                            putBoolean("isLogin", false)

                            ActivityStack.getScreenManager().popActivities(this@PasswordActivity::class.java)
                        }

                    })
        }
    }
}
