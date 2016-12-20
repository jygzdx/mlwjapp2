package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.entity.LocationVO;
import com.mlxing.chatui.daoyou.utils.LocationUtil;
import com.mlxing.chatui.daoyou.utils.PopupUtils;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import easeui.widget.EaseTitleBar;

/**
 * 2016/12/13  弃用，直接传到webkitActivity中接收
 */
public class JpushActivity extends Activity {
    private static final String TAG = "JpushActivity";
    private WebView webView;
    private EaseTitleBar mTitleBar;
    private ProgressBar pgWv;
    private ImageView btn_chat_dot, btn_chat_nodot;
    private ImageView imgError;
    private Context mContext;
    private ValueCallback<Uri[]> mFilePathCallback;
    public ValueCallback<Uri> mUploadMessage;
    private static final int HANDLER_GET_LOCATION = 1;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_GET_LOCATION:
                    Bundle bundle = msg.getData();
                    String locHandle = bundle.getString("handle");
                    String city = bundle.getString("city");
                    double lontitude = bundle.getDouble("lontitude");
                    double latitude = bundle.getDouble("latitude");
                    Log.i(TAG, locHandle + "(" + city + "," + lontitude + "," +
                            "" + latitude + ")");
                    webView.loadUrl("javascript:" + locHandle + "('" + city + "','" + lontitude + "','" +
                            "" + latitude + "')");
                    break;
            }
        }

    };
    private CookieManager cookie;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush);
        mContext = this;
        initView();

        setListener();

        initWebView();

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
                Log.i(TAG, "onPageFinished");


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.i(TAG, "onReceivedError");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "onPageStarted");
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
                Log.i(TAG, "setWebChromeClient.onReceivedTitle");
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

    /*private void iniWebView() {
        webView.getSettings().setSupportZoom(true);//是否支持放大
        webView.getSettings().setBuiltInZoomControls(true);//是否支持缩放控制
        webView.getSettings().setDomStorageEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setDisplayZoomControls(false);// 支持缩放
        }


        // Use WideViewport and Zoom out if there is no viewport defined
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new InJavaScriptGetBody(),"mlxapp");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            webView.getSettings().setDisplayZoomControls(false);
        }
        mTitleBar.setTitle("美丽行");
        mTitleBar.setLeftImageResource(R.drawable.mlx_back);

        //WebChromeClient是webView的辅助类,用来监听加载进度等
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //为progressBar设置实时进度.newProgress代表当前页面的加载进度,范围在0-100之间的整数,等于100时加载完毕
                pgWv.setProgress(newProgress);
                //同时要不断更新progressBar.
                pgWv.postInvalidate();
                //当加载进度为100时,隐藏progressBar.
                if (newProgress > 80) {
                    pgWv.setVisibility(View.GONE);
                }else{
                    pgWv.setVisibility(View.VISIBLE);
                }

            }
        };
        //WebViewClient是webView的功能类,可以控制是否使用自己的webView进行加载页面等
        WebViewClient webViewClient = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading: url = "+url);
                return true;
            }
        };
        //为webView设置webChromeClient
        webView.setWebChromeClient(webChromeClient);
        //为webView设置webViewClient
        webView.setWebViewClient(webViewClient);
        //得到从其他界面传过来的url
        Bundle bundle = getIntent().getExtras();
        String jpushUrl = bundle.getString("jpushUrl");
        Log.i(TAG, "iniWebView: url = "+jpushUrl);
//        //设置使用自己的webView加载页面
//        webViewClient.onLoadResource(webView, jpushUrl);
        //webView加载页面
        webView.loadUrl(jpushUrl);
    }*/

    private void initView(){
        webView = (WebView) findViewById(R.id.webView);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        pgWv = (ProgressBar) findViewById(R.id.pg_wv);
//        btn_chat_dot = (ImageView) findViewById(R.id.btn_chat_dot);
        btn_chat_nodot = (ImageView) findViewById(R.id.btn_chat_nodot);
        imgError = (ImageView) findViewById(R.id.img_error);
    }
    private void setListener(){
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.goToWebView(JpushActivity.this, Constant.URL_HOME);
                finish();
            }
        });
        mTitleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.getInstance().creatRightPop(JpushActivity.this, mTitleBar
                        .getRightLayout(), JpushActivity.this);
            }
        });
    }
    class InJavaScriptGetBody{
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
    }
}

