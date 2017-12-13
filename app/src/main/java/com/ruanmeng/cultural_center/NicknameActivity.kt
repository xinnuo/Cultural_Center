package com.ruanmeng.cultural_center

import android.os.Bundle
import android.text.InputFilter
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        init_title("昵称")
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))

        if (getString("nickName").isNotEmpty()) {
            et_name.setText(getString("nickName"))
            et_name.setSelection(et_name.text.length)
        }

        bt_sure.setOnClickListener {
            if (et_name.text.toString().isBlank()) {
                toast("请输入昵称")
                return@setOnClickListener
            }

            if (et_name.text.toString() == getString("nickName")) {
                toast("未做任何修改")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.nickName_change_sub)
                    .tag(this@NicknameActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("nickName", et_name.text.toString())
                    .execute(object : StringDialogCallback(this@NicknameActivity) {
                        /*{
                            "msg": "更新成功",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast(msg)
                            putString("nickName", et_name.text.toString())

                            ActivityStack.getScreenManager().popActivities(this@NicknameActivity::class.java)
                        }

                    })
        }
    }
}
