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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.ui.GroupNoticeActivity;
import com.mlxing.chatui.daoyou.utils.ActivityManager;
import com.mlxing.chatui.daoyou.utils.zxing.CreatQrcodeActivity;

import java.util.ArrayList;
import java.util.List;

import easeui.ui.EaseGroupRemoveListener;
import easeui.utils.EaseUserUtils;
import easeui.widget.EaseAlertDialog;
import easeui.widget.EaseExpandGridView;
import easeui.widget.EaseSwitchButton;

public class GroupDetailsActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    private static final String IS_TOP = "true";
    private static final String NOT_TOP = "false";

    private EaseExpandGridView userGridview;
    private String groupId;
    private ProgressBar loadingPB;
    private Button exitBtn;
    private Button deleteBtn;
    private EMGroup group;
    private GridAdapter adapter;
    private ProgressDialog progressDialog;

    private RelativeLayout rl_switch_block_groupmsg;

    public static GroupDetailsActivity instance;

    String st = "";
    // 清空所有聊天记录
    private RelativeLayout clearAllHistory, groupQrcode, groupNotice, rl_switch_top,rlGroupName,rl_group_black;
    private RelativeLayout blacklistLayout;
    private RelativeLayout changeGroupNameLayout;
    private RelativeLayout idLayout;
    private TextView idText;
    private EaseSwitchButton switchButton, switchBtnTop;
    private GroupRemoveListener groupRemoveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if (group == null) {
            finish();
            return;
        }

        setContentView(R.layout.em_activity_group_details);
        instance = this;
        st = getResources().getString(R.string.people);
        clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
//        blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
        groupQrcode = (RelativeLayout) findViewById(R.id.rl_group_qrcode);
        groupNotice = (RelativeLayout) findViewById(R.id.rl_group_notice);

//        changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
        idLayout.setVisibility(View.VISIBLE);
        idText = (TextView) findViewById(R.id.tv_group_id_value);

        rl_group_black= (RelativeLayout) findViewById(R.id.rl_group_black);
        rl_switch_top = (RelativeLayout) findViewById(R.id.rl_switch_top);
        rlGroupName= (RelativeLayout) findViewById(R.id.rl_group_name);
        rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
        switchBtnTop = (EaseSwitchButton) findViewById(R.id.switch_btn_top);

        groupQrcode.setOnClickListener(this);
        groupNotice.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        rl_switch_top.setOnClickListener(this);
        rlGroupName.setOnClickListener(this);
        rl_group_black.setOnClickListener(this);

        idText.setText(groupId);
        if (group.getOwner() == null || "".equals(group.getOwner())
                || !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            rl_group_black.setVisibility(View.GONE);
            rlGroupName.setEnabled(false);
//            blacklistLayout.setVisibility(View.GONE);
//            changeGroupNameLayout.setVisibility(View.GONE);
        }

//        所有人都不让改群名称
        rlGroupName.setEnabled(false);
        // 如果自己是群主，显示解散按钮
        if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
//            deleteBtn.setVisibility(View.VISIBLE);
        }

        groupRemoveListener = new GroupRemoveListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupRemoveListener);

