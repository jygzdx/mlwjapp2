/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;


public class DemoApplication extends Application {
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
	public static  Handler mHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		CrashReport.initCrashReport(getApplicationContext(), "900026186", false);

		applicationContext = this;
        instance = this;
//        APICloud.initialize(this);
        mUmShareAPI=UMShareAPI.get(this);
        //init demo helper
        DemoHelper.getInstance().init(applicationContext);
		//微信 appid appsecret
		PlatformConfig.setWeixin("wxbe4d6a747dcf578f", "df0e35026183d0c930e303356e49c7b6");
		api = WXAPIFactory.createWXAPI(getApplicationContext(), "wxbe4d6a747dcf578f");
		api.registerApp("wxbe4d6a747dcf578f");
	}

	public static DemoApplication getInstance() {
		return instance;
	}

	public static UMShareAPI getUmShareAPI(){
		return mUmShareAPI;
	}

    public static Handler getHandler(){
        return mHandler;
    }
    public static void setHandler(Handler handler){
        mHandler=handler;
    }

public static IWXAPI getApi(){return api;}


}
