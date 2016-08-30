package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.mlxing.chatui.R;

import easeui.widget.EaseTitleBar;


/**
 * 群公告页面
 */
public class GroupNoticeActivity extends Activity {

    private EditText edtNotice;
    private EaseTitleBar titleBar;
    private Button btnEdt;

    private EMGroup group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mlx_activity_group_notice);

        edtNotice= (EditText) findViewById(R.id.edt_notice);
        titleBar= (EaseTitleBar) findViewById(R.id.title_bar);
        btnEdt= (Button) findViewById(R.id.btn_edt_notice);

        group = EMGroupManager.getInstance().getGroup(getIntent().getStringExtra("groupId"));
        titleBar.setTitle(group.getGroupName());
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edtNotice.setText(group.getDescription());
        if (group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())){
            //是群主
            edtNotice.setEnabled(false);
            btnEdt.setVisibility(View.GONE);
        }else {
            edtNotice.setEnabled(false);
            btnEdt.setVisibility(View.GONE);
        }
    }


    public void onClick(View view) {
        group.setDescription(edtNotice.getText().toString());
        Toast.makeText(GroupNoticeActivity.this, edtNotice.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