//        ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "(" + group
//                .getAffiliationsCount() + st);
        ((TextView) findViewById(R.id.group_name)).setText("群资料");
        ((TextView) findViewById(R.id.tv_group_total)).setText("全部群成员" + "(" + group.getAffiliationsCount() + st);
        ((TextView) findViewById(R.id.tv_group_name_value)).setText(group.getGroupName());

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        adapter = new GridAdapter(this, R.layout.em_grid, members);
        userGridview.setAdapter(adapter);

        // 保证每次进详情看到的都是最新的group
        updateGroup();

        // 设置OnTouchListener
        userGridview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://删除模式
                        if (adapter.isInDeleteMode) {
                            adapter.isInDeleteMode = false;
                            adapter.notifyDataSetChanged();
                            return true;
                        } else { //点击群图像添加好友

                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        clearAllHistory.setOnClickListener(this);
//        blacklistLayout.setOnClickListener(this);
//        changeGroupNameLayout.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
                    progressDialog.setMessage(st1);
                    progressDialog.show();
                    addMembersToGroup(newmembers);
                    break;
                case REQUEST_CODE_EXIT: // 退出群
                    progressDialog.setMessage(st2);
                    progressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
                    progressDialog.setMessage(st3);
                    progressDialog.show();
                    deleteGrop();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData)) {
                        progressDialog.setMessage(st5);
                        progressDialog.show();

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    EMGroupManager.getInstance().changeGroupName(groupId,
                                            returnData);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            ((TextView) findViewById(R.id.tv_group_name_value)).setText(group.getGroupName());
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (EaseMobException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 将用户加入群黑名单
     *
     * @param username
     */
    protected void addUserToBlackList(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.Are_moving_to_blacklist));
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().blockUser(groupId, username);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string
                                    .Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.failed_to_move_into,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 刷新群成员
     */
    private void refreshMembers() {
        adapter.clear();

        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        adapter.addAll(members);

        adapter.notifyDataSetChanged();
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast",
                        getString(R.string.dissolution_group_hint)),
                REQUEST_CODE_EXIT_DELETE);

    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {

        EMChatManager.getInstance().clearConversation(group.getGroupId());
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    /**
     * 退出群组
     *
     * @param groupId
     */
    private void exitGrop() {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitFromGroup(groupId);

                    /*Long utime = new Date().getTime();
                    String token = MD5.md5(Constant.TOKENKEY+utime.toString());
                    HttpUtil.getInstance().exitGroup(DemoHelper.getInstance().getCurrentUserName(), groupId, token,utime.toString(),new HttpUtil.CallBack() {
                        @Override
                        public void onSuccess(String result) {
                            Log.i(TAG, "HttpUtils: "+result);
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });*/

                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getResources().getString(R
                                            .string.Exit_the_group_chat_failure) + " " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     *
     * @param groupId
     */
    private void deleteGrop() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        new Thread(new Runnable() {

            public void run() {
                try {
                    // 创建者调用add方法
                    if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                        EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
                    } else {
                        // 一般成员调用invite方法
                        EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
//                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName
//                                    () + "(" + group.getAffiliationsCount()
//                                    + st);
                            ((TextView) findViewById(R.id.tv_group_total)).setText("全体群成员" + "(" + group.getAffiliationsCount() + st);
                            progressDialog.dismiss();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st6 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg: // 屏蔽或取消屏蔽群组
                toggleBlockGroup2();
                break;

            case R.id.clear_all_history: // 清空聊天记录
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                new EaseAlertDialog(GroupDetailsActivity.this, null, st9, null, new
                        EaseAlertDialog.AlertDialogUser() {

                            @Override
                            public void onResult(boolean confirmed, Bundle bundle) {
                                if (confirmed) {
                                    clearGroupHistory();
                                }
                            }
                        }, true).show();

                break;

//            case R.id.rl_blacklist: // 黑名单列表
//                startActivity(new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class)
//                        .putExtra("groupId", groupId));
//                break;

            case R.id.rl_group_name://修改群名称
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data",
                        group.getGroupName()), REQUEST_CODE_EDIT_GROUPNAME);
                break;
            case R.id.rl_group_qrcode://群二维码
                startActivity(new Intent(this, CreatQrcodeActivity.class).putExtra("data", group.getGroupId()));
                break;
            case R.id.rl_group_notice://修改群公告
                startActivity(new Intent(this, GroupNoticeActivity.class).putExtra("groupId", group.getGroupId()));
                break;
            case R.id.rl_switch_top:
                toggleTop();

                break;
            case R.id.rl_group_black://拉黑列表
                startActivity(new Intent(GroupDetailsActivity.this, GroupBlacklistActivity.class)
                        .putExtra("groupId", groupId));
                break;
            default:
                break;
        }

    }

    /**
     * 置顶开关
     */
    private void toggleTop() {
        EMConversation emConversation = EMChatManager.getInstance().getConversation(groupId);
        if (switchBtnTop.isSwitchOpen()) {
            //已经置顶
            emConversation.setExtField(NOT_TOP);
            switchBtnTop.closeSwitch();
        } else {
            emConversation.setExtField(IS_TOP);
            switchBtnTop.openSwitch();
        }

    }

    /**
     * 黑名单开关,屏蔽群消息后，就不能接收到此群的消息 （群创建者不能屏蔽群消息）（还是群里面的成员，但不再接收消息）
     */
    private void toggleBlockGroup() {
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMGroupManager.getInstance().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of,
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }).start();

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMGroupManager.getInstance().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
        }
    }

    /**
     * 黑名单开关,如果群聊只是想提示数目，不响铃。可以通过此属性设置，此属性是本地属性
     */
    private void toggleBlockGroup2() {
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            DemoHelper.getInstance().getReceiveNotNoifyGroup().remove(groupId);
            switchButton.closeSwitch();
            progressDialog.dismiss();
            DemoHelper.getInstance().setReceiveNotNoifyGroup();
            /*new Thread(new Runnable() {
                public void run() {
                    try {
                        EMGroupManager.getInstance().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of,
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }
            }).start();*/

        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            DemoHelper.getInstance().getReceiveNotNoifyGroup().add(groupId);
            switchButton.openSwitch();
            progressDialog.dismiss();
            DemoHelper.getInstance().setReceiveNotNoifyGroup();
            /*new Thread(new Runnable() {
                public void run() {
                    try {
                        EMGroupManager.getInstance().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();*/
        }
    }

    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     */
    private class GridAdapter extends ArrayAdapter<String> {

        private int res;
        public boolean isInDeleteMode;
        private List<String> objects;

        public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
            res = textViewResourceId;
            isInDeleteMode = false;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
            // 最后一个item，减人按钮

            if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                if (position == getCount() - 1) {
                    holder.textView.setText("");
                    // 设置成删除按钮
                    holder.imageView.setImageResource(R.drawable.mlx_group_delete);

                    // 如果不是创建者或者没有相应权限，不提供加减人按钮
                    if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                        // if current user is not group admin, hide add/remove btn
                        convertView.setVisibility(View.GONE);
                    } else { // 显示删除按钮
                        if (isInDeleteMode) {
                            // 正处于删除模式下，隐藏删除按钮
                            convertView.setVisibility(View.GONE);
                        } else {
                            // 正常模式
                            convertView.setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.badge_delete).setVisibility(View.GONE);
                        }
                        final String st10 = getResources().getString(R.string
                                .The_delete_button_is_clicked);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EMLog.d(TAG, st10);
                                isInDeleteMode = true;
                                notifyDataSetChanged();
                            }
                        });
                    }
                } else if (position == getCount() - 2) { // 添加群组成员按钮
                    holder.textView.setText("");
                    holder.imageView.setImageResource(R.drawable.mlx_group_add);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
                    // 如果不是创建者或者没有相应权限
                    if (!group.isAllowInvites() && !group.getOwner().equals(EMChatManager.getInstance
                            ().getCurrentUser())) {
                        // if current user is not group admin, hide add/remove btn
                        convertView.setVisibility(View.GONE);
                    } else {
                        // 正处于删除模式下,隐藏添加按钮
                        if (isInDeleteMode) {
                            convertView.setVisibility(View.GONE);
                        } else {
                            convertView.setVisibility(View.VISIBLE);
                            convertView.findViewById(R.id.badge_delete).setVisibility(View.GONE);
                        }
                        final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EMLog.d(TAG, st11);
                                // 进入选人页面
                                startActivityForResult(
                                        (new Intent(GroupDetailsActivity.this,
                                                GroupPickContactsActivity.class).putExtra("groupId",
                                                groupId)),
                                        REQUEST_CODE_ADD_USER);
                            }
                        });
                    }
                }else{
                    final String username = getItem(position);
                    convertView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);

                    EaseUserUtils.setUserNick(username, holder.textView);
                    EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                    if (isInDeleteMode) {
                        // 如果是删除模式下，显示减人图标
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
                    } else {
                        convertView.findViewById(R.id.badge_delete).setVisibility(View.GONE);
                    }
                    final String st12 = getResources().getString(R.string.not_delete_myself);
                    final String st13 = getResources().getString(R.string.Are_removed);
                    final String st14 = getResources().getString(R.string.Delete_failed);
                    final String st15 = getResources().getString(R.string.confirm_the_members);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isInDeleteMode) {
                                // 如果是删除自己，return
                                if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                    new EaseAlertDialog(GroupDetailsActivity.this, st12).show();
                                    return;
                                }
                                if (!NetUtils.hasNetwork(getApplicationContext())) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EMLog.d("group", "remove user from group:" + username);
                                deleteMembersFromGroup(username);
                            } else {
//							 正常情况下点击user，可以进入用户详情或者聊天页面等等
                                if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                    new EaseAlertDialog(GroupDetailsActivity.this,getResources().getString(R.string.Cant_chat_with_yourself)).show();
                                    return;
                                }
                                startActivity(new
                                        Intent(GroupDetailsActivity.this,
                                        SChatActivity.class).putExtra("userId", username));

                                //点击加好友
