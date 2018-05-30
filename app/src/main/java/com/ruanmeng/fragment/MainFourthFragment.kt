package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.cultural_center.*
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_fourth.*
import kotlinx.android.synthetic.main.layout_title.*
import org.json.JSONObject

class MainFourthFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        this.view?.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main_fourth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun onStart() {
        super.onStart()

        if (getBoolean("isLogin")) getData()
    }

    override fun init_title() {
        iv_nav_back.visibility = View.INVISIBLE
        tv_nav_right.visibility = View.VISIBLE
        tv_nav_title.text = "我的"
        tv_nav_right.text = "设置"

        tv_nav_right.setOnClickListener   { startActivity(SettingActivity::class.java) }
        fourth_ll.setOnClickListener      { startActivity(InfoActivity::class.java) }
        fourth_img.setOnClickListener     { startActivity(InfoActivity::class.java) }
        fourth_act.setOnClickListener     { startActivity(CultureMineActivity::class.java) }
        fourth_book.setOnClickListener    { startActivity(BookMineActivity::class.java) }
        fourth_collect.setOnClickListener { startActivity(CollectActivity::class.java) }
        fourth_vol.setOnClickListener     { startActivity(VolunteerBecomeActivity::class.java) }
        fourth_vol.setOnClickListener     {
            if (getBoolean("voulunteer")) startActivity(VolunteerMineActivity::class.java)
            else startActivity(VolunteerBecomeActivity::class.java)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_msg_data)
                .tag(this@MainFourthFragment)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(this@MainFourthFragment.activity, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body()).getJSONObject("userMsg")

                        putBoolean("voulunteer", JSONObject(response.body()).optBoolean("voulunteer"))
                        putString("hotline", JSONObject(response.body()).optString("lxwm"))

                        putString("nickName", obj.optString("nickName"))
                        putString("sex", obj.optString("sex"))
                        putString("userhead", obj.optString("userhead"))

                        fourth_name.text = getString("nickName")
                        fourth_phone.text = getString("mobile")
                        fourth_vol.setLeftString(if (getBoolean("voulunteer")) "我是志愿者" else "我要成为志愿者")

                        if (fourth_img.getTag(R.id.fourth_img) == null) {
                            GlideApp.with(activity!!)
                                    .load(BaseHttp.baseImg + getString("userhead"))
                                    .placeholder(R.mipmap.icon_touxiang)
                                    .error(R.mipmap.icon_touxiang)
                                    .dontAnimate()
                                    .into(fourth_img)

                            fourth_img.setTag(R.id.fourth_img, getString("userhead"))
                        } else {
                            if (fourth_img.getTag(R.id.fourth_img) != getString("userhead")) {
                                GlideApp.with(activity!!)
                                        .load(BaseHttp.baseImg + getString("userhead"))
                                        .placeholder(R.mipmap.icon_touxiang)
                                        .error(R.mipmap.icon_touxiang)
                                        .dontAnimate()
                                        .into(fourth_img)

                                fourth_img.setTag(R.id.fourth_img, getString("userhead"))
                            }
                        }

                        if (getBoolean("voulunteer")) {
                            val data = JSONObject(response.body()).getJSONObject("volunteerDetail")
                            putString("address", data.getString("address"))
                            putString("age", data.getString("age"))
                            putString("city", data.getString("city"))
                            putString("district", data.getString("district"))
                            putString("education", data.getString("education"))
                            putString("idcard", data.getString("idcard"))
                            putString("name", data.getString("name"))
                            putString("politicsStatus", data.getString("politicsStatus"))
                            putString("province", data.getString("province"))
                            putString("sex_vol", data.getString("sex"))
                            putString("synopsis", data.getString("synopsis"))
                            putString("tel", data.getString("tel"))
                            putString("volunteerHead", data.getString("volunteerHead"))
                            putString("work", data.getString("work"))
                        } else {
                            putString("address", "")
                            putString("age", "")
                            putString("city", "")
                            putString("district", "")
                            putString("education", "")
                            putString("idcard", "")
                            putString("name", "")
                            putString("politicsStatus", "")
                            putString("province", "")
                            putString("sex_vol", "")
                            putString("synopsis", "")
                            putString("tel", "")
                            putString("volunteerHead", "")
                            putString("work", "")
                        }
                    }

                })
    }
}
