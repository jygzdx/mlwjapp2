package com.mlxing.chatui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.easemob.chat.EMGroup;
import com.mlxing.chatui.R;

import easeui.widget.EaseAlertDialog;

/**
 * Created by root on 16-5-3.
 */
public class ForwardMessageGroupActivity extends GroupsActivity{
    private String forward_msg_id;
    private EMGroup group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forward_msg_id = getIntent().getStringExtra("forward_msg_id");

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                group = groupAdapter.getItem(position - 1);
                new EaseAlertDialog(ForwardMessageGroupActivity.this, null, getString(R.string.confirm_forward_to, group.getGroupName()), null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            if (group == null)
                                return;
                            try {
                                SChatActivity.activityInstance.finish();
                            } catch (Exception e) {
                            }
                            Intent intent = new Intent(ForwardMessageGroupActivity.this, ChatActivity.class);
                            intent.putExtra("chatType", com.mlxing.chatui.Constant.CHATTYPE_GROUP);
                            intent.putExtra("userId", group.getGroupId());
                            intent.putExtra("forward_msg_id", forward_msg_id);
                            startActivity(intent);
                            finish();
                        }
                    }
                }, true).show();
            }
        });

    }
}
