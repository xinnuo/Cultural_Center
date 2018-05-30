package com.ruanmeng.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruanmeng.cultural_center.R;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.model.OnlineData;
import com.ruanmeng.model.RemoteData;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-03 10:39
 */

public class PopupWindowUtils {

    public static void showFilterPopWindow(
            final Context context,
            View anchor,
            final List<CommonData> list_qu,
            final List<CommonData> list_act,
            final int pos_qu,
            final int pos_time,
            final int pos_act,
            final int pos_status,
            final PopupWindowFilterCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_culture_filter, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        TextView tv_done = view.findViewById(R.id.filter_sure);
        TextView tv_reset = view.findViewById(R.id.filter_reset);
        View divider = view.findViewById(R.id.filter_outer);
        final TagFlowLayout tap_qu = view.findViewById(R.id.filter_qu);
        final TagFlowLayout tap_time = view.findViewById(R.id.filter_time);
        final TagFlowLayout tap_act = view.findViewById(R.id.filter_act);
        final TagFlowLayout tap_status = view.findViewById(R.id.filter_status);

        final TagAdapter<CommonData> adapter_qu;
        final TagAdapter<String> adapter_time;
        final TagAdapter<CommonData> adapter_act;
        final TagAdapter<String> adapter_status;

        tap_qu.setAdapter(adapter_qu = new TagAdapter<CommonData>(list_qu) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData item) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_qu, false);
                tv_name.setText(item.getAreaName());
                return tv_name;
            }
        });

        tap_time.setAdapter(adapter_time = new TagAdapter<String>(new String[]{"全部", "今天", "本周", "本月"}) {
            @Override
            public View getView(FlowLayout parent, int position, String str) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_time, false);
                tv_name.setText(str);
                return tv_name;
            }
        });

        tap_act.setAdapter(adapter_act = new TagAdapter<CommonData>(list_act) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData item) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_act, false);
                tv_name.setText(item.getProgramaName());
                return tv_name;
            }
        });

        tap_status.setAdapter(adapter_status = new TagAdapter<String>(new String[]{"全部", "活动未开始", "活动报名", "报名截止", "活动进行中", "活动结束"}) {
            @Override
            public View getView(FlowLayout parent, int position, String str) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_status, false);
                tv_name.setText(str);
                return tv_name;
            }
        });

        adapter_qu.setSelectedList(pos_qu);
        adapter_time.setSelectedList(pos_time + 1);
        adapter_act.setSelectedList(pos_act);
        adapter_status.setSelectedList(pos_status + 1);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_qu.notifyDataChanged();
                adapter_time.notifyDataChanged();
                adapter_act.notifyDataChanged();
                adapter_status.notifyDataChanged();

                adapter_qu.setSelectedList(0);
                adapter_time.setSelectedList(0);
                adapter_act.setSelectedList(0);
                adapter_status.setSelectedList(0);
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                String districtId = "";
                String dateId = "";
                String programaId = "";
                String stypeId = "";

                int pos1 = pos_qu;
                int pos2 = pos_time;
                int pos3 = pos_act;
                int pos4 = pos_status;

                for (Integer item : tap_qu.getSelectedList()) {
                    pos1 = item;
                    districtId = list_qu.get(item).getAreaId();
                }
                for (Integer item : tap_time.getSelectedList()) {
                    pos2 = item - 1;
                    if (item > 0) dateId = String.valueOf(item - 1);
                }
                for (Integer item : tap_act.getSelectedList()) {
                    pos3 = item;
                    programaId = list_act.get(item).getProgramaId();
                }
                for (Integer item : tap_status.getSelectedList()) {
                    pos4 = item - 1;
                    if (item > 0) stypeId = String.valueOf(item - 2);
                }

                callBack.doWork(districtId, dateId, programaId, stypeId, pos1, pos2, pos3, pos4);
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showDeliverFilterPopWindow(
            final Context context,
            View anchor,
            final List<CommonData> list_type,
            final List<CommonData> list_level,
            final int pos_type,
            final int pos_level,
            final PopupWindowDeliverFilterCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_deliver_filter, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        TextView tv_done = view.findViewById(R.id.filter_sure);
        TextView tv_reset = view.findViewById(R.id.filter_reset);
        View divider = view.findViewById(R.id.filter_outer);
        final TagFlowLayout tap_type = view.findViewById(R.id.filter_type);
        final TagFlowLayout tap_level = view.findViewById(R.id.filter_level);

        final TagAdapter<CommonData> adapter_type;
        final TagAdapter<CommonData> adapter_level;

        tap_type.setAdapter(adapter_type = new TagAdapter<CommonData>(list_type) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData item) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_type, false);
                tv_name.setText(item.getHeritageTypeName());
                return tv_name;
            }
        });

        tap_level.setAdapter(adapter_level = new TagAdapter<CommonData>(list_level) {
            @Override
            public View getView(FlowLayout parent, int position, CommonData item) {
                LayoutInflater mInflater = LayoutInflater.from(context);
                TextView tv_name = (TextView) mInflater.inflate(R.layout.item_filter_grid, tap_level, false);
                tv_name.setText(item.getHeritagelevelName());
                return tv_name;
            }
        });

        adapter_type.setSelectedList(pos_type);
        adapter_level.setSelectedList(pos_level);

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_type.notifyDataChanged();
                adapter_level.notifyDataChanged();

                adapter_type.setSelectedList(0);
                adapter_level.setSelectedList(0);
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                String heritageTypeId = "";
                String heritageLevelId = "";

                int pos1 = pos_type;
                int pos2 = pos_level;

                for (Integer item : tap_type.getSelectedList()) {
                    pos1 = item;
                    heritageTypeId = list_type.get(item).getHeritageTypeId();
                }
                for (Integer item : tap_level.getSelectedList()) {
                    pos2 = item;
                    heritageLevelId = list_level.get(item).getHeritagelevelId();
                }

                callBack.doWork(heritageTypeId, heritageLevelId, pos1, pos2);
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setClippingEnabled(false);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showTypePopWindow(
            final Context context,
            View anchor,
            final List<RemoteData> items,
            final String name_first,
            final String name_second,
            final PopupWindowTypeCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_remote_type, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        final List<RemoteData> item_second = new ArrayList<>();

        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        View divider = view.findViewById(R.id.v_pop_divider);
        final RadioGroup rg_left = view.findViewById(R.id.rg_pop_left);
        final RadioGroup rg_right = view.findViewById(R.id.rg_pop_right);
        Button bt_ok = view.findViewById(R.id.popu_sure);

        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<RemoteData> mlist = (List<RemoteData>) msg.obj;

                item_second.clear();
                if (mlist != null) item_second.addAll(mlist);

                for (RemoteData item : item_second) {
                    RadioButton rb = new RadioButton(context);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                    rb.setLayoutParams(params);
                    rb.setTextAppearance(context, R.style.Font14_selector);
                    rb.setGravity(Gravity.CENTER);
                    rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rb.setText(item.getCourseTypeName());
                    rb.setId(item_second.indexOf(item));
                    if (item.getCourseTypeId().equals(name_second)) rb.setChecked(true);
                    rg_right.addView(rb);

                    RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                    View v = new View(context);
                    v.setLayoutParams(param);
                    v.setBackgroundResource(R.color.colorControlNormal);
                    rg_right.addView(v);
                }
            }
        };

        rg_left.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg_right.removeAllViews();
                rg_right.clearCheck();
                callBack.getSecondList(items.get(checkedId).getCourseTypeId(), handler);
            }
        });

        for (RemoteData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getCourseTypeName());
            rb.setId(items.indexOf(item));
            if (item.getCourseTypeId().equals(name_first)) rb.setChecked(true);
            rg_left.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg_left.addView(v);
        }

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                callBack.doWork(
                        rg_left.getCheckedRadioButtonId() < 0 ? null : items.get(rg_left.getCheckedRadioButtonId()),
                        rg_right.getCheckedRadioButtonId() < 0 ? null : item_second.get(rg_right.getCheckedRadioButtonId()));
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_vertical_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showOnlinePopWindow(
            final Context context,
            View anchor,
            final List<OnlineData> items,
            final String name_first,
            final String name_second,
            final PopupWindowOnlineeCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_remote_type, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8

        final List<OnlineData> item_second = new ArrayList<>();

        View divider_top = view.findViewById(R.id.v_pop_divider_top);
        View divider = view.findViewById(R.id.v_pop_divider);
        final RadioGroup rg_left = view.findViewById(R.id.rg_pop_left);
        final RadioGroup rg_right = view.findViewById(R.id.rg_pop_right);
        Button bt_ok = view.findViewById(R.id.popu_sure);

        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<OnlineData> mlist = (List<OnlineData>) msg.obj;

                item_second.clear();
                if (mlist != null) item_second.addAll(mlist);

                for (OnlineData item : item_second) {
                    RadioButton rb = new RadioButton(context);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                    rb.setLayoutParams(params);
                    rb.setTextAppearance(context, R.style.Font14_selector);
                    rb.setGravity(Gravity.CENTER);
                    rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rb.setText(item.getOnlineArtTypeName());
                    rb.setId(item_second.indexOf(item));
                    if (item.getOnlineArtTypeId().equals(name_second)) rb.setChecked(true);
                    rg_right.addView(rb);

                    RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                    View v = new View(context);
                    v.setLayoutParams(param);
                    v.setBackgroundResource(R.color.colorControlNormal);
                    rg_right.addView(v);
                }
            }
        };

        rg_left.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rg_right.removeAllViews();
                rg_right.clearCheck();
                callBack.getSecondList(items.get(checkedId).getOnlineHallId(), handler);
            }
        });

        for (OnlineData item : items) {
            RadioButton rb = new RadioButton(context);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font14_selector);
            rb.setGravity(Gravity.CENTER);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item.getOnlineHallName());
            rb.setId(items.indexOf(item));
            if (item.getOnlineHallId().equals(name_first)) rb.setChecked(true);
            rg_left.addView(rb);

            RadioGroup.LayoutParams param = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg_left.addView(v);
        }

        divider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                callBack.doWork(
                        rg_left.getCheckedRadioButtonId() < 0 ? null : items.get(rg_left.getCheckedRadioButtonId()),
                        rg_right.getCheckedRadioButtonId() < 0 ? null : item_second.get(rg_right.getCheckedRadioButtonId()));
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_vertical_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public interface PopupWindowFilterCallBack {
        void doWork(String districtId, String dateId, String programaId, String stypeId, int pos1, int pos2, int pos3, int pos4);
    }

    public interface PopupWindowDeliverFilterCallBack {
        void doWork(String heritageTypeId, String heritageLevelId, int pos1, int pos2);
    }

    public interface PopupWindowTypeCallBack {
        void getSecondList(String foreignKey, Handler handler);

        void doWork(RemoteData first, RemoteData second);
    }

    public interface PopupWindowOnlineeCallBack {
        void getSecondList(String foreignKey, Handler handler);

        void doWork(OnlineData first, OnlineData second);
    }
}
