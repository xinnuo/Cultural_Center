package com.ruanmeng.cultural_center

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DeviceHelper
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        transparentStatusBar(false)
        init_title("登录")
    }

    override fun init_title() {
        super.init_title()
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if (getString("mobile").isNotEmpty()) {
            et_name.setText(getString("mobile"))
            et_name.setSelection(et_name.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {

            toast("当前账户在其他设备登录")

            clearData()
            ActivityStack.getScreenManager().popAllActivityExcept(MainActivity::class.java, LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_login -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入手机号！")
                    return
                }

                if (et_pwd.text.isBlank()) {
                    et_pwd.requestFocus()
                    toast("请输入6~20位密码！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    toast("手机号码格式错误，请重新输入!")
                    return
                }
                if (et_pwd.text.length < 6) {
                    toast("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.login_sub)
                        .tag(this@LoginActivity)
                        .params("accountName", et_name.text.toString())
                        .params("password", et_pwd.text.toString())
                        .params("loginType", "mobile")
                        .params("deviceId", DeviceHelper.getDeviceIdIMEI(baseContext))
                        .execute(object : StringDialogCallback(this@LoginActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putBoolean("isLogin", true)
                                putString("mobile", obj.optString("mobile"))
                                putString("token", obj.optString("token"))

                                ActivityStack.getScreenManager().popActivities(this@LoginActivity::class.java)
                            }

                        })
            }
            R.id.tv_sign ->   startActivity(RegisterActivity::class.java)
            R.id.tv_forget -> startActivity(ForgetActivity::class.java)
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putBoolean("voulunteer", false)
        putString("hotline", "")
        putString("nickName", "")
        putString("sex", "")
        putString("token", "")
        putString("userhead", "")
    }
}
