/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlxing.chatui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.util.EMLog;
import com.mlxing.chatui.Constant;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.WebkitActivity;
import com.mlxing.chatui.daoyou.utils.ActivityManager;
import com.mlxing.chatui.db.InviteMessgeDao;
import com.mlxing.chatui.db.UserDao;
import com.mlxing.chatui.domain.InviteMessage;

import easeui.utils.EaseCommonUtils;

public class MainActivity extends BaseActivity implements EMEventListener {

    protected static final String TAG = "MainActivity";
    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    private TextView unreadAddressLable;

    private RelativeLayout[] mTabs;
    private Button[] btns;
    private ContactListFragment contactListFragment;
    // private conversationListFragment conversationListFragment;
//	private ChatAllHistoryFragment conversationListFragment;
    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;
    private SettingsFragment settingFragment;
    private GroupFragment groupFragment;
    private Fragment[] fragments;
    private int[] mainImgs = {R.drawable.mlx_wanjia_selector, R.drawable.mlx_wanqun_selector, R.drawable.mlx_home_selector, R.drawable.mlx_tongxunlu_selector, R.drawable.mlx_xiaoxi_selector};
    private int index;
    // 当前fragment的index
    private int currentTabIndex = 3;

    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;

    private FragmentTransaction trx = getSupportFragmentManager().beginTransaction();

    /**
     * 检查当前用户是否被删除
     */
    public boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        currentTabIndex = getIntent().getIntExtra("index", 4);

        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED,
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
        setContentView(R.layout.em_activity_main);
        initView();





        //判断是否需要重新登陆,账号在别的设备登陆或者被移出。demohelper line617  line628
        if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) &&
                !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) &&
                !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);
        conversationListFragment = new ConversationListFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingsFragment();
        groupFragment = new GroupFragment();
        if (currentTabIndex == 3) {

            fragments = new Fragment[]{groupFragment, groupFragment, conversationListFragment, contactListFragment, conversationListFragment};
            // 添加显示第一个fragment
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    contactListFragment)
                    .add(R.id.fragment_container, conversationListFragment).hide(conversationListFragment).show
                    (contactListFragment)
                    .commit();
        } else {
            fragments = new Fragment[]{groupFragment, groupFragment, conversationListFragment, contactListFragment, conversationListFragment};
            // 添加显示第一个fragment
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    contactListFragment)
                    .add(R.id.fragment_container, conversationListFragment).hide(contactListFragment).show
                    (conversationListFragment)
                    .commit();
        }

        // 注册群组和联系人监听
        DemoHelper.getInstance().registerGroupAndContactListener();
        registerBroadcastReceiver();


        //内部测试方法，请忽略
//        registerInternalDebugReceiver();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
        unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
        mTabs = new RelativeLayout[5];
        btns=new Button[5];
        mTabs[0] = (RelativeLayout) findViewById(R.id.btn_container_group);
        mTabs[1] = (RelativeLayout) findViewById(R.id.btn_container_conversation);
        mTabs[2] = (RelativeLayout) findViewById(R.id.btn_container_address_list);

        findViewById(R.id.btn_container_group).setVisibility(View.GONE);
        findViewById(R.id.btn_container_conversation).setVisibility(View.GONE);
        findViewById(R.id.btn_container_address_list).setVisibility(View.GONE);

        mTabs[3] = (RelativeLayout) findViewById(R.id.btn_container_setting);
        mTabs[4] = (RelativeLayout) findViewById(R.id.btn_container_xiaoxi);

        btns[0]= (Button) findViewById(R.id.btn_wanjia);
        btns[1]= (Button) findViewById(R.id.btn_wanqun);
        btns[2]= (Button) findViewById(R.id.btn_home);
        btns[3]= (Button) findViewById(R.id.btn_tongxunlu);
        btns[4]= (Button) findViewById(R.id.btn_xiaoxi);
        // 把第一个tab设为选中状态
        mTabs[currentTabIndex].setSelected(true);
        btns[currentTabIndex].setTextColor(getResources().getColor(R.color.btn_true));
//        for (int i =0;i<mTabs.length;i++) {
//
//            Drawable drawable1 = getResources().getDrawable(mainImgs[i]);
//            drawable1.setBounds(0, 0, 90,96);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//            mTabs[i].setCompoundDrawables(null, drawable1, null, null);//只放上
//
//
//        }
    }

    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_wanjia:
                index = 0;
                Intent intent = new Intent(MainActivity.this, WebkitActivity.class);
                intent.putExtra("startUrl", com.mlxing.chatui.daoyou.Constant.URL_WANJIA);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_wanqun:
                index = 1;
                Intent intent2 = new Intent(MainActivity.this, WebkitActivity.class);
                intent2.putExtra("startUrl", com.mlxing.chatui.daoyou.Constant.URL_WNQUN);
                startActivity(intent2);
                finish();
                break;
            case R.id.btn_home:
                index = 2;
                Intent intent3 = new Intent(MainActivity.this, WebkitActivity.class);
                intent3.putExtra("startUrl", com.mlxing.chatui.daoyou.Constant.URL_SHOUYE);
                startActivity(intent3);
                finish();
                break;
            case R.id.btn_tongxunlu:
                index = 3;

                break;
            case R.id.btn_xiaoxi:
                index = 4;
                break;

        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            if (index == 3) {

                trx.hide(fragments[currentTabIndex]);
                if (!fragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, fragments[index]);
                }
                trx.show(fragments[index]).commit();
            } else if (index == 4) {
                trx.hide(fragments[currentTabIndex]);
                if (!fragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, fragments[index]);
                }
                trx.show(fragments[index]).commit();
            }

            if (contactListFragment != null) {
                contactListFragment.refresh();
            }
        }

        mTabs[currentTabIndex].setSelected(false);
        btns[currentTabIndex].setTextColor(getResources().getColor(R.color.btn_false));
        // 把当前tab设为选中状态

        mTabs[index].setSelected(true);
        btns[index].setTextColor(getResources().getColor(R.color.btn_true));
        currentTabIndex = index;
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        Log.i(TAG, "onEvent: ");
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                DemoHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUIWithMessage();
                break;
            }

            case EventOfflineMessage: {
                refreshUIWithMessage();
                break;
            }

            case EventConversationListChanged: {
                refreshUIWithMessage();
                break;
            }

            default:
                break;
        }
    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (currentTabIndex == 4) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                }
            }
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
    }

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: 2");
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 4) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else {
                    if (contactListFragment != null) {
                        contactListFragment.refresh();
                        contactListFragment.refresh();

                    }
                }
                if (contactListFragment != null) {
                    contactListFragment.refresh();
                    contactListFragment.refresh();

                }
                String action = intent.getAction();
                if (action.equals(Constant.ACTION_GROUP_CHANAGED)) {
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity
                            .class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
//		unregisterBroadcastReceiver();


    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();

        if (count > 0) {
            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
                    unreadAddressLable.setVisibility(View.VISIBLE);
                } else {
                    unreadAddressLable.setVisibility(View.INVISIBLE);
                }
            }
        });

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
            if (conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;


    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        DemoHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 3)
            contactListFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

//        // unregister this event listener when this activity enters the
//        // background
        DemoHelper sdkHelper = DemoHelper.getInstance();
        sdkHelper.pushActivity(this);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }



    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        DemoHelper.getInstance().logout(true, null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
    }

    /**
     * 内部测试代码，开发者请忽略
     */
    private void registerInternalDebugReceiver() {
        internalDebugReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // 重新显示登陆页面
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                    }
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.context_tab_contact, menu);
    }
}
