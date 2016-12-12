package com.mlxing.chatui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.WebkitActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/11/2.
 */
public class JpushReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {

                JSONObject jsonObject = new JSONObject(extras);
                String jpushUrl = jsonObject.getString("content");
                Log.i("jpushUrl=",jpushUrl);
                /**
                 * 点击通知栏通知, 打开网页地址
                 * 把jpushUrl通过startActivity发送给WebViewActivity
                 */
                Intent intent_WebView = new Intent(context, WebkitActivity.class);
                //必须要写,不然出错,因为这是一个从非activity的类跳转到一个activity,需要一个flag来说明,这个flag就是Intent.FLAG_ACTIVITY_NEW_TASK
                intent_WebView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle_WebView = new Bundle();
                bundle_WebView.putString("startUrl", Constant.URL_HOME);
                bundle_WebView.putString("jpushUrl", jpushUrl);//文章url
                intent_WebView.putExtras(bundle_WebView);
                context.startActivity(intent_WebView);//打开WebViewActivity
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
