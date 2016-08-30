package com.mlxing.chatui.ui;

import android.content.Intent;
import android.os.Bundle;

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.ActivityManager;

import easeui.ui.EaseChatFragment;

/**
 * 单人聊天页面，需要fragment的使用{@link #EaseChatFragment}
 *{@link #}
 */
public class SChatActivity extends BaseActivity{
    public static SChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        ActivityManager.getInstance().addActivity(this);
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString("userId");
        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }


    public String getToChatUsername(){
        return toChatUsername;
    }
}