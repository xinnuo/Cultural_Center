package com.ruanmeng.cultural_center

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
import com.ruanmeng.model.BookData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import com.ruanmeng.view.FullyLinearLayoutManager
import kotlinx.android.synthetic.main.activity_book_edit.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class BookEditActivity : BaseActivity() {

    private lateinit var list: ArrayList<BookData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_edit)
        init_title("预约申请信息")

        @Suppress("UNCHECKED_CAST")
        list = intent.getSerializableExtra("list") as ArrayList<BookData>
        (book_edit_list.adapter as SlimAdapter).updateData(list)
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(24))
        et_tel.setText(getString("mobile"))
        et_tel.setSelection(et_tel.text.length)

        book_edit_list.apply {
            layoutManager = FullyLinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<BookData>(R.layout.item_book_edit_list) { data, injector ->
                        injector.text(R.id.item_book_day, data.thatDay)
                        injector.text(R.id.item_book_time, data.startTime + "~" + data.endtime)

                                .clicked(R.id.item_book_del) {
                                    list.remove(data)
                                    (book_edit_list.adapter as SlimAdapter).notifyDataSetChanged()
                                }
                    }
                    .attachTo(book_edit_list)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.book_submit -> {
                if (list.isEmpty()) {
                    toast("请选择预约时间！")
                    return
                }

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

                if (et_content.text.isBlank()) {
                    et_content.requestFocus()
                    toast("请输入预约用途！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString())) {
                    toast("手机号格式不正确， 请重新输入！")
                    et_tel.setText("")
                    et_tel.requestFocus()
                    return
                }

                val strBuilder = StringBuilder()
                list.filter { it.isCheck }.forEach { strBuilder.append(it.reservetimeitemId).append(",") }

                OkGo.post<String>(BaseHttp.hall_reserve)
                        .tag(this@BookEditActivity)
                        .headers("token", getString("token"))
                        .params("stadiumId", intent.getStringExtra("venueId"))
                        .params("hallId", intent.getStringExtra("hallId"))
                        .params("name", et_name.text.trim().toString())
                        .params("tel", et_tel.text.trim().toString())
                        .params("reserveUse", et_content.text.trim().toString())
                        .params("rids", strBuilder.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                startActivity(BookDoneActivity::class.java)
                                onBackPressed()
                            }

                        })
            }
        }
    }
}
