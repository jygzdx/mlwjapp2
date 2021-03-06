package com.mlxing.chatui.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.PathUtil;
import com.mlxing.chatui.Constant;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.domain.EmojiconExampleGroupData;
import com.mlxing.chatui.domain.RobotUser;
import com.mlxing.chatui.widget.ChatRowVoiceCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import easeui.EaseConstant;
import easeui.domain.EaseUser;
import easeui.ui.EaseChatFragment;
import easeui.widget.chatrow.EaseChatRow;
import easeui.widget.chatrow.EaseChatRowShare;
import easeui.widget.chatrow.EaseCustomChatRowProvider;
import easeui.widget.emojicon.EaseEmojiconMenu;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentListener {

    //避免和基类定义的常量可能发生的冲突，常量从11开始定义//删掉了文件12
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_SENT_SHARE=5;
    private static final int MESSAGE_TYPE_RECV_SHARE=6;

    private EMGroup group;
    private List<String> members;

    /**
     * 是否为环信小助手
     */
    private boolean isRobot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    EaseUser user;

    @Override
    protected void setUpView() {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) { // 单聊
            // 设置标题
            user = DemoHelper.getInstance().getUserInfo(toChatUsername);
            if (user == null) {
               /* HttpUtil.getInstance().getUserInfo(toChatUsername, new HttpUtil.InfoCallBack() {
                    @Override
                    public void onSuccess(List<EaseUser> result) {
                        user = result.get(0);
                        if (user != null && !getActivity().isFinishing()) {
                            titleBar.setTitle(user.getNick());
                        }
                    }
                });*/

                HttpUtil.getUserInfo(toChatUsername).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s = response.body().string();
                        final List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                        if (result != null && result.size() > 0) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        user = result.get(0);
                                        if (user != null && !getActivity().isFinishing()) {
                                            titleBar.setTitle(user.getNick());
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            } else {
                titleBar.setTitle(user.getNick());
            }

        }
        setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        super.setUpView();
        titleBar.setLeftImageResource(R.drawable.mlx_back);
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    //将清空消息改为跳转到用户详情
//                    emptyHistory();
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    intent.putExtra("username", toChatUsername);
                    startActivity(intent);
                } else {
                    toGroupDetails();
                }


            }
        });

        if (isShare){
            String title = (String) SPUtils.get(getActivity().getApplicationContext(),EaseConstant.SHARE_TITLE,"title");
            String content = (String) SPUtils.get(getActivity().getApplicationContext(),EaseConstant.SHARE_CONTENT,"content");
            String url = (String) SPUtils.get(getActivity().getApplicationContext(),EaseConstant.SHARE_URL,"url");

            sendShareMessage(title, "fdf", content,url);
        }
    }

    @Override
    protected void registerExtendMenuItem() {
        //demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
        super.registerExtendMenuItem();
        //增加扩展item
        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
                    clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // 转发消息
                    if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
                        Toast.makeText(getActivity(), R.string.chatroom_not_support_forward, Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    break;
                case ContextMenuActivity.RESULT_CODE_FORWARF_GROUP://转发到群组
                    Intent i = new Intent(getActivity(), ForwardMessageGroupActivity.class);
                    i.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(i);
                    break;
                default:
                    break;
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //发送选中的视频
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //发送选中的文件
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //设置消息扩展属性
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        //设置自定义listview item提供者
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == Constant.CHATTYPE_GROUP) {
            EMGroup group = EMGroupManager.getInstance().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, 0).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
        } else if (chatType == Constant.CHATTYPE_CHATROOM) {
            startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {

        //头像点击事件
        if (chatType == EaseConstant.CHATTYPE_SINGLE || username.equals(DemoHelper.getInstance().getCurrentUserName())) {

            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            // 从群聊中进入单聊
            Intent intent = new Intent(getActivity(), SChatActivity.class);

            intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);

            intent.putExtra("userId", username);

            startActivity(intent);
        }

    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        //消息框长按
        startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message),
                REQUEST_CODE_CONTEXT_MENU);
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO: //视频
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE: //一般文件
                //demo这里是通过系统api选择文件，实际app中最好是做成qq那种选择发送文件
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL: //音频通话
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL: //视频通话
                startVideoCall();
                break;

            default:
                break;
        }
        //不覆盖已有的点击事件
        return false;
    }

    /**
     * 选择文件
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //19以后这个api不可用，demo这里简单处理成图库选择图片
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 拨打语音电话
     */
    protected void startVoiceCall() {
        if (!EMChatManager.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, 0).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * 拨打视频电话
     */
    protected void startVideoCall() {
        if (!EMChatManager.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, 0).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种
            return 6;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //语音通话类型
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //视频通话
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_SHARE,false)){
                    return message.direct==EMMessage.Direct.RECEIVE?MESSAGE_TYPE_RECV_SHARE:MESSAGE_TYPE_SENT_SHARE;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // 语音通话,  视频通话
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_SHARE,false)){
                    return new EaseChatRowShare(getActivity(),message,position,adapter);
                }
            }
            return null;
        }
    }

}
