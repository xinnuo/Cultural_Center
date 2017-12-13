package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class RegisterActivity : BaseActivity() {

    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init_title("注册")
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_yzm -> {
                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    toast("请输入手机号！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    toast("手机号格式不正确！")
                    return
                }

                thread = Runnable {
                    bt_yzm.text = "$time_count 秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "获取验证码"
                        bt_yzm.isClickable = true
                        time_count = 90
                    }
                }

                OkGo.post<String>(BaseHttp.identify_get)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_tel.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_tel.text.toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                time_count = 90
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.register_deal -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "用户协议")
                startActivity(intent)
            }
            R.id.bt_sign -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入昵称！")
                    return
                }

                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    toast("请输入手机号！")
                    return
                }

                if (et_yzm.text.isBlank()) {
                    et_yzm.requestFocus()
                    toast("请输入验证码！")
                    return
                }

                if (et_pwd.text.isBlank()) {
                    et_pwd.requestFocus()
                    toast("请输入6~20位密码！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    toast("手机号格式不正确！")
                    return
                }

                if (et_tel.text.toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码！")
                    return
                }

                if (et_yzm.text.toString() != YZM) {
                    toast("验证码错误，请重新输入！")
                    return
                }

                if (et_pwd.text.length < 6) {
                    toast("新密码长度不少于6位")
                    return
                }

                if (!register_check.isChecked) {
                    toast("请同意用户注册协议！")
                    return
                }

                OkGo.post<String>(BaseHttp.register_sub)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_tel.text.toString())
                        .params("smscode", et_yzm.text.toString())
                        .params("password", et_pwd.text.toString())
                        .params("nickName", et_name.text.toString())
                        .params("loginType", "mobile")
                        .execute(object : StringDialogCallback(this@RegisterActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)

                                ActivityStack.getScreenManager().popActivities(this@RegisterActivity::class.java)
                            }

                        })
            }
        }
    }
}
