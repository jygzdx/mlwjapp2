package com.mlxing.chatui.daoyou.utils;

import com.mlxing.chatui.DemoApplication;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.entity.ForgetEntity;
import com.mlxing.chatui.daoyou.entity.LoginEntity;
import com.mlxing.chatui.daoyou.entity.SendMsgEntity;
import com.mlxing.chatui.daoyou.entity.UserInfoEntity;
import com.mlxing.chatui.daoyou.entity.WxHuanXinEntity;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/3/8.
 */
public class HttpUtil {

    private static ApiInterface apiInterface;
    private static OkHttpClient okHttpClient;

    private static String getNewVersion(){
        return "";
    }

    //retrofit
    private static OkHttpClient  getOkClient(){
        OkHttpClient mOkHttpClient = null;
        InputStream inputStream = null;
        inputStream = DemoApplication.getInstance().getResources().openRawResource(R.raw.mlxing);
        CertificateFactory certificateFactory = null;//证书处理
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", certificate);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            mOkHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    }).build();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return mOkHttpClient;
    }
    /**
     * retrofit
     *
     * @return
     */
    public static ApiInterface getClient() {
        if (apiInterface == null) {



            Retrofit client = new Retrofit.Builder().baseUrl("https://weixin.mlxing.com/shop/getAppUserInfo/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getOkClient())
                    .build();
            apiInterface = client.create(ApiInterface.class);
        }
        return apiInterface;
    }

    public interface ApiInterface {
        //微信
        @FormUrlEncoded
        @POST("https://weixin.mlxing.com/shop/getAppUserInfo")
        Call<WxHuanXinEntity> getHuanXin(@Field("openid") String openid, @Field("access_token") String access_token);

        //注册
        @FormUrlEncoded
        @POST("https://weixin.mlxing.com/login/register")
        Call<LoginEntity> sign(@Field("username") String username, @Field("password") String password,@Field("verify") String verify);

        //登录
        @FormUrlEncoded
        @POST("https://weixin.mlxing.com/shop/app_login")
        Call<LoginEntity> login(@Field("username") String username, @Field("password") String password);

        //忘记密码
        @FormUrlEncoded
        @POST("https://weixin.mlxing.com/login/resetPwd")
        Call<ForgetEntity> forget(@Field("username") String username,@Field("password") String password,@Field("verify") String verify);

        //发送验证码
        @FormUrlEncoded
        @POST("http://weixin.mlxing.com/login/mmsverify")
        Call<SendMsgEntity> sendMsg(@Field("mobile") String mobile);

        //获取个人信息
        @GET("http://wjapi.mlxing.com/guide/v1/guide/getDyUser")
        Call<UserInfoEntity> getUserInfo(@Query("unionId") String unionId);
    }


    //okhttp
    /**
     * 根据名字获取用户信息
     *
     * @param userName
     * @return
     */
    public static okhttp3.Call getUserInfo(String userName) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        if (userName==null){
            userName="0";
        }
        RequestBody body = new FormBody.Builder().add("userlist", userName).build();

        Request request = new Request.Builder().url(com.mlxing.chatui.daoyou.Constant.URL_GETUSERINFOLIST).post(body).build();
        return okHttpClient.newCall(request);
    }

    /**
     * 获取添加朋友的数据
     * @param friendname
     * @return
     */
    public static okhttp3.Call getFriendInfo(String friendname){
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        if (friendname==null){
            friendname="0";
        }
        RequestBody body = new FormBody.Builder().add("nickname", friendname).build();

        Request request = new Request.Builder().url(Constant.SHOW_FRIEND_URL).post(body).build();
        return okHttpClient.newCall(request);
    }

    public static okhttp3.Call getChargeInfo(String total,String mid){
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        RequestBody body = new FormBody.Builder().add("total_fee", total).add("mid",mid).build();

        Request request = new Request.Builder().url(Constant.WEIXIN_CHARGE_URL).post(body).build();
        return okHttpClient.newCall(request);
    }

    /**
     * \get获取字符串
     *
     * @param url
     * @return
     */
    public static okhttp3.Call getString(String url) {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        Request request = new Request.Builder().url(url).build();
        return okHttpClient.newCall(request);

    }

}


