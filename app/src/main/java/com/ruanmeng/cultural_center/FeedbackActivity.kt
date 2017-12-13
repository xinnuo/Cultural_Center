package com.ruanmeng.cultural_center

import android.os.Bundle
import android.text.InputFilter
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        init_title("意见反馈")
    }

    override fun init_title() {
        super.init_title()
        et_content.filters = arrayOf<InputFilter>(NameLengthFilter(300))

        feedback_submit.setOnClickListener {
            if (et_content.text.isBlank()) {
                toast("请输入反馈内容")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.leave_message_sub)
                    .tag(this@FeedbackActivity)
                    .isMultipart(true)
                    .headers("token", getString("token"))
                    .params("content", et_content.text.toString())
                    .execute(object : StringDialogCallback(baseContext) {

                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            toast(msg)
                            ActivityStack.getScreenManager().popActivities(this@FeedbackActivity::class.java)
                        }

                    })
        }
    }
}
