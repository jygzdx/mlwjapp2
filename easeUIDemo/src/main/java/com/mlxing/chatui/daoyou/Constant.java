package com.mlxing.chatui.daoyou;

/**
 * Created by Administrator on 2016/3/1.
 */
public class Constant {
    //首页
//    public static String URL_HOME = "http://static.mlxing.com/index.html#/home";
    public static String URL_HOME = "http://static.mlxing.com/index.html#/players_list";

    /**
     * 微信授权后登陆接口
     */
    public static String URL_LOGIN = "https://weixin.mlxing.com/shop/wx_login?unionid=%s&channel=app";


    //首页API 玩家 玩群 首页
    public static String URL_WANJIA = "http://static.mlxing.com/index.html#/players_list";
    public static String URL_WNQUN = "http://static.mlxing.com/index.html#/player_crowd";
    public static String URL_SHOUYE = "http://static.mlxing.com/index.html#/home";

    //从自己服务器获取环信登陆账号和密码
//    public static String wxPost="http://124.173.70.55:11001/guide/v1/weixin/account/mobileClient/mobileLogin";
   // public static String wxPost = "http://wjapi.mlxing.com/guide/v1/weixin/account/mobileClient/mobileLogin";
    //    public static String wxPost="http://guide.chineseml.com/guide/v1/weixin/account/mobileClient/mobileLogin";
//    public static String wxPost="http://static.mlxing.com/guide/v1/weixin/account/mobileClient/mobileLogin";
    public static String wxPost2 = "https://weixin.mlxing.com/shop/getAppUserInfo";

    //返回成功值
    public static String API_RESPONSE_OK = "200";

    //批量获得用户信息从自己的服务器
    public static String URL_GETUSERINFOLIST = "http://weixin.mlxing.com/shop/getHXList";
//    public static String URL_GETUSERINFOLIST = "http://wjapi.mlxing.com/guide/v1/weixin/account/mobileClient/getUserInfoList";

    //请求头部
    public static String HANDER_NAME = "userAgent";
    public static String HANDER_VALUE = "mlxapp";

    //拦截登陆的url
    public static String LOGIN_URL = "http://mlxing.login";
    //拦截进群url
    public static String QUN = "http://mlxing.group/?group_id=172283396305715676";
    //拦截进入单聊的url
    public static String CHAT = "http://mlxing.chat/?username=q28gx59qffko1i1liwql";
    //拦截可以加群的url
    public static String INGROUP = "http://weixin.mlxing.com/wrq/joinGroup?group_id=172283396305715676";
    //拦截进入通讯录
    public static String URL_CONTACT = "http://mlxing.contact";
    //拦截进入消息
    public static String URL_MESSAGE = "http://mlxing.message";

    //离开指定群组
    public static String TOKENKEY = "acol$!z%wh";
    public static String EXITGROUP = "http://weixin.chineseml.com/easemob/leaveGroup?user=%s&group_id=%s&token=%s&utime=%s";

    //    设置->帮助中心 url：http://front.oss.mlxing.com/front/html/helpcenter/Help.html
    public static String SET_HRLP = "http://front.oss.mlxing.com/front/html/helpcenter/Help.html";

//    QQ：http://wpa.qq.com/msgrd?v=3&uin=3307713966&site=qq&menu=yes
    public static String POP_QQ ="http://wpa.qq.com/msgrd?v=3&uin=3307713966&site=qq&menu=yes";

//    设置->功能介绍：http://front.oss.mlxing.com/front/html/func_intro.html
    public static String SET_ABOUT="http://front.oss.mlxing.com/front/html/func_intro.html";

    //分享的内容
    public static String POP_SHARE="http://front.oss.mlxing.com/front/html/app_share.html";


    //首页的几个大块
    public static  final String MAIN_DAOYOU="http://static.mlxing.com/index.html#/players_list";
    public static  final String MAIN_QUANZI="http://static.mlxing.com/index.html#/player_crowd";
    public static  final String MAIN_FAXIAN="http://static.mlxing.com/zhihuilvy-index.html";
    public static  final String MAIN_HAOWAN="http://static.mlxing.com/index.html#/players_list";
    public static  final String MAIN_WO="我";
}
