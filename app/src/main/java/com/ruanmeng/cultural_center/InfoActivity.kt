package com.ruanmeng.cultural_center

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.PictureFileUtils.getPath
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_info.*
import org.json.JSONObject
import java.io.File
import java.util.ArrayList

class InfoActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        init_title("个人资料")
    }

    override fun onStart() {
        super.onStart()

        info_name.setRightString(getString("nickName"))
    }

    override fun init_title() {
        super.init_title()
        info_img_ll.setOnClickListener(this@InfoActivity)
        info_name.setOnClickListener(this@InfoActivity)
        bt_save.setOnClickListener(this@InfoActivity)

        loadUserHead(getString("userhead"))
    }

    private fun loadUserHead(path: String) {
        GlideApp.with(this@InfoActivity)
                .load(BaseHttp.baseImg + path)
                .placeholder(R.mipmap.icon_touxiang) // 等待时的图片
                .error(R.mipmap.icon_touxiang)       // 加载失败的图片
                .dontAnimate()
                .into(info_img)
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

    private fun getData(path: String) {
        OkGo.post<String>(BaseHttp.userinfo_uploadhead_sub)
                .tag(this@InfoActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("img", File(path))
                .execute(object : StringDialogCallback(this@InfoActivity) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        toast(msg)
                        val userhead = JSONObject(response.body()).getString("object")
                        putString("userhead", userhead)
                        loadUserHead(userhead)
                    }

                })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.info_img_ll -> {
                DialogHelper.showCameraDialog(baseContext) { name ->
                    when(name) {
                        "相册" -> {
                            PictureSelector.create(this@InfoActivity)
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
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    // 是否可预览图片 true or false
                                    .previewImage(true)
                                    // 是否可预览视频 true or false
                                    .previewVideo(false)
                                    // 是否显示拍照按钮 true or false
                                    .isCamera(false)
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
                        "拍照" -> {
                            PictureSelector.create(this@InfoActivity)
                                    .openCamera(PictureMimeType.ofImage())
                                    .theme(R.style.picture_customer_style)
                                    .maxSelectNum(1)
                                    .minSelectNum(1)
                                    .selectionMode(PictureConfig.MULTIPLE)
                                    .previewImage(true)
                                    .previewVideo(false)
                                    .isCamera(false)
                                    .isZoomAnim(true)
                                    .setOutputCameraPath(Const.SAVE_FILE)
                                    .compress(true)
                                    .glideOverride(160, 160)
                                    .enableCrop(true)
                                    .withAspectRatio(1, 1)
                                    .hideBottomControls(true)
                                    .freeStyleCropEnabled(false)
                                    .circleDimmedLayer(false)
                                    .showCropFrame(true)
                                    .showCropGrid(true)
                                    .isGif(false)
                                    .openClickSound(false)
                                    .selectionMedia(selectList.apply { clear() })
                                    .previewEggs(true)
                                    .minimumCompressSize(100)
                                    .forResult(PictureConfig.CHOOSE_REQUEST)
                        }
                    }
                }
            }
            R.id.info_name -> startActivity(NicknameActivity::class.java)
            R.id.bt_save -> { }
        }
    }
}
