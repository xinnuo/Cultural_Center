package com.ruanmeng.cultural_center

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.BookData
import kotlinx.android.synthetic.main.activity_book_time.*
import net.idik.lib.slimadapter.SlimAdapter

class BookTimeActivity : BaseActivity() {

    private var list = ArrayList<BookData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_time)
        init_title("预约时间")

        @Suppress("UNCHECKED_CAST")
        list = intent.getSerializableExtra("list") as ArrayList<BookData>
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        book_list.load_Linear(baseContext)
        mAdapter = SlimAdapter.create()
                .register<BookData>(R.layout.item_time_list) { data, injector ->
                    injector.text(
                            R.id.item_time_time,
                            data.reserveStartDate + " - " + data.reserveEndDate.substring(11))
                            .visibility(R.id.item_time_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_time_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                }
                .attachTo(book_list)
    }
}
