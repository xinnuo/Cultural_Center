package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_forget.*
import org.json.JSONObject

class ForgetActivity : BaseActivity() {

    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        init_title("找回密码")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        if (et_tel.text.isBlank()) {
            et_tel.requestFocus()
            toast("请输入手机号！")
            return
        }

        val phone = et_tel.text.toString().trim()

        when (v.id) {
            R.id.bt_yzm -> {
                if (!CommonUtil.isMobileNumber(phone)) {
                    toast("手机号格式不正确！")
                    return
                }

                getMobileRequest(phone)
            }
            R.id.bt_submit -> {
                if (et_yzm.text.isBlank()) {
                    et_yzm.requestFocus()
                    toast("请输入验证码！")
                    return
                }

                if (et_pwd.text.isBlank()) {
                    et_pwd.requestFocus()
                    toast("请输入6~20位新密码！")
                    return
                }

                if (!CommonUtil.isMobileNumber(phone)) {
                    toast("手机号格式不正确！")
                    return
                }

                if (phone != mTel) {
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

                OkGo.post<String>(BaseHttp.pwd_forget_sub)
                        .tag(this@ForgetActivity)
                        .params("mobile", phone)
                        .params("smscode", et_yzm.text.toString())
                        .params("newpwd", et_pwd.text.toString())
                        .execute(object : StringDialogCallback(this@ForgetActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@ForgetActivity::class.java)
                            }

                        })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getMobileRequest(tel: String) {
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

        OkGo.post<String>(BaseHttp.identify_getbyforget)
                .tag(this@ForgetActivity)
                .params("mobile", tel)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        YZM = JSONObject(response.body()).getString("object")
                        mTel = tel
                        if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                        bt_yzm.isClickable = false
                        time_count = 90
                        bt_yzm.post(thread)
                    }

                })
    }
}
