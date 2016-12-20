package com.mlxing.chatui.daoyou.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mlxing.chatui.daoyou.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/7.
 */
public class VersionBiz {

    private String url;

    public VersionBiz(String url) {
        this.url = url;
    }

    public static String getVersion(Context context){
        String nowVersion = "";
        try{
            nowVersion = context.getPackageManager().getPackageInfo("com.mlxing.chatui",0).versionName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowVersion;
    }

    /**
     * 对比版本是否相同
     * @param currVersion
     * @param serviceAPKVersion
     * @return
     */
    public static boolean isSameVersion(Context context ,String currVersion,String serviceAPKVersion){
        String currVer[] = currVersion.split("\\.");
        String servVer[] = serviceAPKVersion.split("\\.");
        boolean first ,second,third;
        if(Integer.parseInt(currVer[0])!=Integer.parseInt(servVer[0])){
            first = false;
        }else{
            first = true;
        }
        if(Integer.parseInt(currVer[1])!=Integer.parseInt(servVer[1])){
            second = false;
        }else{
            second = true;
        }
        if(Integer.parseInt(currVer[2])!=Integer.parseInt(servVer[2])){
            third = false;
        }else{
            third = true;
        }
        if(first&&second&&third){

            return true;
        }else{

            return false;
        }
    }

    /**
     * 下载apk
     * @param serviceAPKVersion  版本号
     * @param handler
     * @param context
     * 废除url  -->"http://www.mlxing.com/static/app/mlxing_v3.apk"
     *
     */
    public void downApk(final String serviceAPKVersion,final Handler handler,final Context context){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("onfailure",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte buff[] = new byte[1024*8];
                FileOutputStream fos = null;
                int len = 0;
                try{
                    String apkName = String.valueOf(serviceAPKVersion.charAt(0));
                    is = response.body().byteStream();
                    String sdcardRoot= Environment.getExternalStorageDirectory().getAbsolutePath();
                    String apkSavePath=sdcardRoot+"/"+apkName+".apk";
                    File file = new File(apkSavePath);
                    fos = new FileOutputStream(file);
                    while((len = is.read(buff))!=-1){
                        fos.write(buff,0,len);
                    }
                    fos.flush();
                    Log.i("Version","成功");
                    SPUtils.put(context,"downapkVersion",serviceAPKVersion);
                    Message msg = handler.obtainMessage();
                    msg.what = Constant.HANDLER_DOWNLOAD_SUCCESE;
                    msg.obj = apkSavePath;
                    handler.sendMessage(msg);

                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    try{
                        if(is!=null){
                            is.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try{
                        if(fos!=null){
                            fos.close();
                        }
                    }catch (Exception e){
e.printStackTrace();
                    }
                }

            }
        });
    }


}
