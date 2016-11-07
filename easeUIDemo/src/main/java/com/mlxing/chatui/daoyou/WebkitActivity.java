package com.mlxing.chatui.daoyou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.NetUtils;
import com.mlxing.chatui.DemoApplication;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.ActivityManager;
import com.mlxing.chatui.daoyou.utils.LogTool;
import com.mlxing.chatui.daoyou.utils.PopupUtils;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.UIHelper;
import com.mlxing.chatui.daoyou.utils.zxing.CaptureActivity;
import com.mlxing.chatui.db.InviteMessgeDao;
import com.mlxing.chatui.ui.BaseActivity;
import com.mlxing.chatui.ui.ChatActivity;
import com.mlxing.chatui.ui.SChatActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easeui.EaseConstant;
import easeui.widget.EaseTitleBar;

/**
 * webview首页
 */
public class WebkitActivity extends BaseActivity implements EMEventListener {
    private static final String TAG = "WebkitActivity";
    private Context mContext;
    private WebView webView;
    private EaseTitleBar mTitleBar;
    private ProgressBar pgWv;
    private ImageView btn_chat_dot, btn_chat_nodot;
    private ImageView imgError;

    private IWXAPI api;
    private CookieManager cookie;

    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
    private InviteMessgeDao inviteMessgeDao;

    private final static int FILECHOOSER_RESULTCODE = 2;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final int QRCODE_REQUEST = 5;
    public static final int QRCODE_RESULT = 6;


    private ValueCallback<Uri[]> mFilePathCallback;
    public ValueCallback<Uri> mUploadMessage;
    private String mCameraPhotoPath;
    private String title;
    private String tikuUrl;
    private int code;
    private boolean isShare;
    private boolean isJpush = false;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.i("WebkitActivity", (String) msg.obj);
                    break;
            }
        }
    };

    /**
     * 记录用户首次点击back键时间
     */
    private long firstTime;

    /**
     * 保存获取的网页标题
     */
    private List<String> titles = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mlx_activity_webkit);
        Log.i(TAG,"onCreate");
        ActivityManager.getInstance().addActivity(this);
        initView();
        initTitle();
        initWebView();
        mContext = this;
        isShare = getIntent().getBooleanExtra("isShare", false);
        String url = getIntent().getStringExtra("startUrl");
        LogTool.i(TAG, "oncreate:"+url);

        webView.loadUrl(url);


