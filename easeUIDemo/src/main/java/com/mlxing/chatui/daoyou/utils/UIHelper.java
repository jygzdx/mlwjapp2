package com.mlxing.chatui.daoyou.utils;

import android.content.Context;
import android.content.Intent;

import com.mlxing.chatui.daoyou.WebkitActivity;
import com.mlxing.chatui.daoyou.ui.MainMenuActivity;
import com.mlxing.chatui.daoyou.ui.MlxForgetActivity;
import com.mlxing.chatui.daoyou.ui.MlxSignActivity;
import com.mlxing.chatui.daoyou.ui.MlxVideoActivity;
import com.mlxing.chatui.ui.GroupsActivity;
import com.mlxing.chatui.ui.MainActivity;

import easeui.EaseConstant;
import easeui.ui.EaseBaiduMapActivity;

/**
 * 导航类
 * Created by sunll on 2016/4/11.
 */
public class UIHelper {
    /**
     * 跳转到webview
     * @param context
     * @param url
     */
    public static  void goToWebView(Context context,String url){
        Intent intent = new Intent(context, WebkitActivity.class);
        intent.putExtra("startUrl",url);
        context.startActivity(intent);

    }


    /**
     * 跳转到聊天界面
     * @param context
     * @param index
     */
    public static void goToMainActivity(Context context,int index ){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("index",index);
        context.startActivity(intent);
    }

    /**
     * 跳转到设置菜单页面
     * @param context
     */
    public static void goToSetActivity(Context context ){
        Intent intent = new Intent(context, MainMenuActivity.class);
        context.startActivity(intent);
    }

    /**
     * 退出登陆跳转到登录页面
     * @param context
     */
    public static void gotoLoginActivity(Context context){
        Intent intent = new Intent(context, MlxVideoActivity.class);
        context.startActivity(intent);
        ActivityManager.getInstance().exit();
    }

    /**
     * 跳转到登录首页
     * @param context
     */
    public static void gotoHomeLoginActivity(Context context){
        Intent intent = new Intent(context, MlxVideoActivity.class);
        context.startActivity(intent);
    }
    /**
     * 跳转到忘记密码
     * @param context
     */
    public static void gotoForgetActivity(Context context){
        Intent intent = new Intent(context, MlxForgetActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到欢迎视频
     * @param context
     */
    public static void gotoMlxVideoActivity(Context context){
        Intent intent = new Intent(context, MlxVideoActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到注册页面
     * @param context
     */
    public static void gotoSignActivity(Context context){
        Intent intent = new Intent(context, MlxSignActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到地图页面
     * @param context
     */
    public static void gotoMapAvtivity(Context context){
        Intent intent = new Intent(context,EaseBaiduMapActivity.class);
        intent.putExtra("popup", "yes");
        context.startActivity(intent);

    }

    public  static void gotoShareGroup(Context context){
        Intent intent=new Intent(context, GroupsActivity.class);
        intent.putExtra(EaseConstant.MESSAGE_ATTR_IS_SHARE,true);
        context.startActivity(intent);
    }
}
