package com.ruanmeng.cultural_center

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.showToast
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.RandomLength
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_culture_done.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CultureDoneActivity : BaseActivity() {

    private var mSaveFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_culture_done)
        init_title("报名")
    }

    override fun init_title() {
        super.init_title()

        val qrcode = intent.getStringExtra("qrcode")
        window.decorView.post {
            kotlin.run {
                val bitmap = Tools.strToBitmap(qrcode)
                if (bitmap != null) {
                    runOnUiThread { culture_done.setImageBitmap(bitmap) }
                }
            }
        }

        culture_done.setOnLongClickListener {
            if (mSaveFile == null) saveFile(getViewBitmap(culture_done))
            else showToast("图片保存至：${mSaveFile!!.absoluteFile}", Toast.LENGTH_LONG)

            return@setOnLongClickListener true
        }
    }

    /**
     * 根据view来生成bitmap图片，可用于截图功能
     */
    private fun getViewBitmap(v: View): Bitmap? {
        v.clearFocus()
        v.isPressed = false
        // 能画缓存就返回false
        val willNotCache = v.willNotCacheDrawing()
        v.setWillNotCacheDrawing(false)
        val color = v.drawingCacheBackgroundColor
        v.drawingCacheBackgroundColor = 0
        if (color != 0) v.destroyDrawingCache()
        v.buildDrawingCache()
        val cacheBitmap = v.drawingCache ?: return null
        val bitmap = Bitmap.createBitmap(cacheBitmap)
        // Restore the view
        v.destroyDrawingCache()
        v.setWillNotCacheDrawing(willNotCache)
        v.drawingCacheBackgroundColor = color
        return bitmap
    }

    /**
     * 保存Bitmap图片为本地文件
     */
    private fun saveFile(bitmap: Bitmap?) {
        val dir = File(Environment.getExternalStorageDirectory().absolutePath, Const.SAVE_FILE)
        if (!dir.exists()) dir.mkdirs()
        mSaveFile = File(dir, "/qrcode_" + RandomLength.getRandomString(6) + ".jpg")
        try {
            if (!mSaveFile!!.exists()) mSaveFile!!.createNewFile()
            val out = FileOutputStream(mSaveFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()

            showToast("图片保存至：${mSaveFile!!.absoluteFile}", Toast.LENGTH_LONG)

            // 保存图片到相册显示的方法（没有则只有重启后才有）
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(mSaveFile)
            intent.data = uri
            sendBroadcast(intent)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (mSaveFile == null)  saveFile(getViewBitmap(culture_done))

        ActivityStack.getScreenManager().popActivities(
                this@CultureDoneActivity::class.java,
                CultureDetailActivity::class.java)
    }
}
