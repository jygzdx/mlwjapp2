package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.mlxing.chatui.DemoApplication;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.entity.LoginEntity;
import com.mlxing.chatui.daoyou.entity.WxHuanXinEntity;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.StringUtil;
import com.mlxing.chatui.daoyou.utils.UIHelper;
import com.mlxing.chatui.ui.LoginActivity;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MlxLoginActivity extends Activity {
    private Context context;
    private String TAG="MlxLoginActivity";
    @BindView(R.id.img_wx)
    ImageView imgWx;
    @BindView(R.id.img_login)
    ImageView imgLogin;
    @BindView(R.id.text_sign)
    TextView textSign;
    @BindView(R.id.text_find)
    TextView textFind;

    private UMShareAPI mShareAPI = ((DemoApplication) getApplication()).getUmShareAPI();
    private String unionId, nickname, headImg;

    private boolean progressShow;
    ProgressDialog pd;
    private Map<String, String> wxMap;
    private WxHuanXinEntity wxHuanXinEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mlx_activity_mlx_login);
        context=this;

        //如果有unionid，直接进入主界面
        String unionid = (String) SPUtils.get(context, SPUtils.SP_UNIONID, "");
        if (!unionid.equals("")) {
            UIHelper.goToWebView(context, (String) SPUtils.get(context, SPUtils.SP_UNIONID, ""));
            finish();
        }
        ButterKnife.bind(this);

        textSign.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线

        textSign.getPaint().setAntiAlias(true);//抗锯齿

    }

    @OnClick({R.id.img_wx, R.id.img_login, R.id.text_sign, R.id.text_find,R.id.visitor})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_wx:
                wxLogin();
                break;
            case R.id.img_login:
                startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.text_sign:
//                startActivity(new Intent(context,MlxSignActivity.class));
                startActivityForResult(new Intent(context,MlxSignActivity.class),Activity.RESULT_OK);
                break;
            case R.id.text_find:
                // TODO: 16-5-19 找回密码
                break;
            case R.id.visitor://游客登录
                UIHelper.goToWebView(MlxLoginActivity.this,Constant.URL_HOME);
                break;
        }
    }

    /**
     * 微信一键登陆
     *
     * @param v
     */
    public void wxLogin() {
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


    /**
     * 通过服务器返回的用户名和密码登陆环信。
     *
     * @param username
     * @param userpw
     */
    private void hxLogin(final String username, String userpw) {

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
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        String s = response.body().string();
                        List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                        if (result != null && result.size() > 0) {
                            EaseUser user = result.get(0);
                            DemoHelper.getInstance().setCurrentUserName(username);
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

                pd.dismiss();
                progressShow = false;

                SPUtils.put(context, SPUtils.SP_UNIONID, String.format(Constant.URL_LOGIN, unionId));
                UIHelper.goToWebView(context, (String) SPUtils.get(context, SPUtils.SP_UNIONID, ""));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
