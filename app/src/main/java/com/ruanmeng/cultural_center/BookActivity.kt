package com.ruanmeng.cultural_center

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.widget.CheckBox
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.ruanmeng.adapter.EventDecorator
import com.ruanmeng.base.*
import com.ruanmeng.model.BookData
import com.ruanmeng.model.BookModel
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.HtmlUtil
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_book.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*
import kotlin.collections.ArrayList

class BookActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private val list_times = ArrayList<BookData>()
    private val list_check = ArrayList<BookData>()

    private var hallId = ""
    private var oneDay = TimeHelper.getInstance().stringDateShort

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        init_title("预约")

        getTabData()
    }

    override fun init_title() {
        super.init_title()
        book_tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                hallId = list[tab.position].hallId

                OkGo.getInstance().cancelTag(this@BookActivity)

                window.decorView.postDelayed({
                    runOnUiThread {
                        getDetailData()
                        getData()
                    }
                }, 300)
            }

        })

        book_calendar.setBeforeDayClickable(false)
        book_calendar.setOnDateChangedListener { widget, date, selected ->
            oneDay = TimeHelper.getInstance().dateToStr(date.date)

            if (hallId.isNotEmpty()) getData()
        }
        book_calendar.setSelectedDate(Calendar.getInstance().time)

        book_list.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 2)
            adapter = SlimAdapter.create()
                    .register<BookData>(R.layout.item_book_grid) { data, injector ->
                        injector.text(R.id.item_book_check, data.startTime + "~" + data.endtime)

                                .with<CheckBox>(R.id.item_book_check, { view ->
                                    view.setOnCheckedChangeListener { _, isChecked ->
                                        data.isCheck = isChecked
                                        if (isChecked) {
                                            if (!list_check.contains(data)) list_check.add(data)
                                        } else {
                                            if (list_check.contains(data)) list_check.remove(data)
                                        }
                                    }
                                    view.isChecked = data.isCheck
                                })
                    }
                    .attachTo(book_list)
        }
    }

    private fun getTabData() {
        OkGo.post<CommonModel>(BaseHttp.hall_list)
                .tag(this@BookActivity)
                .params("venueId", intent.getStringExtra("venueId"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.addItems(response.body().hallList)
                        for (item in list) book_tab.addTab(book_tab.newTab().setText(item.hallName))
                    }

                })
    }

    private fun getDetailData() {
        OkGo.post<BookModel>(BaseHttp.hall_details)
                .tag(this@BookActivity)
                .params("hallId", hallId)
                .execute(object : JacksonDialogCallback<BookModel>(baseContext, BookModel::class.java) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<BookModel>) {
                        val obj = response.body().hallDetails

                        if (obj != null) {
                            book_square.text = "${obj.hallArea}㎡"
                            book_name.text = obj.contacts
                            book_num.text = "${obj.galleryful}人"
                            book_tel.text = obj.tel
                            book_she.text = obj.hallFacility
                            book_money.text = obj.expenses

                            Thread {
                                val str = HtmlUtil.delHTMLTag(obj.hallIntroduce) //耗时操作
                                runOnUiThread { book_info.text = str }
                            }.start()
                        }
                    }

                })
    }

    override fun getData() {
        OkGo.post<BookModel>(BaseHttp.hallReserve_details)
                .tag(this@BookActivity)
                .params("hallId", hallId)
                .params("oneDay", oneDay)
                .execute(object : JacksonDialogCallback<BookModel>(baseContext, BookModel::class.java, true) {

                    override fun onSuccess(response: Response<BookModel>) {
                        list_times.clear()
                        list_times.addItems(response.body().couldTimeQuantum)

                        (book_list.adapter as SlimAdapter).updateData(list_times).notifyDataSetChanged()

                        val list_days = ArrayList<BookData>()
                        val list_items = ArrayList<CalendarDay>()
                        list_days.addItems(response.body().couldDay)

                        list_days.forEach {
                            val calendar = Calendar.getInstance()
                            calendar.time = TimeHelper.getInstance().strToDate(it.thatDay)
                            list_items.add(CalendarDay.from(calendar))
                        }

                        @Suppress("DEPRECATION")
                        book_calendar.addDecorator(EventDecorator(resources.getColor(R.color.colorAccent), list_items))
                    }

                })
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.book_submit -> {
                if (!getBoolean("isLogin")) {
                    startActivity(LoginActivity::class.java)
                    return
                }

                if (list_check.isEmpty()) {
                    toast("请选择预约时间！")
                    return
                }

                intent.setClass(baseContext, BookEditActivity::class.java)
                intent.putExtra("hallId", hallId)
                intent.putExtra("list", list_check)
                startActivity(intent)

                ActivityStack.getScreenManager().popActivities(this@BookActivity::class.java)
            }
        }
    }
}
