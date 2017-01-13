package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.DemoModel;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.utils.CacheClearHelper;
import com.mlxing.chatui.daoyou.utils.PopupUtils;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.UIHelper;

import easeui.widget.EaseAlertDialog;
import easeui.widget.EaseSwitchButton;
import easeui.widget.EaseTitleBar;

public class MainMenuActivity extends Activity {

    private static final String TAG = "MainMenuActivity";

    private RelativeLayout rl_set_clear;
    private RelativeLayout rl_set_help;
    private RelativeLayout rl_set_about;
    private Button btn_set_out;

    private TextView tv_cache_value;
    EaseTitleBar titleBar;

    /**
     * 设置新消息通知布局
     */
    private RelativeLayout rl_switch_notification;
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 设置震动布局
     */
    private RelativeLayout rl_switch_vibrate;
    /**
     * 声音和震动中间的那条线
     */
    private TextView textview1;
    private TextView textview2;
    private EaseSwitchButton notifiSwitch;
    private EaseSwitchButton soundSwitch;
    private EaseSwitchButton vibrateSwitch;
    private DemoModel settingsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mlx_activity_main_menu);
        initView();
        //如果有unionid，直接更改退出登陆按钮字
        String unionid = (String) SPUtils.get(this, SPUtils.SP_UNIONID, "");
        if (!unionid.equals("")) {
            btn_set_out.setText("退出登录");
        }else {
            btn_set_out.setText("登录");
        }


        settingsModel = DemoHelper.getInstance().getModel();

        titleBar.setTitle(getResources().getString(R.string.setting));
        titleBar.setLeftTextVisiable(View.INVISIBLE);
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.getInstance().creatRightPop(MainMenuActivity.this,titleBar.getRightLayout(),MainMenuActivity.this);
            }
        });
        tv_cache_value.setText(CacheClearHelper.getTotalCacheSize(getApplicationContext()));

        // 震动和声音总开关，来消息时，是否允许此开关打开
        // the vibrate and sound notification are allowed or not?
        if (settingsModel.getSettingMsgNotification()) {
            notifiSwitch.openSwitch();
            rl_switch_sound.setVisibility(View.VISIBLE);
            rl_switch_vibrate.setVisibility(View.VISIBLE);
            textview1.setVisibility(View.VISIBLE);
            textview2.setVisibility(View.VISIBLE);
        } else {
            notifiSwitch.closeSwitch();
            rl_switch_sound.setVisibility(View.GONE);
            rl_switch_vibrate.setVisibility(View.GONE);
            textview1.setVisibility(View.GONE);
            textview2.setVisibility(View.GONE);
        }

        // 是否打开声音
        // sound notification is switched on or not?
        if (settingsModel.getSettingMsgSound()) {
            soundSwitch.openSwitch();
        } else {
            soundSwitch.closeSwitch();
        }

        // 是否打开震动
        // vibrate notification is switched on or not?
        if (settingsModel.getSettingMsgVibrate()) {
            vibrateSwitch.openSwitch();
        } else {
            vibrateSwitch.closeSwitch();
        }
    }

    private void initView() {

        rl_set_clear= (RelativeLayout) findViewById(R.id.rl_set_clear);
        rl_set_help= (RelativeLayout) findViewById(R.id.rl_set_help);
        rl_set_about= (RelativeLayout) findViewById(R.id.rl_set_about);
         btn_set_out= (Button) findViewById(R.id.btn_set_out);

     tv_cache_value= (TextView) findViewById(R.id.tv_cache_value);
         titleBar= (EaseTitleBar) findViewById(R.id.title_bar);


        rl_switch_notification= (RelativeLayout) findViewById(R.id.rl_switch_notification);
        rl_switch_sound= (RelativeLayout) findViewById(R.id.rl_switch_sound);
         rl_switch_vibrate= (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
        /**
         * 声音和震动中间的那条线
         */
        textview1= (TextView) findViewById(R.id.textview1);
        textview2= (TextView) findViewById(R.id.textview2);
        notifiSwitch= (EaseSwitchButton) findViewById(R.id.switch_notification);
        soundSwitch= (EaseSwitchButton) findViewById(R.id.switch_sound);
         vibrateSwitch= (EaseSwitchButton) findViewById(R.id.switch_vibrate);

    }

//    /**
//     * 清除WebView缓存
//     */
//    public void clearWebViewCache(){
//
//        //清理Webview缓存数据库
//        try {
//            deleteDatabase("webview.db");
//            deleteDatabase("webviewCache.db");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //WebView 缓存文件
//        File appCacheDir = new File(getFilesDir().getAbsolutePath()+"/webcache");
//        Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());
//
//        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
//        Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());
//
//        //删除webview 缓存目录
//        if(webviewCacheDir.exists()){
//            deleteFile(webviewCacheDir);
//        }
//        //删除webview 缓存 缓存目录
//        if(appCacheDir.exists()){
//            deleteFile(appCacheDir);
//        }
//    }
//
//    /**
//     * 递归删除 文件/文件夹
//     *
//     * @param file
//     */
//    public void deleteFile(File file) {
//
//        Log.i(TAG, "delete file path=" + file.getAbsolutePath());
//
//        if (file.exists()) {
//            if (file.isFile()) {
//                file.delete();
//            } else if (file.isDirectory()) {
//                File files[] = file.listFiles();
//                for (int i = 0; i < files.length; i++) {
//                    deleteFile(files[i]);
//                }
//            }
//            file.delete();
//        } else {
//            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
//        }
//    }
//

    public void click(View v) {
        switch (v.getId()) {
//            case R.id.rl_set_feedback://反馈
//                break;
//            case R.id.rl_set_update://更新
//                break;
            case R.id.rl_set_clear://清理
                String str = "清除成功";
//                clearWebViewCache();
                CacheClearHelper.clearAllCache(getApplicationContext());
                tv_cache_value.setText(CacheClearHelper.getTotalCacheSize(getApplicationContext()));
                Toast.makeText(MainMenuActivity.this, str, Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_set_help://帮助
                UIHelper.goToWebView(MainMenuActivity.this, Constant.SET_HRLP);
                break;
            case R.id.rl_set_about://关于
                UIHelper.goToWebView(MainMenuActivity.this, Constant.SET_ABOUT);
                break;
            case R.id.btn_set_out://退出登陆
                if ("退出登录".equals(btn_set_out.getText().toString())){



                String st6 ="确定要退出登录吗？";
                new EaseAlertDialog(MainMenuActivity.this, null, st6, null, new
                        EaseAlertDialog.AlertDialogUser() {

                            @Override
                            public void onResult(boolean confirmed, Bundle bundle) {
                                if (confirmed) {
                                    DemoHelper.getInstance().logout(false, new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
//                                            CacheClearHelper.cleanApplicationData(getApplicationContext());
                                            CacheClearHelper.removeCookie(getApplicationContext());
                                            SPUtils.put(MainMenuActivity.this, SPUtils.SP_UNIONID, "");
                                            SPUtils.remove(MainMenuActivity.this,SPUtils.MID);
                                            UIHelper.gotoLoginActivity(MainMenuActivity.this);

                                            finish();
                                        }

                                        @Override
                                        public void onError(int i, String s) {

                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });
                                }
                            }
                        }, true).show();
                }else{
                    UIHelper.gotoHomeLoginActivity(this);
                }
                break;
            case R.id.rl_switch_notification://通知选项按钮
                if (notifiSwitch.isSwitchOpen()) {
                    notifiSwitch.closeSwitch();
                    rl_switch_sound.setVisibility(View.GONE);
                    rl_switch_vibrate.setVisibility(View.GONE);
                    textview1.setVisibility(View.GONE);
                    textview2.setVisibility(View.GONE);
                    settingsModel.setSettingMsgNotification(false);
                } else {
                    notifiSwitch.openSwitch();
                    rl_switch_sound.setVisibility(View.VISIBLE);
                    rl_switch_vibrate.setVisibility(View.VISIBLE);
                    textview1.setVisibility(View.VISIBLE);
                    textview2.setVisibility(View.VISIBLE);
                    settingsModel.setSettingMsgNotification(true);
                }
                break;
            case R.id.rl_switch_sound://声音选项按钮
                if (soundSwitch.isSwitchOpen()) {
                    soundSwitch.closeSwitch();
                    settingsModel.setSettingMsgSound(false);
                } else {
                    soundSwitch.openSwitch();
                    settingsModel.setSettingMsgSound(true);
                }
                break;
            case R.id.rl_switch_vibrate://震动选项按钮
                if (vibrateSwitch.isSwitchOpen()) {
                    vibrateSwitch.closeSwitch();
                    settingsModel.setSettingMsgVibrate(false);
                } else {
                    vibrateSwitch.openSwitch();
                    settingsModel.setSettingMsgVibrate(true);
                }
                break;
        }
    }

}
