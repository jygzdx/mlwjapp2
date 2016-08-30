package com.mlxing.chatui.ui;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.NetUtils;
import com.mlxing.chatui.Constant;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.PopupUtils;
import com.mlxing.chatui.db.InviteMessgeDao;

import java.util.ArrayList;
import java.util.List;

import easeui.domain.EaseUser;
import easeui.ui.EaseConversationListFragment;


/**
 * 会话列表
 */
public class ConversationListFragment extends EaseConversationListFragment {

    private TextView errorText;
    private List<String> groupTopAvatars=new ArrayList<>();
    private List<EMConversation> conversations;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
    }
    
    @Override
    protected void setUpView() {
//        super.setUpView();
        conversations=loadConversationList();
        conversationList.addAll(conversations);
        conversationListView.init(conversationList);

        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }

        EMChatManager.getInstance().addConnectionListener(connectionListener);
        conversationListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        // 注册上下文菜单
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(EMChatManager.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), SChatActivity.class);
                    if (conversation.isGroup()) {
                        intent = new Intent(getActivity(), ChatActivity.class);
                        if (conversation.getType() == EMConversationType.ChatRoom) {
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        } else {
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }

                    }
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });

        titleBar.setLeftTextVisiable(View.INVISIBLE);
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.getInstance().creatRightPop(getActivity(), titleBar.getRightLayout(),getActivity());
            }
        });

    }

    /**
     * 初始化群的前几人头像
     */
    private void initGroupTopAvatars() {
        for (EMConversation conversation:conversationList) {
            StringBuilder sb = new StringBuilder();
            if (conversation.getType()== EMConversationType.GroupChat){
                String groupid = conversation.getUserName();
                EMGroup group = EMGroupManager.getInstance().getGroup(groupid);
                for (int i = 0;i<(group.getMembers().size()>4?4:group.getMembers().size());i++){
                    EaseUser user = DemoHelper.getInstance().getUserInfo(group.getMembers().get(i));
                    if (user!=null){
                        sb.append(user.getAvatar());
                        if (i<(group.getMembers().size()>4?4:group.getMembers().size())-1){
                            sb.append(",");
                        }
                    }
                }
            }else{
                sb.append("0");
            }
            groupTopAvatars.add(sb.toString());
        }
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())){
         errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
          errorText.setText(R.string.the_current_network);
        }
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu); 
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        /*if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else*/ if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = true;
            handled = true;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        refresh();

        // 更新消息未读数
        ((MainActivity) getActivity()).updateUnreadLabel();
        
        return handled ? true : super.onContextItemSelected(item);
    }

}