//        x.view().inject(this);

        //checkVersion();



        inviteMessgeDao = new InviteMessgeDao(this);

        // 注册群组和联系人监听
        DemoHelper.getInstance().registerGroupAndContactListener();
        registerBroadcastReceiver();
    }

    /**
     * 查询版本号
     */
    public void checkVersion() {
//        VersionUtils versionUtils = new VersionUtils();
//        versionUtils.getNewVersion(handler);
    }


    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        pgWv = (ProgressBar) findViewById(R.id.pg_wv);
        btn_chat_dot = (ImageView) findViewById(R.id.btn_chat_dot);
        btn_chat_nodot = (ImageView) findViewById(R.id.btn_chat_nodot);
        imgError = (ImageView) findViewById(R.id.img_error);
    }

    private void initWebView() {
        cookie = CookieManager.getInstance();
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用支持javascript
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 默认
        webSettings.setAllowFileAccess(true);// 可以访问文件
        webSettings.setBuiltInZoomControls(true);// 支持缩放
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);

        webView.addJavascriptInterface(new InJavaScriptGetBody(), "mlxapp");
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
//        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
//        webSettings.setAppCachePath(appCachePath);


        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setDisplayZoomControls(false);// 支持缩放
        }


        // Use WideViewport and Zoom out if there is no viewport defined
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        webSettings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            webSettings.setDisplayZoomControls(false);
        }

        //增加userAgent mlxapp
        webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + " " +
                "mlxapp");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                checkShowError();
                tikuUrl = url;
                Log.i(TAG, "shouldOverrideUrlLoading: " + url);
                Log.i(TAG,"shouldOverrideUrlLoading");
                if (url.contains("mlxing.group")) {
                    //http://mlxing.group/?group_id=XXX
                    // 进入群聊
                    Intent intent = new Intent(WebkitActivity.this, ChatActivity.class);
                    // it is group chat
                    intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_GROUP);
                    String u = url.substring(url.lastIndexOf("=") + 1);
                    intent.putExtra("userId", u);
                    Log.i(TAG, "shouldOverrideUrlLoading: ID:" + u);
                    startActivity(intent);
                    return true;
                } else if (url.contains("http://weixin.mlxing.com/zf/ddzf/")) {//支付
                   /* {"appid":"wxbe4d6a747dcf578f","noncestr":"c6wh33rcqasr3r0vfrz6vk3v2iq5cgvg",
 "package":"Sign=WXPay","partnerid":"1318904101","prepayid":"wx20160518150825e1533904db0277480426",
 "timestamp":"1463555305","sign":"2596ED0F62FFD05ADEA54D1379614734"}*/

                   /* final PayReq req = new PayReq();
                    //支付
                    api = DemoApplication.getApi();
                    HttpUtil.getString(url).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String s = response.body().string();
                            LogTool.i(TAG, s);
                            if (s != null) {
                                try {
                                    JSONObject json = new JSONObject(s);
                                    req.appId = json.getString("appid");
                                    req.partnerId = json.getString("partnerid");
                                    req.prepayId = json.getString("prepayid");
                                    req.nonceStr = json.getString("noncestr");
                                    req.timeStamp = json.getString("timestamp");
                                    req.packageValue = json.getString("package");
                                    req.sign = json.getString("sign");
                                    api.sendReq(req);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    return true;*/
                } else if (url.contains("mlxing.chat")) {//进入单聊
                    Intent intent = new Intent(WebkitActivity.this, SChatActivity.class);
                    // it is group chat
                    intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_SINGLE);
                    String u = url.substring(url.lastIndexOf("=") + 1);
                    intent.putExtra("userId", u);
                    Log.i(TAG, "shouldOverrideUrlLoading: ID:" + u);
                    startActivity(intent);
                    return true;
                } else if (url.contains(Constant.LOGIN_URL)) {//拦截登陆
//                    startActivity(new Intent(WebkitActivity.this, LoginActivity.class));

                    //如果有unionid，直接进入主界面
                    String unionid = (String) SPUtils.get(WebkitActivity.this, SPUtils
                            .SP_UNIONID, "");
                    if (!unionid.equals("")) {
                        webView.loadUrl(unionid);
                    } else {
                        UIHelper.gotoHomeLoginActivity(WebkitActivity.this);
                    }
                    return true;
                } else if (url.contains(Constant.URL_CONTACT)) {//拦截进入通讯录
                    UIHelper.goToMainActivity(WebkitActivity.this, 3);
                    return true;
                } else if (url.contains(Constant.URL_MESSAGE)) {//拦截进入会话
                    UIHelper.goToMainActivity(WebkitActivity.this, 4);
                    return true;
                } else if (url.contains("weixin.mlxing.com/wrq/joinGroup")) {//拦截判断是否该加群
                    //http://weixin.mlxing.com/wrq/joinGroup?group_id=0000
                    String id = url.substring(url.lastIndexOf("=") + 1);
                    if (isInGroup(id)) {
                        Intent intent = new Intent(WebkitActivity.this, ChatActivity.class);
                        // it is group chat
                        intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_GROUP);
                        intent.putExtra("userId", id);
                        Log.i(TAG, "shouldOverrideUrlLoading: ID:" + id);
                        startActivity(intent);
                        return true;
                    } else {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                } else if (url.contains("mlxing.qunliao")) {//进入环信页面
                    DemoHelper.getInstance().getContactInfos();

                    if (!DemoHelper.getInstance().isLoggedIn()) {
                        EMChatManager.getInstance().login(SPUtils.get(WebkitActivity.this,
                                SPUtils.USERNAME, "").toString(), SPUtils.get(WebkitActivity
                                .this, SPUtils.PASSWORD, "").toString(), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                UIHelper.goToMainActivity(WebkitActivity.this, 4);
                            }

                            @Override
                            public void onError(int i, String s) {
                            }

                            @Override
                            public void onProgress(int i, String s) {
                            }
                        });
                    } else {
                        UIHelper.goToMainActivity(WebkitActivity.this, 4);
                    }
                    return true;
                } else if (url.contains("mlxing.qrcode")) {//拦截进入扫描二维码
                    Intent intent = new Intent(WebkitActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, WebkitActivity.QRCODE_REQUEST);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler.proceed();//接受证书
                Log.i(TAG,"onReceivedSslError");
                //handleMessage(Message msg); 其他处理
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                checkShowError();
                Log.i(TAG,"onPageFinished");
//                mTitleBar.setTitle(getaTitle());
                code = (int) SPUtils.get(WebkitActivity.this, "js", 9);
                Log.i(TAG, "onPageFinished: " + code);
                if (code == 0 || code == -1 || code == -2) {
                    webView.loadUrl("javascript:_jsCallbackForAppPay(" + code + ")");
                    SPUtils.put(WebkitActivity.this, "js", 9);
                }
                if (webView.canGoBack() || Constant.SET_HRLP.equals(url) || Constant.SET_ABOUT
                        .equals(url) || isShare ) {
                        Log.i(TAG,"onPageFinished.visible");
                    mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                } else {
                    mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
                }
//                if(isJpush){
//                    mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
//                }

                if (url.contains("http://weixin.mlxing.com/zf/ddzf/")) {//支付
//                    String ht = "javascript:window.mlxapp.getBody(document.getElementsByTagName
// ('html')[0].innerHTML);";
                    String ht = "javascript:window.mlxapp.getBody(getPayJson());";
                    webView.loadUrl(ht);
                    onBackPressed();
                }

                webView.loadUrl("javascript:window.mlxapp.getTitle(document.title);");
                //webView.loadUrl("javascript:window.mlxapp.getUserMobile();");


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG,"onReceivedError");
                imgError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
                Log.i(TAG,"onPageStarted");
                checkShowError();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pgWv.setProgress(newProgress);
                Log.i(TAG,"setWebChromeClient.onProgressChanged");
                if (newProgress >= 80) {
                    pgWv.setVisibility(View.GONE);

                } else {
                    pgWv.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged
                        (view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String tit) {
                super.onReceivedTitle(view, tit);
                Log.i(TAG,"setWebChromeClient.onReceivedTitle");
                title = tit;
                    titles.add(title);
                    mTitleBar.setTitle(getaTitle());

                if (webView.canGoBack() || Constant.SET_HRLP.equals(view.getUrl()) || Constant
                        .SET_ABOUT.equals(view.getUrl())) {
                    Log.i(TAG,"onReceivedTitle.visible");
                    mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                } else {
                    mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
                }
            }


            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                Log.i(TAG,"setWebChromeClient.onShowFileChooser");
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;
            }

            //扩展浏览器上传文件
            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0++版本
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String
                    capture) {
                openFileChooserImpl(uploadMsg);
            }
        });

    }


    /**
     * 5.0以下的上传文件
     *
     * @param uploadMsg
     */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void initTitle() {
        Log.i(TAG,"initTitle");
//        mTitleBar.setTitle("美丽行");
        mTitleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.getInstance().creatRightPop(WebkitActivity.this, mTitleBar
                        .getRightLayout(), WebkitActivity.this);
            }
        });
        mTitleBar.setLeftTextVisiable(View.INVISIBLE);
        mTitleBar.setLeftImageResource(R.drawable.mlx_back);
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);

    }

    public void goBack(){

            Log.i(TAG,"onBackPressed");

            if (Constant.Url_TIKU_OLD.equals(tikuUrl)) {

                webView.goBackOrForward(-2);
                return;
            }

            if (webView.canGoBack()) {

                if (titles.size() > 0) {

                    if (title != titles.get(titles.size() - 1)) {

                        title = titles.get(titles.size() - 1);
                        mTitleBar.setTitle(getaTitle());
                    }
                    titles.remove(titles.size() - 1);
                }
                if (titles.size() > 1) {
                    titles.remove(titles.size() - 1);
                    title = titles.get(titles.size() - 1);
                    mTitleBar.setTitle(getaTitle());
                }
                LogTool.i(TAG, titles.toString());
                webView.goBack();
            } else {
                if ("帮助中心".equals(title) || "功能介绍".equals(title) || isShare) {
                    super.onBackPressed();
                } else {

                    moveTaskToBack(true);
                }
            }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-firstTime>2000){
            firstTime = System.currentTimeMillis();
            Toast.makeText(this,"再按一次返回键退出程序",Toast.LENGTH_SHORT).show();
        }else {
            finish();
            System.exit(0);
        }
    }

    public String getaTitle() {
        if (title.length() > 7) {
            title = title.substring(0, 7);
        }
        return title;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        //接收二维码返回的消息，判断是否加入群聊天
        if (requestCode == QRCODE_REQUEST && resultCode == QRCODE_RESULT) {
            String url = data.getStringExtra("qr");
            String id = url.substring(url.lastIndexOf("=") + 1);
            if (isInGroup(id)) {
                Intent intent = new Intent(WebkitActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", id);
                Log.i(TAG, "shouldOverrideUrlLoading: ID:" + id);
                startActivity(intent);
            } else {
                webView.loadUrl(url);
            }


        }

        //上传图片的
        if (mFilePathCallback == null && mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }


        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == INPUT_FILE_REQUEST_CODE) {

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(this);
        code = (int) SPUtils.get(this, SPUtils.SP_JS, 9);
        Log.i(TAG, "onResume: " + code);
        if (code == 0 || code == -1 || code == -2) {
            webView.loadUrl("javascript:_jsCallbackForAppPay(" + code + ")");
            SPUtils.put(this, SPUtils.SP_JS, 9);
        }
        updateUnreadAddressLable();
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event
                        .EventConversationListChanged});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    //下面是消息通知相关

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
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
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
//                    mTitleBar.setRightDotVisiable(View.VISIBLE);
                    btn_chat_dot.setVisibility(View.VISIBLE);
                    btn_chat_nodot.setVisibility(View.GONE);
                    SPUtils.put(WebkitActivity.this, SPUtils.SP_DOT, SPUtils.isDot);
                } else {
//                    mTitleBar.setRightDotVisiable(View.GONE);
                    btn_chat_dot.setVisibility(View.GONE);
                    btn_chat_nodot.setVisibility(View.VISIBLE);
                    SPUtils.put(WebkitActivity.this, SPUtils.SP_DOT, SPUtils.noDot);

                }
            }
        });

    }

    /**
     * 刷新消息数
     */
    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadAddressLable();

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

    /**
     * 监听刷新未读信息事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        Log.i(TAG, "onEvent: ");

        refreshUIWithMessage();


    }


    /**
     * 是否在群里
     *
     * @param id
     * @return
     */
    public boolean isInGroup(String id) {
        List<EMGroup> groups = new ArrayList<>();
        groups = EMGroupManager.getInstance().getAllGroups();

        for (int i = 0; i < groups.size(); i++) {
            if (id.equals(groups.get(i).getGroupId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 回到首页
     *
     * @param view
     */
    public void click(View view) {


        if (!DemoHelper.getInstance().isLoggedIn()) {

            /*EMChatManager.getInstance().login(SPUtils.get(this, SPUtils.USERNAME, "").toString
            (), SPUtils.get(this, SPUtils.PASSWORD, "").toString(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    UIHelper.goToMainActivity(WebkitActivity.this, 4);
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });*/
            UIHelper.gotoHomeLoginActivity(this);
        } else {
            DemoHelper.getInstance().getContactInfos();
            UIHelper.goToMainActivity(this, 4);

        }
    }

    /**
     * 重新加载网络
     *
     * @param v
     */
    public void netClick(View v) {
        webView.reload();
        imgError.setVisibility(View.GONE);
    }

    /**
     * 是否显示网络错误页面
     */
    private void checkShowError() {
        if (!NetUtils.hasNetwork(getApplicationContext())) {
            imgError.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取分享信息--友盟sdk
     */
    public void share() {
        webView.loadUrl("javascript:window.mlxapp.setTitle(document.title);");
        webView.loadUrl("javascript:window.mlxapp.getShareInfo(getShareInfo());");

        final UMImage image = new UMImage(mContext, R.drawable.mlx_icon);
        new ShareAction((Activity) mContext).setDisplayList(new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE})
                .addButton("umshare", "umshare", "em_add", "em_add")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        Log.i(TAG, "onclickword: " + snsPlatform.mKeyword);
                        if (snsPlatform.mKeyword.equals("umshare")) {

                            UIHelper.gotoShareGroup(mContext);
                        } else {

                            if (share_media == SHARE_MEDIA.WEIXIN) {
                                new ShareAction((Activity) mContext)
                                        .setPlatform(share_media)
                                        .withText("你要的大咖导游，都在这里，戳")
                                        .withTargetUrl(Constant.POP_SHARE)
                                        .withMedia(image)
                                        .share();
                            } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                                new ShareAction((Activity) mContext)
                                        .setPlatform(share_media)
                                        .withText("你要的大咖导游，都在这里，戳")
                                        .withTargetUrl(Constant.POP_SHARE)
                                        .withMedia(image)
                                        .share();
                            }


/*
                                    new ShareAction((Activity) mcontext)
                                            .setDisplayList(new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN,
                                             SHARE_MEDIA.WEIXIN_CIRCLE})
                                            .withText("你要的大咖导游，都在这里，戳")
                                            .withTargetUrl(Constant.POP_SHARE)
                                            .withMedia(image)
                                            .open();*/
                        }
                    }
                }).open();
    }


    class InJavaScriptGetBody {

        @JavascriptInterface
        public void getBody(String body) {
            LogTool.i(TAG, "body:" + body);
            final PayReq req = new PayReq();
            //支付
            body = body.substring(body.indexOf("{"), body.indexOf("}") + 1);
            LogTool.i(TAG, "body2:" + body);
            //body=body.replaceAll("\\","");
            api = DemoApplication.getApi();
            if (body != null) {
                try {
                    JSONObject json = new JSONObject(body);
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
                    api.sendReq(req);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @JavascriptInterface
        public void getTitle(String title) {
            mTitleBar.setTitle(title);


        }
        @JavascriptInterface
        public void setTitle(String title) {
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_TITLE, title);
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_CONTENT, "某某分享了一个链接");
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_URL, Constant.POP_SHARE);

        }

        @JavascriptInterface
        public void getShareInfo(String info) {
            //{ content:neirong,url:wangzhi,title:bitoai  }
            try {
                JSONObject jsonObject = new JSONObject(info);
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                String url = jsonObject.getString("url");
                SPUtils.put(getApplicationContext(), EaseConstant.SHARE_TITLE, title);
                SPUtils.put(getApplicationContext(), EaseConstant.SHARE_CONTENT, content);
                SPUtils.put(getApplicationContext(), EaseConstant.SHARE_URL, url);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @JavascriptInterface
        public String getUserMobile() {
            String phone = (String) SPUtils.get(getApplicationContext(), SPUtils.PHONE, "none");
            LogTool.i(TAG, phone);
            return phone;
        }

        @JavascriptInterface
        public void gotoNewWebView(String url) {
            UIHelper.goToNewWebView(WebkitActivity.this, url, false);
        }

        @JavascriptInterface
        public void mlxShare(final String title, final String content, final String shareUrl) {
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_TITLE, title);
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_CONTENT, content);
            SPUtils.put(getApplicationContext(), EaseConstant.SHARE_URL, shareUrl);

            final UMImage image = new UMImage(mContext, R.drawable.mlx_icon);
            new ShareAction((Activity) mContext).setDisplayList(new SHARE_MEDIA[]{SHARE_MEDIA
                    .WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE})
                    .addButton("umshare", "umshare", "em_add", "em_add")
                    .setShareboardclickCallback(new ShareBoardlistener() {
                        @Override
                        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                            Log.i(TAG, "onclickword: " + snsPlatform.mKeyword);
                            if (snsPlatform.mKeyword.equals("umshare")) {

                                UIHelper.gotoShareGroup(mContext);
                            } else {

                                if (share_media == SHARE_MEDIA.WEIXIN) {
                                    new ShareAction((Activity) mContext)
                                            .setPlatform(share_media).withTitle(title)
                                            .withText(content)
                                            .withTargetUrl(Constant.POP_SHARE)
                                            .withMedia(image)
                                            .share();
                                } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                                    new ShareAction((Activity) mContext)
                                            .setPlatform(share_media).withTitle(title)
                                            .withText(content)
                                            .withTargetUrl(Constant.POP_SHARE)
                                            .withMedia(image)
                                            .share();
                                }

                            }
                        }
                    }).open();
        }
    }
}