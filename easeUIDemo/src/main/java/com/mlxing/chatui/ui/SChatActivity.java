package com.mlxing.chatui.ui;

import android.content.Intent;
import android.os.Bundle;

import com.easemob.chat.EMMessage;
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
    String content;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        ActivityManager.getInstance().addActivity(this);
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString("userId");
//        content = getIntent().getExtras().getString("content");
//        if(content.length()>0){
//            //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
//            EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
//            //发送消息
//            EMClient.getInstance().chatManager().sendMessage(message);
//        }

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
