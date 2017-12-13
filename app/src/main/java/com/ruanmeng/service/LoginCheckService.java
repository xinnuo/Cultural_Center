package com.ruanmeng.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruanmeng.share.BaseHttp;
import com.ruanmeng.utils.PreferencesUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-03-20 16:27
 */

public class LoginCheckService extends Service {

    private Timer mTimeStampTimer;
    private TimerTask mTimeStampTimerTask;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getTimestamp();
        }
    };

    private final IBinder binder = new TimeStampBinder(); //绑定器

    public class TimeStampBinder extends Binder {
        public LoginCheckService getService() {
            return LoginCheckService.this;
        }

        public Timer getTimeStampTimer() {
            return mTimeStampTimer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTimeStampTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setTimeStampTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setTimeStampTimer() {
        if (mTimeStampTimer == null) {
            mTimeStampTimer = new Timer();
            mTimeStampTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };

            mTimeStampTimer.schedule(mTimeStampTimerTask, 0, 5 * 1000);
        }
    }

    /**
     * 获取系统时间戳
     */
    public void getTimestamp() {
        OkGo.<String>post(BaseHttp.check_userdevice)
                .headers("token", PreferencesUtils.getString(this, "token"))
                .params("deviceId", "")
                .execute(new StringNoDialogCallback(this) {

                    @Override
                    public void onSuccessResponse(Response<String> response, String msg, String msgCode) {

                    }

                });
    }

    @Override
    public void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        if (mTimeStampTimerTask != null) {
            mTimeStampTimerTask.cancel();
            mTimeStampTimerTask = null;
        }
        if (mTimeStampTimer != null) {
            mTimeStampTimer.cancel();
            mTimeStampTimer = null;
        }
        super.onDestroy();
    }

}
