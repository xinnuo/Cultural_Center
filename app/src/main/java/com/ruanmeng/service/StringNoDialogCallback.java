package com.ruanmeng.service;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.lzy.okgo.utils.OkLogger;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MToast;

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

    private MProgressDialog mMProgressDialog;
    private Context context;

    public StringNoDialogCallback(Context context) {
        this.context = context;
    }

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
                MToast.makeTextShort(context, msg).show();

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
    public void onFinish() {
        mMProgressDialog.dismiss();
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        Throwable exception = response.getException();
        if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
            MToast.makeTextShort(context, "网络连接失败，请连接网络！").show();
        } else if (exception instanceof SocketTimeoutException) {
            MToast.makeTextShort(context, "网络请求超时！").show();
        } else if (exception instanceof HttpException) {
            MToast.makeTextShort(context, "服务器发生未知错误！").show();
        } else if (exception instanceof StorageException) {
            MToast.makeTextShort(context, "SD卡不存在或没有权限！").show();
        } else {
            MToast.makeTextShort(context, "网络数据请求失败！").show();
        }
    }
}
