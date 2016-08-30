package com.mlxing.chatui.daoyou;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.db.InviteMessgeDao;
import com.mlxing.chatui.ui.BaseActivity;
import com.mlxing.chatui.ui.LoginActivity;
import com.mlxing.chatui.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaoYouMainActivity extends BaseActivity implements EMEventListener {
    private Button[] btnS = null;
    private int currentIndex, lastIndex;
    private PopupWindow popupWindow;
    private WebView webView;
    Map<String,String> extraHeaders = new HashMap<String, String>();
    private LinearLayout ll;
    private ProgressBar pgWv;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> strS = new ArrayList<String>();
    private String[] strCenter = {"订单", "我的地盘"};
    private String[] strMedia = {"智库", "电台"};
    private String[] strService = {"帮助中心", "导游申请"};
    private String[] strWanRenQun = {"消息", "万人群"};
    /**
     * 未读信息数目
     */
    private TextView tvUnreadNumber;
    private InviteMessgeDao inviteMessgeDao;

    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    private String urlFind;
    private final String TAG = "DaoYouMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean(com.mlxing.chatui
                .Constant.ACCOUNT_REMOVED,
                false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            DemoHelper.getInstance().logout(true, null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict",
                false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dao_you_main);
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setVisibility(View.GONE);
        urlFind = String.format(Constant.URL_LOGIN, SPUtils.get(this, SPUtils.SP_OPENID, "")
                .toString());
        Log.e(TAG, "onCreate: urlfind:" + urlFind);
        initWebView();
        initPopup();


        tvUnreadNumber = (TextView) findViewById(R.id.unread_number);
        inviteMessgeDao = new InviteMessgeDao(this);

        btnS = new Button[]{(Button) findViewById(R.id.btn1), (Button) findViewById(R.id.btn2),
                (Button) findViewById(R.id.btn3), (Button) findViewById(R.id.btn4), (Button)
                findViewById(R.id.btn5)};
        btnS[0].setSelected(true);

        // 注册群组和联系人 监听
        DemoHelper.getInstance().registerGroupAndContactListener();
//        registerBroadcastReceiver();
    }

    /**
     * 初始化popup菜单
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initPopup() {

        View view = LayoutInflater.from(this).inflate(R.layout.popup_view, null);
        listView = (ListView) view.findViewById(R.id.lv_popup);
        listView.setDividerHeight(0);
        adapter = new ArrayAdapter<String>(this, R.layout.popup_item, strS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listViewItemListener);
        view.setAlpha(0.8f);
        popupWindow = new PopupWindow(view, 500, 200);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.getBackground().setAlpha(256);
    }

    /**
     * popup菜单的点击事件
     */
    AdapterView.OnItemClickListener listViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String str = strS.get(position);
            String url = null;
           /* if (str.equals("订单")) {
//                url = com.mlxing.chatui.daoyou.Constant.URL_dingdan;
                url = "http://weixin.chineseml.com/wrq/main/171591302436094408?from=app";
            } else if (str.equals("我的地盘")) {
//                url = "http://www.gbtai.com/";
//                url="file:///android_asset/text.html";
                url = com.mlxing.chatui.daoyou.Constant.URL_dipan;
            } else if (str.equals("智库")) {
                url = com.mlxing.chatui.daoyou.Constant.URL_zhiku;
            } else if (str.equals("电台")) {
                url = com.mlxing.chatui.daoyou.Constant.URL_diantai;
            } else if (str.equals("帮助中心")) {
                url = com.mlxing.chatui.daoyou.Constant.URL_bangzhu;
            } else if (str.equals("导游申请")) {
                url = com.mlxing.chatui.daoyou.Constant.URL_shenqing;
            } else if (str.equals("消息")) {
                url = Constant.URL_wanrenqun;
            } else if (str.equals("万人群")) {
                openIM();
            }*/
            if (!"".equals(str)) {
                webView.loadUrl(url);
            }
            dismiss();
        }
    };


    /**
     * 初始化WebView
     */
    private void initWebView() {

        pgWv = (ProgressBar) findViewById(R.id.pg_wv);
        webView = (WebView) findViewById(R.id.wv_main);

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setAppCacheEnabled(true);


        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webView.getSettings().setAppCachePath(appCachePath);
        webView.getSettings().setAllowFileAccess(true);

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString()+" mlxapp");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                view.loadUrl(url);

                return true;
            }


        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pgWv.setProgress(newProgress);
                if (newProgress == 100) {
                    pgWv.setVisibility(View.GONE);
                } else {
                    pgWv.setVisibility(View.VISIBLE);
                }

                super.onProgressChanged
                        (view, newProgress);
            }

