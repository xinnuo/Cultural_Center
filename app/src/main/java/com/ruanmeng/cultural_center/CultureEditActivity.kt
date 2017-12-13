package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_culture_edit.*
import org.json.JSONObject

class CultureEditActivity : BaseActivity() {

    private var signUpLimit = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_edit)
        init_title("报名")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))
        et_tel.setText(getString("mobile"))
        et_tel.setSelection(et_tel.text.length)

        signUpLimit = intent.getStringExtra("signUpLimit").toInt()
        culture_address.text = intent.getStringExtra("address")
        culture_people.text = "活动人数：${intent.getStringExtra("peopleCount")}人"
        culture_time.text = intent.getStringExtra("time")
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.culture_remove -> {
                if (culture_account.text.toString() == "1") return

                val count = culture_account.text.toString().toInt()
                culture_account.text = (count - 1).toString()
            }
            R.id.culture_add -> {
                val count = culture_account.text.toString().toInt()
                if (count == signUpLimit) {
                    toast("单次最多可报名${signUpLimit}人")
                    return
                }

                culture_account.text = (count + 1).toString()
            }
            R.id.culture_submit -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入姓名！")
                    return
                }

                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    toast("请输入联系方式！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    toast("手机号格式不正确， 请重新输入！")
                    et_tel.setText("")
                    et_tel.requestFocus()
                    return
                }

                OkGo.post<String>(BaseHttp.activity_apply)
                        .tag(this@CultureEditActivity)
                        .headers("token", getString("token"))
                        .params("activityId", intent.getStringExtra("activityId"))
                        .params("activityCount", culture_account.text.toString())
                        .params("name", et_name.text.trim().toString())
                        .params("tel", et_tel.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val qrcode = JSONObject(response.body()).getString("qrcode")
                                intent.setClass(baseContext, CultureDoneActivity::class.java)
                                intent.putExtra("qrcode", qrcode)
                                startActivity(intent)

                                ActivityStack.getScreenManager().popActivities(this@CultureEditActivity::class.java)
                            }

                        })
            }
        }
    }
}
