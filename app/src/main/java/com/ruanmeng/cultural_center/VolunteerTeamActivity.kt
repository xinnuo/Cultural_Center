package com.ruanmeng.cultural_center

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class VolunteerTeamActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venue)
        init_title("优秀志愿团队")
    }
}