//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                Toast.makeText(DaoYouMainActivity.this, title, Toast.LENGTH_SHORT).show();
//                super.onReceivedTitle(view, title);
//            }
        });
        String url= SPUtils.get(DaoYouMainActivity.this,SPUtils.SP_UNIONID,"").toString();
        if (url=="")
            url=Constant.URL_HOME;

        webView.loadUrl(url,extraHeaders);
    }


    /**
     * 下面按钮的点击事
     */
    public void btnClick(View v) {

        switch (v.getId()) {
            case R.id.btn1://发现
                dismiss();
                currentIndex = 0;
                webView.clearCache(true);
                webView.loadUrl(urlFind);

                break;
            case R.id.btn2://万人群
                dismiss();
                currentIndex = 1;

                break;
            case R.id.btn3://中心
                currentIndex = 2;
                break;
            case R.id.btn4://媒体
                currentIndex = 3;

                break;
            case R.id.btn5://服务
                currentIndex = 4;
                break;

        }

        if (currentIndex == 1 || currentIndex == 2 || currentIndex == 3 || currentIndex == 4) {
            if (currentIndex == 2) {
                strS = Arrays.asList(strCenter);
            } else if (currentIndex == 3) {
                strS = Arrays.asList(strMedia);
            } else if (currentIndex == 4) {
                strS = Arrays.asList(strService);
            } else if (currentIndex == 1) {
                strS = Arrays.asList(strWanRenQun);
            }
            adapter = new ArrayAdapter<String>(this, R.layout.popup_item, strS);
            listView.setAdapter(adapter);

            popupWindow.setWidth(v.getWidth());
            popupWindow.setHeight(v.getHeight() * strS.size());
            if (lastIndex == currentIndex) {
                if (popupWindow.isShowing()) {

                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(btnS[currentIndex]);
                }

                return;
            }
            popupWindow.dismiss();

            popupWindow.showAsDropDown(btnS[currentIndex], 0, 0);

        }
        btnS[lastIndex].setSelected(false);
        btnS[currentIndex].setSelected(true);
        lastIndex = currentIndex;


    }

    /**
     * 打开环信聊天界面
     */
    private void openIM() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 让菜单消失
     */
    private void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations()
                .values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count1 = getUnreadMsgCountTotal();
                int count2 = getUnreadAddressCountTotal();
                if ((count1 + count2) > 0) {
                    tvUnreadNumber.setText(String.valueOf(count1 + count2));
                    tvUnreadNumber.setVisibility(View.VISIBLE);
                } else {
                    tvUnreadNumber.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.mlxing.chatui.Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(com.mlxing.chatui.Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadAddressLable();


            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
//
//        updateUnreadAddressLable();
//        // unregister this event listener when this activity enters the
//        // background
//        DemoHelper sdkHelper = DemoHelper.getInstance();
//        sdkHelper.pushActivity(this);
//
//        // register the event listener when enter the foreground
//        EMChatManager.getInstance().registerEventListener(this,
//                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
//                        EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event
//                        .EventConversationListChanged});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
//        unregisterBroadcastReceiver();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        webView.clearCache(true);
        webView.clearHistory();
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        refreshUIWithMessage();
    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadAddressLable();

            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           if (webView.canGoBack()){
               webView.goBack();

               return true;

           }else {
               moveTaskToBack(false);
               return true;
           }
        }
        return super.onKeyDown(keyCode, event);
    }

}
