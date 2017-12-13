package com.ruanmeng.share;

import com.ruanmeng.cultural_center.BuildConfig;

/**
 * 项目名称：Cultural_Center
 * 创建人：小卷毛
 * 创建时间：2017-11-02 10:02
 */
public class BaseHttp {

    private static String baseUrl = BuildConfig.API_HOST;
    private static String baseIp = baseUrl + "/api";

    public static String baseImg = baseUrl + "/";

    public static String index_data = baseIp + "/index_data.rm";         //首页数据
    public static String activity_data = baseIp + "/activity_data.rm";   //文化活动（筛选）
    public static String activity_detil = baseIp + "/activity_detil.rm"; //活动详情
    public static String programa_list = baseIp + "/programa_list.rm";   //活动栏目
    public static String activity_apply = baseIp + "/activity_apply.rm"; //活动报名
    public static String get_district = baseIp + "/get_district.rm";     //地区筛选

    public static String venue_index = baseIp + "/venue_index.rm";                 //场馆
    public static String venue_details = baseIp + "/venue_details.rm";             //场馆详细信息
    public static String hall_list = baseIp + "/hall_list.rm";                     //场厅列表
    public static String hall_details = baseIp + "/hall_details.rm";               //场厅详细信息
    public static String hallReserve_details = baseIp + "/hallReserve_details.rm"; //场厅预约时间段信息
    public static String hall_reserve = baseIp + "/hall_reserve.rm";               //场厅预约
    public static String user_insertGrade = baseIp + "/user_insertGrade.rm";       //用户打分

    public static String cultural_index = baseIp + "/cultural_index.rm";     //文化馆联盟
    public static String cultural_detil = baseIp + "/cultural_detil.rm";     //文化馆详情
    public static String cultural_news = baseIp + "/cultural_news.rm";       //文化馆新闻
    public static String cultural_dept = baseIp + "/cultural_dept.rm";       //机构设置
    public static String dept_staff = baseIp + "/dept_staff.rm";             //各部门
    public static String staff_detail = baseIp + "/staff_detail.rm";         //成员信息

    public static String big_type = baseIp + "/big_type.rm";           //课程大分类
    public static String small_type = baseIp + "/small_type.rm";       //课程小分类
    public static String search_course = baseIp + "/search_course.rm"; //查询课程
    public static String course_detail = baseIp + "/course_detail.rm"; //课程详情

    public static String heritage_index = baseIp + "/heritage_index.rm";   //非遗首页
    public static String heritage_detail = baseIp + "/heritage_detail.rm"; //非遗详情
    public static String heritage_screen = baseIp + "/heritage_screen.rm"; //非遗级别分类

    public static String onlinehall_index = baseIp + "/onlinehall_index.rm";     //网上展厅首页
    public static String onlinehall_artType = baseIp + "/onlinehall_artType.rm"; //网上展厅分类
    public static String onlinehall_data = baseIp + "/onlinehall_data.rm";       //某分类下作品合集
    public static String onlinehall_detail = baseIp + "/onlinehall_detail.rm";   //某合集下所有作品

    public static String culture_index = baseIp + "/culture_index.rm";         //文化专题
    public static String culture_introduce = baseIp + "/culture_introduce.rm"; //某专题首页
    public static String culture_video = baseIp + "/culture_video.rm";         //专题视频
    public static String culture_img = baseIp + "/culture_img.rm";             //专题图片
    public static String culture_news = baseIp + "/culture_news.rm";           //专题资讯

    public static String club_index = baseIp + "/club_index.rm";         //群文社团
    public static String club_introduce = baseIp + "/club_introduce.rm"; //某群文社团首页
    public static String club_member = baseIp + "/club_member.rm";       //社团成员
    public static String club_news = baseIp + "/club_news.rm";           //社团资讯

    public static String volunteer_index = baseIp + "/volunteer_index.rm";                       //优秀志愿者
    public static String volunteer_detail = baseIp + "/volunteer_detail.rm";                     //志愿者详情
    public static String volunteer_all = baseIp + "/volunteer_all.rm";                           //全体志愿者
    public static String volunteer_applyForActivity = baseIp + "/volunteer_applyForActivity.rm"; //志愿者申请服务某活动
    public static String volunteer_activity = baseIp + "/volunteer_activity.rm";                 //志愿者活动
    public static String volunteer_slider_data = baseIp + "/volunteer_slider_data.rm";           //志愿者页面轮播图

    public static String news_detail = baseIp + "/news_detail.rm";               //新闻详情
    public static String collect_business = baseIp + "/collect_business.rm";     //收藏新闻、非遗、艺术品
    public static String news_type = baseIp + "/news_type.rm";                   //新闻类型
    public static String news_list = baseIp + "/news_list.rm";                   //新闻列表
    public static String cancel_collect_sub = baseIp + "/cancel_collect_sub.rm"; //取消收藏
    public static String is_collected = baseIp + "/is_collected.rm";             //判断是否收藏过

    public static String user_msg_data = baseIp + "/user_msg_data.rm";                   //用户个人资料
    public static String user_activity = baseIp + "/user_activity.rm";                   //我的活动
    public static String user_collect = baseIp + "/user_collect.rm";                     //我的收藏
    public static String user_appointVenue = baseIp + "/user_appointVenue.rm";           //我的场地预约
    public static String user_volunteerActivity = baseIp + "/user_volunteerActivity.rm"; //我是志愿者
    public static String be_volunteer = baseIp + "/be_volunteer.rm";                     //成为志愿者(提交)
    public static String volunteer_imgUpload = baseIp + "/volunteer_imgUpload.rm";       //志愿者头像上传(提交)
    public static String volunteer_edit = baseIp + "/volunteer_edit.rm";                 //修改志愿者信息(提交)

    public static String userinfo_uploadhead_sub = baseIp + "/userinfo_uploadhead_sub.rm"; //上传头像
    public static String nickName_change_sub = baseIp + "/nickName_change_sub.rm";         //修改用户名
    public static String password_change_sub = baseIp + "/password_change_sub.rm";         //修改密码(提交)
    public static String register_sub = baseIp + "/register_sub.rm";                       //注册提交
    public static String login_sub = baseIp + "/login_sub.rm";                             //登录
    public static String pwd_forget_sub = baseIp + "/pwd_forget_sub.rm";                   //找回密码
    public static String identify_get = baseIp + "/identify_get.rm";                       //获取验证码(注册)
    public static String identify_getbyforget = baseIp + "/identify_getbyforget.rm";       //获取验证码(忘记密码)

    public static String leave_message_sub = baseIp + "/leave_message_sub.rm"; //意见反馈(提交)
    public static String help_center = baseIp + "/help_center.rm";             //web公共接口
    public static String city1_data = baseIp + "/city1_data.rm";               //省
    public static String city2_data = baseIp + "/city2_data.rm";               //市
    public static String area_data = baseIp + "/area_data.rm";                 //区
    public static String check_userdevice = baseIp + "/check_userdevice.rm";   //检查用户是否在不同设备登录
}