//                            addContact(username);

                            }
                        }

                        /**
                         * 删除群成员
                         *
                         * @param username
                         */
                        protected void deleteMembersFromGroup(final String username) {
                            final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
                            deleteDialog.setMessage(st13);
                            deleteDialog.setCanceledOnTouchOutside(false);
                            deleteDialog.show();
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        // 删除被选中的成员
                                        EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
                                        isInDeleteMode = false;
                                        runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                deleteDialog.dismiss();
                                                refreshMembers();
//                                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
//                                                    + group.getAffiliationsCount() + st);
                                                ((TextView) findViewById(R.id.tv_group_total)).setText("全体群成员" + "(" + group.getAffiliationsCount() + st);
                                            }
                                        });
                                    } catch (final Exception e) {
                                        deleteDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                }
                            }).start();
                        }
                    });

                    button.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            if (EMChatManager.getInstance().getCurrentUser().equals(username))
                                return true;
                            if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                                new EaseAlertDialog(GroupDetailsActivity.this, null, st15, null, new EaseAlertDialog.AlertDialogUser() {

                                    @Override
                                    public void onResult(boolean confirmed, Bundle bundle) {
                                        if (confirmed) {
                                            addUserToBlackList(username);
                                        }
                                    }
                                }, true).show();

                            }
                            return false;
                        }
                    });
                }
            } else { // 普通item，显示群组成员
                final String username = getItem(position);
                convertView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                EaseUserUtils.setUserNick(username, holder.textView);
                EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                if (isInDeleteMode) {
                    // 如果是删除模式下，显示减人图标
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
                } else {
                    convertView.findViewById(R.id.badge_delete).setVisibility(View.GONE);
                }
                final String st12 = getResources().getString(R.string.not_delete_myself);
                final String st13 = getResources().getString(R.string.Are_removed);
                final String st14 = getResources().getString(R.string.Delete_failed);
                final String st15 = getResources().getString(R.string.confirm_the_members);
                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInDeleteMode) {
                            // 如果是删除自己，return
                            if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                new EaseAlertDialog(GroupDetailsActivity.this, st12).show();
                                return;
                            }
                            if (!NetUtils.hasNetwork(getApplicationContext())) {
                                Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
                                return;
                            }
                            EMLog.d("group", "remove user from group:" + username);
                            deleteMembersFromGroup(username);
                        } else {
//							 正常情况下点击user，可以进入用户详情或者聊天页面等等
                            if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                new EaseAlertDialog(GroupDetailsActivity.this, "不能和自己聊天").show();
                                return;
                            }
                            startActivity(new
                                    Intent(GroupDetailsActivity.this,
                                    SChatActivity.class).putExtra("userId", username));

                            //点击加好友
