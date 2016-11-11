/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
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
package com.mlxing.chatui.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.mlxing.chatui.DemoApplication;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.entity.ForgetEntity;
import com.mlxing.chatui.daoyou.entity.LoginEntity;
import com.mlxing.chatui.daoyou.entity.UserInfoEntity;
import com.mlxing.chatui.daoyou.entity.WxHuanXinEntity;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;
import com.mlxing.chatui.daoyou.utils.LogTool;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.StringUtil;
import com.mlxing.chatui.daoyou.utils.ToastTool;
import com.mlxing.chatui.daoyou.utils.UIHelper;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easeui.domain.EaseUser;
import easeui.widget.EaseTitleBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.edit_username)
    EditText editUsername;
    @BindView(R.id.edit_pswd1)
    EditText editPswd1;

    private Context context;
    private UMShareAPI mShareAPI = ((DemoApplication) getApplication()).getUmShareAPI();
    private String unionId, nickname, headImg;


    private Map<String, String> wxMap;
    private WxHuanXinEntity wxHuanXinEntity;

    private boolean autoLogin = false;

    /**
     * 编辑框的用户名和密码
     */
    private String inUsername, inPswd, inPswd2;


    /**
     * 微信登陆返回信息
     */
    private boolean progressShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.em_activity_login);

        //如果有unionid，直接进入主界面
        String unionid = (String) SPUtils.get(this, SPUtils.SP_UNIONID, "");
        if (!unionid.equals("")) {
            UIHelper.goToWebView(this, (String) SPUtils.get(this, SPUtils.SP_UNIONID, ""));
            finish();
        }

        ButterKnife.bind(this);
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        context=this;
        titleBar.setTitle("登录");
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LoginEntity login = (LoginEntity) getIntent().getSerializableExtra("user");
        ForgetEntity forget= (ForgetEntity) getIntent().getSerializableExtra("forget");
        if (login != null) {
            LogTool.i(TAG, login.getUsername() + ";" + login.getUserpw());
            unionId = login.getUnionid();
            nickname = login.getNickName();
            headImg = login.getHeadImg();
            progressShow = true;
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
            pd.setMessage(getString(R.string.Is_landing));
            pd.show();
            hxLogin(login.getUsername(), login.getUserpw());
        }
        if (forget!=null){
            LogTool.i(TAG, forget.getUsername() + ";" + forget.getUserpw());
            unionId = forget.getUnionid();
            nickname = forget.getNickName();
            headImg = forget.getHeadImg();
            progressShow = true;
            pd = new ProgressDialog(LoginActivity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
            pd.setMessage(getString(R.string.Is_landing));
            pd.show();
            hxLogin(forget.getUsername(), forget.getUserpw());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }


    ProgressDialog pd;


    /**
     * 通过服务器返回的用户名和密码登陆环信。
     *
     * @param username
     * @param userpw
     */
    private void hxLogin(final String username, String userpw) {
        try{

            // 调用sdk登陆方法登陆聊天服务器
            DemoHelper.getInstance().init(getApplicationContext());
            EMChatManager.getInstance().login(username, userpw, new EMCallBack() {

                @Override
                public void onSuccess() {
                    if (!progressShow) {
                        return;
                    }
                    // 登陆成功，保存用户名和图像
                    HttpUtil.getUserInfo(username).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Log.i(TAG,"onFailure");

                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            String s = response.body().string();
                            List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                            if (result != null && result.size() > 0) {
                                EaseUser user = result.get(0);
                                DemoHelper.getInstance().setCurrentUserName(user.getUsername());
                                DemoHelper.getInstance().setCurrentUserAvatar(user.getAvatar());
                                DemoHelper.getInstance().setCurrentUserNickName(user.getNick());
                            }
                        }
                    });

                    SPUtils.put(getApplicationContext(), SPUtils.SP_MY_REFRESH, new Date().getTime());
                    // 注册群组和联系人监听
                    DemoHelper.getInstance().registerGroupAndContactListener();

                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();

                    //获取陌生人列表
                    DemoHelper.getInstance().getStrangerList();
                    DemoHelper.getInstance().getContactInfos();


                    // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                    boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                            nickname);
                    if (!updatenick) {
                        Log.e("LoginActivity", "update current user nick fail");
                    }

//                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                    progressShow = false;
//                }

//                // 进入主页面
                /*Intent intent = new Intent(LoginActivity.this,
                        DaoYouMainActivity.class);
                startActivity(intent);*/
                    SPUtils.put(LoginActivity.this, SPUtils.SP_UNIONID, String.format(Constant.URL_LOGIN, unionId));

                    //getUserPhone(unionId);



                    UIHelper.goToWebView(LoginActivity.this, (String) SPUtils.get(LoginActivity.this, SPUtils.SP_UNIONID, ""));
                    finish();
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {
                    if (!progressShow) {
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            progressShow = false;
                            Toast.makeText(getApplicationContext(), getString(R.string.Login_failed)
                                            + message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getUserPhone(String unionId) {
        HttpUtil.getClient().getUserInfo(unionId).enqueue(new Callback<UserInfoEntity>() {
            @Override
            public void onResponse(Call<UserInfoEntity> call, Response<UserInfoEntity> response) {
                UserInfoEntity userInfoEntity = response.body();
                if("200".equals(userInfoEntity.getCode())){
                    SPUtils.put(getApplicationContext(),SPUtils.PHONE,userInfoEntity.getResult().getPhone());
                }
            }

            @Override
            public void onFailure(Call<UserInfoEntity> call, Throwable t) {

            }
        });
    }


    /**
     * 登陆
     */
    public void login() {

        try{
            inUsername = editUsername.getText().toString();
            inPswd = editPswd1.getText().toString();
            if (isCanLogin(inUsername, inPswd)) {
                HttpUtil.getClient().login(inUsername, inPswd).enqueue(new Callback<LoginEntity>() {
                    @Override
                    public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                        LoginEntity loginEntity = response.body();
                        LogTool.i(TAG, loginEntity.getCode() + ";" + loginEntity.getUnionid() + ";" + loginEntity.getUsername() + ";" + loginEntity.getUserpw());
                        if ("1".endsWith(loginEntity.getCode())) {
                            SPUtils.put(LoginActivity.this, SPUtils.USERNAME, loginEntity.getUsername());
                            SPUtils.put(LoginActivity.this, SPUtils.PASSWORD, loginEntity.getUserpw());
                            unionId = loginEntity.getUnionid();
                            headImg = loginEntity.getHeadImg();
                            nickname = loginEntity.getNickName();
                            progressShow = true;
                            pd = new ProgressDialog(LoginActivity.this);
                            pd.setCanceledOnTouchOutside(false);
                            pd.setOnCancelListener(new OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    progressShow = false;
                                }
                            });
                            pd.setMessage(getString(R.string.Is_landing));
                            pd.show();
                            hxLogin(loginEntity.getUsername(), loginEntity.getUserpw());
                        } else {
                            ToastTool.show(LoginActivity.this, "登录失败:用户名或密码错误", 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginEntity> call, Throwable t) {
                        ToastTool.show(LoginActivity.this, "登录失败", 0);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * 判断是否可以进行登陆
     *
     * @param inUsername
     * @param inPswd
     * @return
     */
    private boolean isCanLogin(String inUsername, String inPswd) {
        if (inUsername.length() != 11) {
            ToastTool.showLong(this, "请输入正确的手机号");
            return false;
        }
        if (inPswd.length() <= 0) {
            ToastTool.showLong(this, "密码不能为空");
            return false;
        }

        return true;
    }

    private boolean isCanSign(String inUsername, String inPswd, String inPswd2) {
        LogTool.i(TAG, inPswd + ";" + inPswd2);
        if (inUsername.length() != 11) {
            ToastTool.showLong(this, "请输入正确的手机号");
            return false;
        }
        if (inPswd.length() <= 0 || inPswd2.length() <= 0) {
            ToastTool.showLong(this, "密码不能为空");
            return false;
        }
        if (!inPswd.equals(inPswd2)) {
            ToastTool.showLong(this, "两次输入的密码不一致");
            return false;
        }

        return true;

    }


    @OnClick({R.id.img_login, R.id.img_sign,R.id.img_forget,R.id.img_wx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_login:
                login();
                break;
            case R.id.img_sign:
                UIHelper.gotoSignActivity(this);
                break;
            case R.id.img_forget:
                UIHelper.gotoForgetActivity(this);
                break;
            case R.id.img_wx:
                wxLogin();
                break;
        }
    }

    /**
     * 微信一键登陆
     *
     * @param
     */
    public void wxLogin() {
        try{
            progressShow = true;
            pd = new ProgressDialog(context);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
            pd.setMessage(getString(R.string.Is_landing));
            pd.show();

            mShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                    wxMap = new HashMap();
                    wxMap = map;
                    Log.i(TAG, "umeng onComplete:" + wxMap.toString());
                    //调用自己的服务器 {unionid=oCIF1s97ZErW5hTyplpCqgqHbpP8,
                    // scope=snsapi_userinfo, expires_in=7200, access_token=OezXcEiiBSKSxW0eoylIeDCsjNdDNbv5N33ac5yG24_
                    // cGLT-5mLItiqDUlmKRNT0srIUo9bMnA2fk-Q4CdMO5purxYekLzlUB2mtNZWGOKw20h4rgFNh1RTPOzP6-QkKv2AX-SDt4yW2_Tff0Ghs9A,
                    // openid=o_XtIv6zF7RiHffxc7U_ECf0e2NE, refresh_token=OezXcEiiBSKSxW0eoylIeDCsjNdDNbv5N33ac5yG24_cGLT-5mLItiqDUlmKRNT03Voz
                    // lk4eVsEofW0Tu9sOspxFKCRC0gu6XEg8DiznZdWXzkC2MMqRUBqVJtOnvmKjG2XUcgSvWDmqSUqPMZJgZg}
                    //username='y1hk3j8dgtm3796', userpw='aairpbgnkq'

                    Log.i(TAG, "unioid: " + wxMap.get("unionid") + "    openid:" + wxMap.get("openid"));

                    unionId = wxMap.get("unionid");
                    nickname = wxMap.get("nickname");

                    doGetAccount(wxMap.get("openid"), wxMap.get("access_token"));

                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    Log.i(TAG, "umengonError: 错误" + i, throwable);

                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    Log.i(TAG, "umengonCancel: 取消" + i);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    /**
     * 从自己的服务器获得账号密码,并登录
     *
     * @param openid
     * @param
     */
    private void doGetAccount(String openid, String access_token) {

        Call<WxHuanXinEntity> huanXinCall = HttpUtil.getClient().getHuanXin(openid, access_token);
        huanXinCall.enqueue(new Callback<WxHuanXinEntity>() {
            @Override
            public void onResponse(Call<WxHuanXinEntity> call, Response<WxHuanXinEntity> response) {
                Log.i(TAG, "onResponse code=: " + response.code());
                wxHuanXinEntity = response.body();
                headImg = wxHuanXinEntity.getHeadImg();
                Log.i(TAG, "onSuccess: wxhuanxin:" + wxHuanXinEntity.toString());

                if (StringUtil.empty(wxHuanXinEntity.getUsername()) || StringUtil.empty(wxHuanXinEntity.getUserpw())) {
                    Toast.makeText(context, "获得的登录名或密码为空\r\nname：" + wxHuanXinEntity.getUsername() + "  pw：" + wxHuanXinEntity.getUserpw(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SPUtils.put(context, SPUtils.USERNAME, wxHuanXinEntity.getUsername());
                    SPUtils.put(context, SPUtils.PASSWORD, wxHuanXinEntity.getUserpw());
                    hxLogin(wxHuanXinEntity.getUsername(), wxHuanXinEntity.getUserpw());
                }

            }

            @Override
            public void onFailure(Call<WxHuanXinEntity> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t);
            }
        });
    }
}
