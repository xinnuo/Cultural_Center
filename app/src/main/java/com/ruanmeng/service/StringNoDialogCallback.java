package com.ruanmeng.service;

import android.content.Context;
import android.text.TextUtils;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.lzy.okgo.utils.OkLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-12-13 11:35
 */

public abstract class StringNoDialogCallback extends StringCallback {

    @Override
    public void onStart(Request<String, ? extends Request> request) { }

    @Override
    public void onSuccess(Response<String> response) {
        OkLogger.i(response.body());

        try {
            JSONObject obj = new JSONObject(response.body());

            String msgCode = obj.getString("msgcode");
            String msg = obj.isNull("msg") ? "请求成功！" : obj.getString("msg");

            if (!TextUtils.equals("100", msgCode)) {
                onSuccessResponseErrorCode(response, msg, msgCode);
            } else {
                onSuccessResponse(response, msg, msgCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onSuccessResponse(Response<String> response, String msg, String msgCode);

    public void onSuccessResponseErrorCode(Response<String> response, String msg, String msgCode) { }

    @Override
    public void onFinish() { }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        Throwable exception = response.getException();
        if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
            OkLogger.e("网络连接失败，请连接网络！");
        } else if (exception instanceof SocketTimeoutException) {
            OkLogger.e("网络请求超时！");
        } else if (exception instanceof HttpException) {
            OkLogger.e("服务器发生未知错误！");
        } else if (exception instanceof StorageException) {
            OkLogger.e("SD卡不存在或没有权限！");
        } else {
            OkLogger.e("网络数据请求失败！");
        }
    }
}