//                            addContact(username);

                        }
                    }

                    /**
                     * 删除群成员
                     *
                     * @param username
                     */
                    protected void deleteMembersFromGroup(final String username) {
                        final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
                        deleteDialog.setMessage(st13);
                        deleteDialog.setCanceledOnTouchOutside(false);
                        deleteDialog.show();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // 删除被选中的成员
                                    EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
                                    isInDeleteMode = false;
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            deleteDialog.dismiss();
                                            refreshMembers();
//                                            ((TextView) findViewById(R.id.group_name)).setText(group.getGroupName() + "("
//                                                    + group.getAffiliationsCount() + st);
                                            ((TextView) findViewById(R.id.tv_group_total)).setText("全体群成员" + "(" + group.getAffiliationsCount() + st);
                                        }
                                    });
                                } catch (final Exception e) {
                                    deleteDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), st14 + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }
                        }).start();
                    }
                });

                button.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        if (EMChatManager.getInstance().getCurrentUser().equals(username))
                            return true;
                        if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                            new EaseAlertDialog(GroupDetailsActivity.this, null, st15, null, new EaseAlertDialog.AlertDialogUser() {

                                @Override
                                public void onResult(boolean confirmed, Bundle bundle) {
                                    if (confirmed) {
                                        addUserToBlackList(username);
                                    }
                                }
                            }, true).show();

                        }
                        return false;
                    }
                });
            }
            return convertView;
        }


        @Override
        public int getCount() {
            if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
                return super.getCount();
            }
            return super.getCount() + 2;
        }
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
                    // 更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.tv_group_total)).setText("全体群成员" + "(" + group.getAffiliationsCount() + st);
                            loadingPB.setVisibility(View.GONE);
                            refreshMembers();
                            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                                // 显示解散按钮--改为不显示
                                exitBtn.setVisibility(View.GONE);
                                deleteBtn.setVisibility(View.GONE);
                            } else {
                                // 显示退出按钮
                                exitBtn.setVisibility(View.VISIBLE);
                                deleteBtn.setVisibility(View.GONE);
                            }

                            // update block
