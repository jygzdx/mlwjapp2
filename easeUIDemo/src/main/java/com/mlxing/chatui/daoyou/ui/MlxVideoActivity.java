package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.daoyou.utils.UIHelper;
import com.mlxing.chatui.ui.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MlxVideoActivity extends Activity {

    @BindView(R.id.video)
    VideoView video;

    @BindView(R.id.img_start)
    ImageView imgStart;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlx_video);

        //如果有unionid，直接进入主界面
        String unionid = (String) SPUtils.get(this, SPUtils.SP_UNIONID, "");
        if (!unionid.equals("")) {
            UIHelper.goToWebView(this, (String) SPUtils.get(this, SPUtils.SP_UNIONID, ""));
            finish();
        }
        ButterKnife.bind(this);

        String uri = "android.resource://" + getPackageName() + "/" + R.raw.welcomeapp;
        video.setVideoURI(Uri.parse(uri));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        video.setLayoutParams(layoutParams);
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                video.start();
            }
        });
        video.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        video.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        video.start();
        video.seekTo(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        position = video.getCurrentPosition();
        video.pause();
    }

    @OnClick({R.id.img_start, R.id.img_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_login:
                UIHelper.goToWebView(this, Constant.URL_HOME);
                finish();
                break;
            case R.id.img_start:
                UIHelper.goToWebView(this, Constant.URL_HOME);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
