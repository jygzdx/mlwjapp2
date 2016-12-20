/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlxing.chatui;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DemoApplication extends Application {
    private static final String TAG = "DemoApplication";
    public static String regId;
    public static Context applicationContext;
    private static DemoApplication instance;
    private static UMShareAPI mUmShareAPI;
    private static IWXAPI api;
    // login user name
    public final String PREF_USERNAME = "username";

    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    public static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "900026186", false);
        SDKInitializer.initialize(getApplicationContext());
        applicationContext = this;
        instance = this;
//        APICloud.initialize(this);
        mUmShareAPI = UMShareAPI.get(this);
        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
        //微信 appid appsecret
        PlatformConfig.setWeixin("wxbe4d6a747dcf578f", "df0e35026183d0c930e303356e49c7b6");
        api = WXAPIFactory.createWXAPI(getApplicationContext(), "wxbe4d6a747dcf578f");
        api.registerApp("wxbe4d6a747dcf578f");


        //极光推送初始化
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        String mid = (String) SPUtils.get(getApplicationContext(),SPUtils.MID," ");
        String regId = (String) SPUtils.get(getApplicationContext(),SPUtils.REGID," ");
        Log.i(TAG, "onCreate: regId = "+regId);
        if(!" ".equals(mid)&&" ".equals(regId)){
            regId = JPushInterface.getRegistrationID(this);
            SPUtils.put(getApplicationContext(),SPUtils.REGID,regId);

            OkHttpClient mOkHttpClient = new OkHttpClient();
            FormBody formBody = new FormBody.Builder().add("mid",mid).add("register_id",regId).build();
            Request request = new Request.Builder().url("http://weixin.mlxing.com/shop/register_id").post(formBody).build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i("DemoApplication","返回信息："+response.body().string());
                }
            });
        }

    }

    public static DemoApplication getInstance() {
        return instance;
    }

    public static UMShareAPI getUmShareAPI() {
        return mUmShareAPI;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static void setHandler(Handler handler) {
        mHandler = handler;
    }

    public static IWXAPI getApi() {
        return api;
    }


}