//                            if (group.isMsgBlocked()) {
//                                switchButton.openSwitch();
//                            } else {
//                                switchButton.closeSwitch();
//                            }

                           List<String> l = DemoHelper.getInstance().getReceiveNotNoifyGroup();
                            if (l.contains(groupId)){
                                switchButton.openSwitch();
                            }else{
                                switchButton.closeSwitch();
                            }

                            EMConversation emConversation = EMChatManager.getInstance().getConversation(groupId);
                            if (emConversation != null) {
                                if (emConversation.getExtField() != null) {
                                    if (emConversation.getExtField().equals(IS_TOP)) {
                                        switchBtnTop.openSwitch();
                                    } else {
                                        switchBtnTop.closeSwitch();
                                    }
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingPB.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView badgeDeleteView;
    }

    /**
     * 监测群组解散或者被T事件
     */
    private class GroupRemoveListener extends EaseGroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            finish();
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            finish();
        }

    }

    /**
     * 添加联系人
     *
     * @param view
     */
    public void addContact(final String userName) {
        if (EMChatManager.getInstance().getCurrentUser().equals(userName)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(userName)) {
            //提示已在好友列表中(在黑名单列表里)，无需添加
            if (EMContactManager.getInstance().getBlackListUsernames().contains(userName)) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo写死了个reason，实际应该让用户手动填入
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMContactManager.getInstance().addContact(userName, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast
                                    .LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }


}
