/**
 * created by 小卷毛, 2017/02/20
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.maning.mndialoglibrary.MProgressDialog;
import com.ruanmeng.cultural_center.R;
import com.ruanmeng.model.CityData;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-10-12 17:43
 */
public class DialogHelper {

    @SuppressLint("StaticFieldLeak")
    private static MProgressDialog mMProgressDialog;

    private DialogHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showDialog(Context context) {
        if (mMProgressDialog == null) {
            mMProgressDialog = new MProgressDialog.Builder(context)
                    .setCancelable(true)
                    .isCanceledOnTouchOutside(false)
                    .setDimAmount(0.5f)
                    .build();
        }

        mMProgressDialog.show();
    }

    public static void dismissDialog() {
        if (mMProgressDialog != null && mMProgressDialog.isShowing())
            mMProgressDialog.dismiss();
    }

    public static void showCameraDialog(
            final Context context,
            final CameraCallBack callBack) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_info_camera, null);

                TextView tv_ce = view.findViewById(R.id.tv_camera_ce);
                TextView tv_pai = view.findViewById(R.id.tv_camera_pai);
                TextView tv_cancel = view.findViewById(R.id.tv_camera_cancel);

                tv_ce.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.doWork("相册");
                    }
                });
                tv_pai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.doWork("拍照");
                    }
                });
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
            }
        };

        dialog.show();
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String btnText,
            final HintCallBack msgCallBack) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.content(content)
                .title(title)
                .btnText(btnText)
                .btnNum(1)
                .btnTextColor(context.getResources().getColor(R.color.colorAccent))
                .showAnim(new BounceTopEnter())
                .show();
        materialDialog.setOnBtnClickL(
                new OnBtnClickL() { //left btn click listener
                    @Override
                    public void onBtnClick() {
                        materialDialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String left,
            final String right,
            final HintCallBack msgCallBack) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(content)
                .title(title)
                .btnText(left, right)
                .btnTextColor(
                        context.getResources().getColor(R.color.black),
                        context.getResources().getColor(R.color.colorAccent))
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String left,
            final String right,
            boolean isOutDismiss,
            final HintCallBack msgCallBack) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(content)
                .title(title)
                .btnText(left, right)
                .btnTextColor(
                        context.getResources().getColor(R.color.black),
                        context.getResources().getColor(R.color.colorAccent))
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCanceledOnTouchOutside(isOutDismiss);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showItemDialog(
            final Context context,
            final String title,
            final List<String> items,
            final ItemCallBack callBack) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loopView;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_select_item, null);

                TextView tv_title = view.findViewById(R.id.tv_dialog_select_title);
                TextView tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle);
                TextView tv_ok = view.findViewById(R.id.tv_dialog_select_ok);
                loopView = view.findViewById(R.id.lv_dialog_select_loop);

                tv_title.setText(title);
                loopView.setTextSize(14f);
                loopView.setDividerColor(context.getResources().getColor(R.color.divider));
                loopView.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.doWork(loopView.getSelectedItem(), items.get(loopView.getSelectedItem()));
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loopView.setItems(items);
            }

        };

        dialog.show();
    }

    public static void showAddressDialog(
            final Context context,
            final List<CityData> list_province,
            final List<CityData> list_city,
            final List<CityData> list_district,
            final AddressCallBack callBack) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView province, city, district;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_select_address, null);

                TextView tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle);
                TextView tv_ok = view.findViewById(R.id.tv_dialog_select_ok);
                province = view.findViewById(R.id.lv_dialog_select_province);
                city = view.findViewById(R.id.lv_dialog_select_city);
                district = view.findViewById(R.id.lv_dialog_select_district);

                province.setTextSize(14f);
                province.setDividerColor(context.getResources().getColor(R.color.divider));
                province.setNotLoop();
                city.setTextSize(14f);
                city.setDividerColor(context.getResources().getColor(R.color.divider));
                city.setNotLoop();
                district.setTextSize(14f);
                district.setDividerColor(context.getResources().getColor(R.color.divider));
                district.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.doWork(
                                province.getSelectedItem(),
                                city.getSelectedItem(),
                                district.getSelectedItem());
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                List<String> provinces = new ArrayList<>();
                List<String> cities = new ArrayList<>();
                final List<String> districts = new ArrayList<>();

                for (CityData item : list_province) provinces.add(item.getAreaName());
                for (CityData item : list_city) cities.add(item.getAreaName());
                for (CityData item : list_district) districts.add(item.getAreaName());

                if (provinces.size() > 0) province.setItems(provinces);
                if (cities.size() > 0) city.setItems(cities);
                if (districts.size() > 0) district.setItems(districts);

                province.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        callBack.getCities(city, district, province.getSelectedItem());
                    }
                });
                city.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        callBack.getDistricts(district, city.getSelectedItem());
                    }
                });
            }

        };

        dialog.show();
    }

    public interface AddressCallBack {
        void doWork(int pos_province, int pos_city, int pos_district);

        void getCities(LoopView loopView, LoopView loopView2, int pos);

        void getDistricts(LoopView loopView, int pos);
    }

    public interface ItemCallBack {
        void doWork(int position, String name);
    }

    public interface HintCallBack {
        void doWork();
    }

    public interface CameraCallBack {
        void doWork(String name);
    }
}
