package com.ruanmeng.cultural_center

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CityData
import com.ruanmeng.model.CityModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NameLengthFilter
import com.weigan.loopview.LoopView
import kotlinx.android.synthetic.main.activity_volunteer_become.*
import org.json.JSONObject
import java.io.File
import java.util.*

class VolunteerBecomeActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()
    private var list_province = ArrayList<CityData>()
    private var list_city = ArrayList<CityData>()
    private var list_district = ArrayList<CityData>()

    private var sex = ""
    private var work = ""
    private var education = ""
    private var politicsStatus = ""
    private var volunteerHead = ""

    private var address = ""
    private var province = ""
    private var city = ""
    private var district = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_become)
        init_title(if (intent.getBooleanExtra("isModify", false)) "修改信息" else "成为志愿者")
    }

    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(10))

        vol_gender.setOnClickListener(this@VolunteerBecomeActivity)
        vol_address.setOnClickListener(this@VolunteerBecomeActivity)
        vol_job.setOnClickListener(this@VolunteerBecomeActivity)
        vol_edu.setOnClickListener(this@VolunteerBecomeActivity)
        vol_outlook.setOnClickListener(this@VolunteerBecomeActivity)
        vol_img.setOnClickListener(this@VolunteerBecomeActivity)
        bt_submit.setOnClickListener(this@VolunteerBecomeActivity)

        if (intent.getBooleanExtra("isModify", false)) {
            sex = getString("sex_vol")
            work = getString("work")
            education = getString("education")
            politicsStatus = getString("politicsStatus")
            volunteerHead = getString("volunteerHead")
            address = getString("address")
            province = getString("province")
            city = getString("city")
            district = getString("district")

            et_name.setText(getString("name"))
            et_name.setSelection(et_name.text.length)
            vol_gender.setRightString(when(getString("sex_vol")) {
                "0" -> "女"
                "1" -> "男"
                else -> ""
            })
            et_age.setText(getString("age"))
            et_card.setText(getString("idcard"))
            et_tel.setText(getString("tel"))
            vol_address.setRightString(getString("address"))
            vol_job.setRightString(when(getString("work")) {
                "1" -> "在职"
                "2" -> "待业"
                "3" -> "无业"
                else -> ""
            })
            vol_edu.setRightString(when(getString("education")) {
                "1" -> "小学"
                "2" -> "初中"
                "3" -> "高中"
                "5" -> "大学专科以上"
                "6" -> "硕士研究生以上"
                else -> ""
            })
            vol_outlook.setRightString(when(getString("politicsStatus")) {
                "0" -> "群众"
                "1" -> "团员"
                "2" -> "党员"
                else -> ""
            })
            et_content.setText(getString("synopsis"))
            loadUserHead(getString("volunteerHead"))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.vol_gender -> {
                DialogHelper.showItemDialog(baseContext, "选择性别", Arrays.asList("男", "女")) { position, name ->
                    vol_gender.setRightString(name)
                    sex = if (position == 0) "1" else "0"
                }
            }
            R.id.vol_address -> {
                showLoadingDialog()

                getProvince(object : ResultCallBack {

                    override fun doWork() {
                        cancelLoadingDialog()

                        DialogHelper.showAddressDialog(
                                baseContext,
                                list_province,
                                list_city,
                                list_district,
                                object : DialogHelper.AddressCallBack {

                                    override fun doWork(pos_province: Int, pos_city: Int, pos_district: Int) {
                                        address = list_province[pos_province].areaName + list_city[pos_city].areaName + list_district[pos_district].areaName

                                        province = list_province[pos_province].areaId
                                        city = list_city[pos_city].areaId
                                        district = list_district[pos_district].areaId

                                        vol_address.setRightString(address)
                                    }

                                    override fun getCities(loopView: LoopView, loopView2: LoopView, pos: Int) {
                                        getCity(list_province[pos].areaId, object : ResultCallBack {

                                            override fun doWork() {
                                                val cities = ArrayList<String>()
                                                val districts = ArrayList<String>()

                                                list_city.mapTo(cities) { it.areaName }
                                                list_district.mapTo(districts) { it.areaName }

                                                if (cities.size > 0) loopView.setItems(cities)
                                                if (districts.size > 0) {
                                                    loopView2.visibility = View.VISIBLE
                                                    loopView2.setItems(districts)
                                                } else loopView2.visibility = View.INVISIBLE
                                            }

                                        })
                                    }

                                    override fun getDistricts(loopView: LoopView, pos: Int) {
                                        getDistrict(list_city[pos].areaId, object : ResultCallBack {

                                            override fun doWork() {
                                                val districts = ArrayList<String>()
                                                list_district.mapTo(districts) { it.areaName }

                                                if (districts.size > 0) {
                                                    loopView.visibility = View.VISIBLE
                                                    loopView.setItems(districts)
                                                } else loopView.visibility = View.INVISIBLE
                                            }

                                        })
                                    }

                                })
                    }

                })
            }
            R.id.vol_job -> {
                DialogHelper.showItemDialog(baseContext, "从业状况", Arrays.asList("在职", "待业", "无业")) { position, name ->
                    vol_job.setRightString(name)
                    when (position) {
                        0 -> work = "1"
                        1 -> work = "2"
                        2 -> work = "3"
                    }
                }
            }
            R.id.vol_edu -> {
                DialogHelper.showItemDialog(baseContext, "最高学历", Arrays.asList("小学", "初中", "高中", "大学专科以上", "硕士研究生以上")) { position, name ->
                    vol_edu.setRightString(name)
                    when (position) {
                        0 -> education = "1"
                        1 -> education = "2"
                        2 -> education = "3"
                        3 -> education = "5"
                        4 -> education = "6"
                    }
                }
            }
            R.id.vol_outlook -> {
                DialogHelper.showItemDialog(baseContext, "政治面貌", Arrays.asList("群众", "团员", "党员")) { position, name ->
                    vol_outlook.setRightString(name)
                    politicsStatus = position.toString()
                }
            }
            R.id.vol_img -> {
                PictureSelector.create(this@VolunteerBecomeActivity)
                        // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .openGallery(PictureMimeType.ofImage())
                        // 主题样式(不设置则为默认样式)
                        .theme(R.style.picture_customer_style)
                        // 最大图片选择数量 int
                        .maxSelectNum(1)
                        // 最小选择数量 int
                        .minSelectNum(1)
                        // 每行显示个数 int
                        .imageSpanCount(4)
                        // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .selectionMode(PictureConfig.SINGLE)
                        // 是否可预览图片 true or false
                        .previewImage(true)
                        // 是否可预览视频 true or false
                        .previewVideo(false)
                        // 是否显示拍照按钮 true or false
                        .isCamera(true)
                        // 图片列表点击 缩放效果 默认true
                        .isZoomAnim(true)
                        // 自定义拍照保存路径,可不填
                        .setOutputCameraPath(Const.SAVE_FILE)
                        // 是否压缩 true or false
                        .compress(true)
                        // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .glideOverride(160, 160)
                        // 是否裁剪 true or false
                        .enableCrop(true)
                        // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .withAspectRatio(1, 1)
                        // 是否显示uCrop工具栏，默认不显示 true or false
                        .hideBottomControls(true)
                        // 压缩图片保存地址
                        .compressSavePath(cacheDir.absolutePath)
                        // 裁剪框是否可拖拽 true or false
                        .freeStyleCropEnabled(false)
                        // 是否圆形裁剪 true or false
                        .circleDimmedLayer(false)
                        // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                        .showCropFrame(true)
                        // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                        .showCropGrid(true)
                        // 是否显示gif图片 true or false
                        .isGif(false)
                        // 是否开启点击声音 true or false
                        .openClickSound(false)
                        // 是否传入已选图片 List<LocalMedia> list
                        .selectionMedia(selectList.apply { clear() })
                        // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .previewEggs(true)
                        // 小于100kb的图片不压缩
                        .minimumCompressSize(100)
                        // 结果回调onActivityResult code
                        .forResult(PictureConfig.CHOOSE_REQUEST)
            }
            R.id.bt_submit -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入姓名！")
                    return
                }

                if (sex.isBlank()) {
                    toast("请选择性别！")
                    return
                }

                if (et_age.text.isBlank()) {
                    et_age.requestFocus()
                    toast("请输入年龄！")
                    return
                }

                if (et_card.text.isBlank()) {
                    et_card.requestFocus()
                    toast("请输入身份证号码！")
                    return
                }

                if (et_tel.text.isBlank()) {
                    et_tel.requestFocus()
                    toast("请输入联系方式！")
                    return
                }

                if (address.isBlank()) {
                    toast("请选择地址！")
                    return
                }

                if (et_content.text.isBlank()) {
                    et_content.requestFocus()
                    toast("请输入简介！")
                    return
                }

                if (volunteerHead.isBlank()) {
                    toast("请上传个人头像！")
                    return
                }

                if (!CommonUtil.IDCardValidate(et_card.text.toString().trim())) {
                    toast("身份证号码错误，请重新输入！")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_tel.text.toString().trim())) {
                    toast("手机号码格式错误，请重新输入！")
                    return
                }

                OkGo.post<String>(if (intent.getBooleanExtra("isModify", false)) BaseHttp.volunteer_edit else BaseHttp.be_volunteer)
                        .tag(this@VolunteerBecomeActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("name", et_name.text.toString().trim())
                        .params("sex", sex)
                        .params("age", et_age.text.toString().trim())
                        .params("idcard", et_card.text.toString().trim())
                        .params("tel", et_tel.text.toString().trim())
                        .params("address", address)
                        .params("province", province)
                        .params("city", city)
                        .params("district", district)
                        .params("work", work)
                        .params("education", education)
                        .params("politicsStatus", politicsStatus)
                        .params("synopsis", et_content.text.toString().trim())
                        .params("volunteerHead", volunteerHead)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast("志愿者信息提交成功！")

                                putString("address", address)
                                putString("age", et_age.text.toString().trim())
                                putString("city", city)
                                putString("district", district)
                                putString("education", education)
                                putString("idcard", et_card.text.toString().trim())
                                putString("name", et_name.text.toString().trim())
                                putString("politicsStatus", politicsStatus)
                                putString("province", province)
                                putString("sex_vol", sex)
                                putString("synopsis", et_content.text.toString().trim())
                                putString("tel", et_tel.text.toString().trim())
                                putString("volunteerHead", volunteerHead)
                                putString("work", work)

                                ActivityStack.getScreenManager().popActivities(this@VolunteerBecomeActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    if (selectList[0].isCompressed) getData(selectList[0].compressPath)
                }
            }
        }
    }

    private fun loadUserHead(path: String) {
        GlideApp.with(this@VolunteerBecomeActivity)
                .load(BaseHttp.baseImg + path)
                .placeholder(R.mipmap.icon_add) // 等待时的图片
                .error(R.mipmap.icon_add)       // 加载失败的图片
                .dontAnimate()
                .into(vol_img)
    }

    private fun getData(path: String) {
        OkGo.post<String>(BaseHttp.volunteer_imgUpload)
                .tag(this@VolunteerBecomeActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("img", File(path))
                .execute(object : StringDialogCallback(baseContext) {

                    /* {"filePath":"upload/volunteer/9C66FF190A6A44BD9AAD2029F33F50AD.jpg","msgcode":100,"success":"y"} */
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        toast("头像上传成功！")
                        volunteerHead = JSONObject(response.body()).getString("filePath")
                        loadUserHead(volunteerHead)
                    }

                })
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<CityModel>(BaseHttp.city1_data)
                .tag(this@VolunteerBecomeActivity)
                .execute(object : JacksonDialogCallback<CityModel>(baseContext, CityModel::class.java) {

                    override fun onSuccess(response: Response<CityModel>) {
                        list_province.apply {
                            clear()
                            addItems(response.body().areas)
                        }
                    }

                    override fun onFinish() {
                        if (list_province.size > 0) getCity(list_province[0].areaId, callback)
                    }

                })
    }

    private fun getCity(id: String, callback: ResultCallBack) {
        OkGo.post<CityModel>(BaseHttp.city2_data)
                .tag(this@VolunteerBecomeActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CityModel>(baseContext, CityModel::class.java) {

                    override fun onSuccess(response: Response<CityModel>) {
                        list_city.apply {
                            clear()
                            addItems(response.body().areas)
                        }
                    }

                    override fun onFinish() {
                        if (list_city.size > 0) getDistrict(list_city[0].areaId, callback)
                    }

                })
    }

    private fun getDistrict(id: String, callback: ResultCallBack) {
        OkGo.post<CityModel>(BaseHttp.area_data)
                .tag(this@VolunteerBecomeActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CityModel>(baseContext, CityModel::class.java) {

                    override fun onSuccess(response: Response<CityModel>) {
                        list_district.apply {
                            clear()
                            addItems(response.body().rows)
                        }
                    }

                    override fun onFinish() {
                        callback.doWork()
                    }
                })
    }

    interface ResultCallBack {
        fun doWork()
    }
}
