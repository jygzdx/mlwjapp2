package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.UIHelper;

/**
 * 用户协议界面
 */
public class DealActivity extends Activity implements View.OnClickListener {

    private Button btnAgree, btnDisagree;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        initView();
        initListener();
        initWebView();
    }

    private void initWebView() {

        webView.setBackgroundColor(0);
        String fileName = "file:///android_asset/xieyi.html";
        webView.loadUrl(fileName);
    }

    private void initListener() {
        btnDisagree.setOnClickListener(this);
        btnAgree.setOnClickListener(this);
    }

    private void initView() {
        btnAgree = (Button) findViewById(R.id.id_agree);
        btnDisagree = (Button) findViewById(R.id.id_disagree);
        webView = (WebView) findViewById(R.id.webView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_agree:
                SPUtils.put(this,SPUtils.SP_DEAL_STATE,true);
                UIHelper.gotoSignActivity(this);
                finish();
                break;
            case R.id.id_disagree:
                SPUtils.put(this,SPUtils.SP_DEAL_STATE,false);
                finish();
                break;

        }
    }
}
