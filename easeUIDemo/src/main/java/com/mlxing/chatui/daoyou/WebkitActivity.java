package com.mlxing.chatui.daoyou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.view.LayoutInflater;
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
import android.widget.Button;
import android.widget.CheckBox;
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
import com.mlxing.chatui.daoyou.entity.LocationVO;
import com.mlxing.chatui.daoyou.utils.ActivityManager;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.LocationUtil;
import com.mlxing.chatui.daoyou.utils.LogTool;
import com.mlxing.chatui.daoyou.utils.NetworkUtil;
import com.mlxing.chatui.daoyou.utils.PopupUtils;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.StringUtil;
import com.mlxing.chatui.daoyou.utils.UIHelper;
import com.mlxing.chatui.daoyou.utils.VersionBiz;
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
import java.util.Timer;
import java.util.TimerTask;

import easeui.EaseConstant;
import easeui.widget.EaseTitleBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * webview首页
 * 友盟sdk为完整版sdk
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
    //    private LocationUtil locationUtil;
    private int code;
    private boolean isJpush = false;

    private int mtype;

    private boolean shanshuo;
    private String mpartnerid;
    //js传过来的方法名
    private String handle;

    //计时器
    private Timer timer;
    private TimerTask timerTask;
    //极光推送url
    private String jpushUrl;

    private static final int HANDLER_TIME = 7;
    private static final int HANDLER_SHARE = 8;
    private static final int HANDLER_PUSH = 9;
    private int timeCode;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_PUSH:
                    Log.i(TAG, "handleMessage: jpushUrl = " + jpushUrl);
                    webView.loadUrl(jpushUrl);
                    isJpush = true;
                    break;
                case HANDLER_SHARE:
                    try {
                        String json = (String) msg.obj;
                        Log.i(TAG, "mlxShare1: json= " + json);
                        JSONObject obj = new JSONObject(json);
                        String title = obj.getString("title");
                        int id = obj.getInt("id");
                        String desc = obj.getString("desc");
                        String url = obj.getString("url");
                        UMImage image = new UMImage(mContext, R.drawable.mlx_icon);
                        if (id == 0) {
                            new ShareAction((Activity) mContext)
                                    .setPlatform(SHARE_MEDIA.WEIXIN)
                                    .withTitle(title)
                                    .withText(desc)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                        } else if (id == 1) {
                            new ShareAction((Activity) mContext)
                                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                    .withTitle(title)
                                    .withText(desc)
                                    .withTargetUrl(url)
                                    .withMedia(image)
                                    .share();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_TIME:
                    if (timeCode % 2 == 1) {
                        btn_chat_nodot.setVisibility(View.VISIBLE);
                    } else {
                        btn_chat_nodot.setVisibility(View.GONE);
                    }

                    break;

                case HANDLER_GET_LOCATION:
                    Bundle bundle = msg.getData();
                    String locHandle = bundle.getString("handle");
                    String city = bundle.getString("city");
                    double lontitude = bundle.getDouble("lontitude");
                    double latitude = bundle.getDouble("latitude");
                    Log.i(TAG, locHandle + "(" + city + "," + lontitude + "," +
                            "" + latitude + ")");
                    webView.loadUrl("javascript:" + locHandle + "('" + city + "','" + lontitude +
                            "','" +
                            "" + latitude + "')");
                    break;

                case HANDLER_CHARGE_MONEY_FAILURE:
                    //充值失败
                    webView.loadUrl("javascript:" + handle + "(false" + ")");
                    break;
                case HANDLER_PAY_FAILURE:
                    //付款失败
                    webView.loadUrl("javascript:cancelPay(2" + ")");
                    mpartnerid = "";
                    mtype = 0;
                    break;
                case IS_SAME_VERSION:
                    String serviceAPKVersion = msg.getData().getString("serviceAPKVersion");
                    String url = msg.getData().getString("url");

                    String currVersion = VersionBiz.getVersion(mContext);
                    boolean isSameVersion = VersionBiz.isSameVersion(mContext, currVersion,
                            serviceAPKVersion);
                    String versionName = (String) SPUtils.get(mContext, "downapkVersion", " ");
                    if (!isSameVersion) {
                        if (!versionName.equals(serviceAPKVersion)) {
                            if (NetworkUtil.isWifi(mContext)) {
                                Log.i(TAG, "wifi下载");
                                //下载apk
                                VersionBiz versionBiz = new VersionBiz(url);
                                versionBiz.downApk(serviceAPKVersion, handler, mContext);
                            } else {
                                Log.i(TAG, "不是WiFi状态");
                            }
                        }
                    } else {
                        Log.i(TAG, "不用更新版本");
                    }
                    break;
                case Constant.HANDLER_DOWNLOAD_SUCCESE:
                    final String apkSavePath = (String) msg.obj;
                    Log.i(TAG, "apkSavePath=" + apkSavePath);

                    boolean isSelected = (boolean) SPUtils.get(mContext, "isSelected", false);
                    if (!isSelected) {
                        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();

                        View mView = LayoutInflater.from(mContext).inflate(R.layout
                                .dialog_updata, null);
                        final CheckBox cb_selected = (CheckBox) mView.findViewById(R.id
                                .dialog_selected);
                        Button btn_positive = (Button) mView.findViewById(R.id.dialog_Positive);
                        Button btn_negative = (Button) mView.findViewById(R.id.dialog_Negative);

                        btn_negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean isSelected = cb_selected.isSelected();
                                SPUtils.put(mContext, "isSelected", isSelected);
                                dialog.dismiss();
                            }
                        });
                        btn_positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SPUtils.put(mContext, "isSelected", false);
                                //跳转到apk安装页面
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                File file = new File(apkSavePath);
                                Uri uri = Uri.fromFile(file);
                                String type = "application/vnd.android.package-archive";
                                intent.setDataAndType(uri, type);
                                startActivity(intent);
                            }
                        });

                        dialog.setView(mView);
                        dialog.show();
                    }
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
        Log.i(TAG, "onCreate");
        ActivityManager.getInstance().addActivity(this);
        initView();
        initTitle();
        initWebView();
        mContext = this;
        String url = getIntent().getStringExtra("startUrl");

        LogTool.i(TAG, "oncreate:" + url);

        checkVersion();

        webView.loadUrl(url);

        inviteMessgeDao = new InviteMessgeDao(this);

        // 注册群组和联系人监听
        DemoHelper.getInstance().registerGroupAndContactListener();
        registerBroadcastReceiver();
        jpushUrl = getIntent().getStringExtra("jpushUrl");
        if (jpushUrl != null) {
            handler.sendEmptyMessageDelayed(HANDLER_PUSH, 2000);
        }
    }

    private static final int IS_SAME_VERSION = 1;

    private void checkVersion() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://weixin.mlxing" +
                ".com/vers/get_version?class=3").build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("onfailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Log.i("body", body);
                try {
                    JSONObject obj = new JSONObject(body);
                    String serviceAPKVersion = obj.getString("version");
                    String url = obj.getString("url");

                    Message message = handler.obtainMessage();
                    message.what = IS_SAME_VERSION;
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    bundle.putString("serviceAPKVersion", serviceAPKVersion);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        pgWv = (ProgressBar) findViewById(R.id.pg_wv);
//        btn_chat_dot = (ImageView) findViewById(R.id.btn_chat_dot);
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
                Log.i(TAG, "shouldOverrideUrlLoading");
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
                } else if (url.contains("mlxing.chat")) {//进入单聊
                    Intent intent = new Intent(WebkitActivity.this, SChatActivity.class);
                    // it is group chat
                    intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_SINGLE);
                    String u = StringUtil.getValueByName(url, "username");
                    intent.putExtra("userId", u);
                    String content = StringUtil.getValueByName(url, "content");
                    if (content != "" && content != null) {
                        intent.putExtra("content", content);

                    }
                    Log.i(TAG, "url=" + url + "shouldOverrideUrlLoading: ID:" + u);
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
                Log.i(TAG, "onReceivedSslError");
                //handleMessage(Message msg); 其他处理
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                checkShowError();
                Log.i(TAG, "onPageFinished  jpush = " + jpushUrl);
//                mTitleBar.setTitle(getaTitle());
                code = (int) SPUtils.get(WebkitActivity.this, "js", 9);
                Log.i(TAG, "onPageFinished: " + code);
                if (code == 0 || code == -1 || code == -2) {
                    webView.loadUrl("javascript:_jsCallbackForAppPay(" + code + ")");
                    SPUtils.put(WebkitActivity.this, "js", 9);
                }
                if (webView.canGoBack() || Constant.SET_HRLP.equals(url) || Constant.SET_ABOUT
                        .equals(url)) {
                    Log.i(TAG, "onPageFinished.visible");
                    mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                } else {
                    mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
                }

                if (url.contains("http://weixin.mlxing.com/zf/ddzf/")) {//支付
                    String ht = "javascript:window.mlxapp.getBody(getPayJson());";
                    webView.loadUrl(ht);
                    onBackPressed();
                }

                webView.loadUrl("javascript:window.mlxapp.getTitle(document.title);");

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG, "onReceivedError");
                imgError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "onPageStarted");
                checkShowError();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pgWv.setProgress(newProgress);
                Log.i(TAG, "setWebChromeClient.onProgressChanged");
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
                Log.i(TAG, "setWebChromeClient.onReceivedTitle+jpush = " + jpushUrl);
                title = tit;
                getaTitle();
                titles.add(title);
                Log.i(TAG, "onReceivedTitle: titles= " + titles.toArray());
                mTitleBar.setTitle(title);

                if (webView.canGoBack() || Constant.SET_HRLP.equals(view.getUrl()) || Constant
                        .SET_ABOUT.equals(view.getUrl())) {
                    Log.i(TAG, "onReceivedTitle.visible");
                    mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                } else {
                    mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
                }
            }


            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams) {
                Log.i(TAG, "setWebChromeClient.onShowFileChooser");
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
        Log.i(TAG, "initTitle");
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

    public void goBack() {

        Log.i(TAG, "goBack()");

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
            if ("帮助中心".equals(title) || "功能介绍".equals(title)) {
                super.onBackPressed();
            } else {

                moveTaskToBack(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstTime > 2000) {
            firstTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
        } else {
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
            Log.i(TAG, "onActivityResult.url=" + url);
            String id = url.substring(url.lastIndexOf("=") + 1);
            if (isInGroup(id)) {
                Intent intent = new Intent(WebkitActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", id);
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
        if (code == 0) {
            //付款成功
            webView.loadUrl("javascript:beginDraw(" + mtype + "," + mpartnerid + ")");
            SPUtils.put(this, SPUtils.SP_JS, 9);
            mpartnerid = "";
            mtype = 0;
        } else {
            //付款失败
            webView.loadUrl("javascript:cancelPay(1" + ")");
            SPUtils.put(this, SPUtils.SP_JS, 9);
            mpartnerid = "";
            mtype = 0;
        }
        if (code == 0) {
            //充值成功
            webView.loadUrl("javascript:" + handle + "(true)");
            handle = "";
            SPUtils.put(this, SPUtils.SP_JS, 9);
        } else {
            //充值失败
            webView.loadUrl("javascript:" + handle + "(false)");
            handle = "";
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
                Log.i(TAG, "updateUnreadAddressLable");
                if ((count1 + count2) > 0) {
//                    mTitleBar.setRightDotVisiable(View.VISIBLE);
//                    btn_chat_dot.setVisibility(View.VISIBLE);
//                    btn_chat_nodot.setVisibility(View.GONE);
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = handler.obtainMessage();
                            msg.what = HANDLER_TIME;
                            timeCode++;
                            handler.sendMessage(msg);
                            Log.i(TAG, "run: handler");
                        }
                    };
                    Log.i(TAG, "timer=" + timer);
                    timer.schedule(timerTask, 500, 500);
                    Log.i(TAG, "run");
                    SPUtils.put(WebkitActivity.this, SPUtils.SP_DOT, SPUtils.isDot);
                } else {
//                    mTitleBar.setRightDotVisiable(View.GONE);
//                    btn_chat_dot.setVisibility(View.GONE);
                    btn_chat_nodot.setVisibility(View.VISIBLE);
                    Log.i(TAG, "run===next");
                    if (timer != null) {
                        timer.cancel();
                    }
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
                                        .withTargetUrl(Constant.URL_HOME)
                                        .withMedia(image)
                                        .withTitle("美丽行")
                                        .share();
                            } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                                new ShareAction((Activity) mContext)
                                        .setPlatform(share_media)
                                        .withText("你要的大咖导游，都在这里，戳")
                                        .withTargetUrl(Constant.URL_HOME)
                                        .withMedia(image)
                                        .withTitle("美丽行")
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

    private static final int HANDLER_CHARGE_MONEY_FAILURE = 5;
    private static final int HANDLER_PAY_FAILURE = 4;
    private static final int HANDLER_GET_LOCATION = 6;

    class InJavaScriptGetBody {
        @JavascriptInterface
        public void sendChatInfo(String username, String content) {
            Log.i(TAG, "sendChatInfo: username=" + username + "  content = " + content);
            Intent intent = new Intent(WebkitActivity.this, SChatActivity.class);
            intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_SINGLE);
            intent.putExtra("userId", username);
            if (content != "" && content != null) {
                intent.putExtra("content", content);
            }
            startActivity(intent);
        }

        @JavascriptInterface
        public void getLocationInfo(String json) {
            Log.i(TAG, "getLocationInfo: json=" + json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                final String handle = jsonObject.getString("handle");
                LocationUtil.getInstance(mContext).setLocationListener(new LocationUtil
                        .OnLocationListener() {
                    @Override
                    public void onLocationResult(LocationVO locationResult) {
                        Log.i(TAG, "locationVo=" + locationResult);
                        String city = locationResult.getCity();
                        double latitude = locationResult.getLatitude();
                        double lontitude = locationResult.getLontitude();
                        String backJson = null;
                        if (city != null) {
                            SPUtils.put(mContext, SPUtils.SP_CITY, city);
                            SPUtils.put(mContext, SPUtils.SP_LONTITUDE, lontitude);
                            SPUtils.put(mContext, SPUtils.SP_LATITUDE, latitude);
                        } else {
                            city = (String) SPUtils.get(mContext, SPUtils.SP_CITY, -1);
                            lontitude = (double) SPUtils.get(mContext, SPUtils.SP_LONTITUDE, -1);
                            latitude = (double) SPUtils.get(mContext, SPUtils.SP_LATITUDE, -1);
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = HANDLER_GET_LOCATION;
                        Bundle bundle = new Bundle();
                        bundle.putString("handle", handle);
                        bundle.putString("city", city);
                        bundle.putDouble("lontitude", lontitude);
                        bundle.putDouble("latitude", latitude);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                });
                LocationUtil.getInstance(mContext).startLocation(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //调起客服界面
        @JavascriptInterface
        public void showCustomer(String number) {
            UIHelper.goToCustomer(mContext);
        }

        @JavascriptInterface
        public void chargeMoney(String json) {
            Log.i(TAG, json);
            try {
                JSONObject jsonObject = new JSONObject(json);
                String total = jsonObject.getString("total_fee");//金额
                handle = jsonObject.getString("handle");//回调方法名
                String mid = (String) SPUtils.get(mContext, SPUtils.MID, " ");
                Log.i(TAG, "handle=" + handle);
                //没有登录直接返回
                if (" ".equals(mid)) {
                    Message msg = handler.obtainMessage();
                    msg.what = HANDLER_CHARGE_MONEY_FAILURE;
                    Log.i(TAG, "没有登录");
                    handler.sendMessage(msg);
                    return;
                }
                //发送充值请求
                HttpUtil.getChargeInfo(total, mid).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "充值支付失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String content = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(content);
                            api = DemoApplication.getApi();
                            if (null != obj && !obj.has("return_code")) {
                                PayReq pay = new PayReq();
                                pay.appId = obj.getString("appid");
                                pay.partnerId = obj.getString("partnerid");//商户id
                                pay.prepayId = obj.getString("prepayid");//商品id
                                pay.nonceStr = obj.getString("noncestr");//随机数
                                pay.timeStamp = obj.getString("timestamp");//时间戳
                                pay.packageValue = obj.getString("package");//预支付id
                                pay.sign = obj.getString("sign");

                                pay.extData = "caipiao";
                                boolean paymes = api.sendReq(pay);
                                Log.i("MainActivity", "充值成功发起支付" + paymes);
                            } else {
                                Log.i("MainActivity", "充值返回错误：" + obj.getString("return_msg"));
                            }
                        } catch (JSONException e) {
                            Log.i("MainActivity", "异常：" + e.getMessage());
                            Toast.makeText(mContext, "异常：" + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void wxPay(int type, String partnerid) {
            mtype = type;
            mpartnerid = partnerid;
            String mid = (String) SPUtils.get(mContext, SPUtils.MID, " ");
            //没有登录直接返回
            if (" ".equals(mid)) {
                Message msg = handler.obtainMessage();
                msg.what = HANDLER_PAY_FAILURE;
                handler.sendMessage(msg);
                return;
            }
            OkHttpClient mOkHttpClient = new OkHttpClient();
            FormBody formBody = new FormBody.Builder().add("class", "2").add("mid", mid).build();

            final Request request = new Request.Builder().url("http://weixin.mlxing" +
                    ".com/activity/indent?trade_type=APP").post(formBody).build();

            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, "一元夺宝支付失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String content = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(content);
                        api = DemoApplication.getApi();
                        if (null != obj && !obj.has("return_code")) {
                            PayReq pay = new PayReq();
                            pay.appId = obj.getString("appid");
                            pay.partnerId = obj.getString("partnerid");//商户id
                            pay.prepayId = obj.getString("prepayid");//商品id
                            pay.nonceStr = obj.getString("noncestr");//随机数
                            pay.timeStamp = obj.getString("timestamp");//时间戳
                            pay.packageValue = obj.getString("package");//预支付id
                            pay.sign = obj.getString("sign");

                            pay.extData = "caipiao";
                            boolean paymes = api.sendReq(pay);
                            Log.i("MainActivity", "成功发起支付" + paymes);

                        } else {
                            Log.i("MainActivity", "返回错误：" + obj.getString("return_msg"));
                        }
                    } catch (JSONException e) {
                        Log.i("MainActivity", "异常：" + e.getMessage());
                        Toast.makeText(mContext, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

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
            Log.i(TAG, "getTitle: javascreptInterface");
            if (isJpush) {
                mTitleBar.setTitle("美丽行");
                Log.i(TAG, "getTitle: isjpush");
                isJpush = false;
            } else {
                Log.i(TAG, "getTitle: notisjpush");
                mTitleBar.setTitle(title);
            }


        }

        @JavascriptInterface
        public void setBackgroundColor() {
            mTitleBar.setTextBackgroundColor(Color.parseColor("#6D6D6D"));
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
        public void share(String json) {
            Message msg = handler.obtainMessage();
            msg.what = HANDLER_SHARE;
            msg.obj = json;
            handler.sendMessage(msg);
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
                                            .withTitle("美丽行")
                                            .share();
                                } else if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                                    new ShareAction((Activity) mContext)
                                            .setPlatform(share_media).withTitle(title)
                                            .withText(content)
                                            .withTargetUrl(Constant.POP_SHARE)
                                            .withMedia(image)
                                            .withTitle("美丽行")
                                            .share();
                                }

                            }
                        }
                    }).open();
        }
    }
}