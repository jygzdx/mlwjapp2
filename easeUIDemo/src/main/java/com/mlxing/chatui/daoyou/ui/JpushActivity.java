package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.WebkitActivity;
import com.mlxing.chatui.daoyou.utils.PopupUtils;

import easeui.widget.EaseTitleBar;

public class JpushActivity extends Activity {

    private WebView webView;
    private EaseTitleBar mTitleBar;
    private ProgressBar pgWv;
    private ImageView btn_chat_dot, btn_chat_nodot;
    private ImageView imgError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jpush);

        initView();

        setListener();

        iniWebView();

    }

    private void iniWebView() {
        webView.getSettings().setSupportZoom(true);//是否支持放大
        webView.getSettings().setBuiltInZoomControls(true);//是否支持缩放控制

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
        WebViewClient webViewClient = new WebViewClient();
        //为webView设置webChromeClient
        webView.setWebChromeClient(webChromeClient);
        //为webView设置webViewClient
        webView.setWebViewClient(webViewClient);
        //得到从其他界面传过来的url
        Bundle bundle = getIntent().getExtras();
        String jpushUrl = bundle.getString("jpushUrl");
        //设置使用自己的webView加载页面
        webViewClient.onLoadResource(webView, jpushUrl);
        //webView加载页面
        webView.loadUrl(jpushUrl);
    }

    private void initView(){
        webView = (WebView) findViewById(R.id.webView);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        pgWv = (ProgressBar) findViewById(R.id.pg_wv);
        btn_chat_dot = (ImageView) findViewById(R.id.btn_chat_dot);
        btn_chat_nodot = (ImageView) findViewById(R.id.btn_chat_nodot);
        imgError = (ImageView) findViewById(R.id.img_error);
    }
    private void setListener(){
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JpushActivity.this, WebkitActivity.class);
                startActivity(intent);
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
}

