package com.mlxing.chatui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.TextMessageBody;
import com.easemob.easeui.R;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;
import com.mlxing.chatui.daoyou.utils.LogTool;
import com.mlxing.chatui.daoyou.utils.SPUtils;
import com.mlxing.chatui.db.DemoDBManager;
import com.mlxing.chatui.db.InviteMessgeDao;
import com.mlxing.chatui.db.UserDao;
import com.mlxing.chatui.domain.EmojiconExampleGroupData;
import com.mlxing.chatui.domain.InviteMessage;
import com.mlxing.chatui.domain.InviteMessage.InviteMesageStatus;
import com.mlxing.chatui.domain.RobotUser;
import com.mlxing.chatui.parse.UserProfileManager;
import com.mlxing.chatui.receiver.CallReceiver;
import com.mlxing.chatui.ui.ChatActivity;
import com.mlxing.chatui.ui.MainActivity;
import com.mlxing.chatui.ui.VideoCallActivity;
import com.mlxing.chatui.ui.VoiceCallActivity;
import com.mlxing.chatui.utils.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import easeui.controller.EaseUI;
import easeui.domain.EaseEmojicon;
import easeui.domain.EaseEmojiconGroupEntity;
import easeui.domain.EaseUser;
import easeui.model.EaseNotifier;
import easeui.utils.EaseCommonUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DemoHelper {
    /**
     * 数据同步listener
     */
    static public interface DataSyncListener {
        /**
         * 同步完毕
         *
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    private Map<String, EaseUser> contactList;

    private Map<String, RobotUser> robotList;

    private  List<String> groupIds;

    private UserProfileManager userProManager;

    private static DemoHelper instance = null;

    private DemoModel demoModel = null;

    /**
     * HuanXin sync groups status listener
     */
    private List<DataSyncListener> syncGroupsListeners;
    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;
    /**
     * HuanXin sync blacklist status listener
     */
    private List<DataSyncListener> syncBlackListListeners;

    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    private boolean alreadyNotified = false;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;

    private String username, nickname, avatar;

    private Context appContext;

    private CallReceiver callReceiver;

    private EMConnectionListener connectionListener;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private LocalBroadcastManager broadcastManager;

    private boolean isGroupAndContactListenerRegisted;

    /**
     * 接收消息但不提醒的群名单
     */
    private List<String> receiveNotNoifyGroup=new ArrayList<>();
    public List<String> getReceiveNotNoifyGroup(){
        if (receiveNotNoifyGroup==null){
            receiveNotNoifyGroup=new ArrayList<>();
        }
        return receiveNotNoifyGroup;
    }
    public void setReceiveNotNoifyGroup(){
        EMChatManager.getInstance().getChatOptions().setGroupsOfNotificationDisabled(receiveNotNoifyGroup);
    }



    private DemoHelper() {
    }

    public synchronized static DemoHelper getInstance() {
        if (instance == null) {
            instance = new DemoHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        if (EaseUI.getInstance().init(context)) {
            appContext = context;

            //if your app is supposed to user Google Push, please set project number
            String projectNumber = "562451699741";
            //不使用GCM推送的注释掉这行
            EMChatManager.getInstance().setGCMProjectNumber(projectNumber);
            //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
            EMChatManager.getInstance().setMipushConfig("2882303761517370134", "5131737040134");

            EMChat.getInstance().setAppInited();
            //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMChat.getInstance().setDebugMode(false);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //调用easeui的api设置providers
            setEaseUIProviders();
            demoModel = new DemoModel(context);
            //设置chat options
            setChatoptions();
            //初始化PreferenceManager
            PreferenceManager.init(context);
            //初始化用户管理类
            getUserProfileManager().init(context);

            //设置全局监听
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();

        }
    }

    private void setChatoptions() {
        //easeui库默认设置了一些options，可以覆盖
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setUseRoster(true);
    }

    public DemoModel getDemoModel() {
        return demoModel;
    }


    protected void setEaseUIProviders() {
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                Log.i(TAG, "getUser:1 " + username);

                Log.i(TAG, "getUser:2 " + getUserInfo(username));
                return getUserInfo(username);
            }
        });

        //不设置，则使用easeui默认的
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return demoModel.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return demoModel.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return demoModel.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {

                if (message == null) {
                    return demoModel.getSettingMsgNotification();
                }

                if (!demoModel.getSettingMsgNotification()) {
                    return false;
                } else {
                    //如果允许新消息提示
                    //屏蔽的用户和群组不提示用户
                    String chatUsename = null;
                    List<String> notNotifyIds = null;
                    // 获取设置的不提示新消息的用户或者群组ids
                    if (message.getChatType() == ChatType.Chat) {
                        chatUsename = message.getFrom();
                        notNotifyIds = demoModel.getDisabledIds();
                    } else {
                        chatUsename = message.getTo();
                        notNotifyIds = demoModel.getDisabledGroups();
                    }

                    Log.i(TAG, "isMsgNotifyAllowed: "+receiveNotNoifyGroup+"+"+message.getFrom()+"+"+message.getTo());
                    if(receiveNotNoifyGroup.contains(message.getTo())){
                        return false;
                    }
                    if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        });
        //设置表情provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                //返回文字表情emoji文本和图片(resource id或者本地路径)的映射map
                return null;
            }
        });

        //不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                //有电话时优先跳转到通话页面
                if (isVideoCalling) {
                    intent = new Intent(appContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    intent = new Intent(appContext, VoiceCallActivity.class);
                } else {
                    ChatType chatType = message.getChatType();
                    if (chatType == ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("userId", message.getTo());
                        if (chatType == ChatType.GroupChat) {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });
    }

    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners() {
        syncGroupsListeners = new ArrayList<DataSyncListener>();
        syncContactsListeners = new ArrayList<DataSyncListener>();
        syncBlackListListeners = new ArrayList<DataSyncListener>();

        isGroupsSyncedWithServer = demoModel.isGroupsSynced();
        isContactsSyncedWithServer = demoModel.isContactSynced();
        isBlackListSyncedWithServer = demoModel.isBacklistSynced();

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
                    onCurrentAccountRemoved();
                } else if (error == EMError.CONNECTION_CONFLICT) {
                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {

                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
                    new Thread() {
                        @Override
                        public void run() {
                            DemoHelper.getInstance().notifyForRecevingEvents();
                        }
                    }.start();
                } else {
                    if (!isGroupsSyncedWithServer) {
                        asyncFetchGroupsFromServer(null);
                    }

                    if (!isContactsSyncedWithServer) {
                        asyncFetchContactsFromServer(null);
                    }

                    if (!isBlackListSyncedWithServer) {
                        asyncFetchBlackListFromServer(null);
                    }
                }
            }
        };


        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);
        //注册连接监听
        EMChatManager.getInstance().addConnectionListener(connectionListener);
        //注册群组和联系人监听
        registerGroupAndContactListener();
        //注册消息事件监听
        registerEventListener();

    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
     */
    public void registerGroupAndContactListener() {
//        if (!isGroupAndContactListenerRegisted) {
        //注册群组变动监听
        EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
        //注册联系人变动监听
        EMContactManager.getInstance().setContactListener(new MyContactListener());
//            isGroupAndContactListenerRegisted = true;
//        }

    }

    /**
     * 群组变动监听
     */
    class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            Log.i(TAG, "onInvitationAccpted:1 " + "收到群邀请");
            updateGroupInfo();

            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
           /* String st3 = appContext.getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + " " + st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            getNotifier().viberateAndPlayTone(msg);*/
            //发送local广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onInvitationAccpted(final String groupId, String inviter, String reason) {
            Log.i(TAG, "onInvitationAccpted:2 " + "同意邀请进群");
            updateGroupInfo();

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            Log.i(TAG, "onInvitationDeclined: 群申请被拒绝");
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            Log.i(TAG, "onUserRemoved: 被踢出群");
            //TODO 提示用户被T了，demo省略此步骤
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            //TODO 提示用户群被解散,demo省略
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            Log.i(TAG, "onApplicationReceived: 申请加入群");
            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            Log.i(TAG, "onApplicationAccept:3 " + "进群申请被同意");
            updateGroupInfo();

            String st4 = appContext.getString(R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + " " + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            getNotifier().viberateAndPlayTone(msg);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            Log.i(TAG, "onApplicationDeclined: 加群申请被拒绝");
            // 加群申请被拒绝，demo未实现
        }
    }

    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Log.i(TAG, "onContactAdded: zengjialianxiren");
            Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            for (String username : usernameList) {
                EaseUser user = getUserInfo(username);
                if (user == null)
                    user = getUserInfo(username);
                // 添加好友时可能会回调added方法两次
//                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
//                }
                toAddUsers.put(username, user);
            }

            localUsers.putAll(toAddUsers);
            //发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            Log.i(TAG, "onContactDeleted: beihsanchu");
            // 被删除
            Map<String, EaseUser> localUsers = DemoHelper.getInstance().getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(String username, String reason) {
            Log.i(TAG, "onContactInvited: beiyaoqing");
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }

            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactAgreed(String username) {
            Log.i(TAG, "onContactAgreed: haoyoushenqing beitongyi");
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactRefused(String username) {
            Log.i(TAG, "onContactRefused: haoyou shenqingbeijujue");
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        getNotifier().viberateAndPlayTone(null);
    }

    /**
     * 账号在别的设备登录
     */
    protected void onConnectionConflict() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        appContext.startActivity(intent);

    }

    /**
     * 账号被移除
     */
    protected void onCurrentAccountRemoved() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_REMOVED, true);
        appContext.startActivity(intent);
    }

    EaseUser user = null;

    public EaseUser getUserInfo(String username) {
        //获取user信息，demo是从内存的好友列表里获取，
        //实际开发中，可能还需要从服务器获取用户信息,
        //从服务器获取的数据，最好缓存起来，避免频繁的网络请求
        checkUpdate();
        if (username.equals(EMChatManager.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        user = getContactList().get(username);
        if (user != null) {

            Log.i(TAG, "haoyougetUserInfo: " + username + user.getNick());
        }

        //获取不在好友列表里的群成员具体信息，即陌生人信息，demo未实现--→已实现,现在全部从群里获取。
        if (user == null && getStrangerList() != null) {
            user = getStrangerList().get(username);
        }
        if (user == null) {//新的陌生人
            /*HttpUtil.getInstance().getUserInfo(username, new HttpUtil.InfoCallBack() {
                @Override
                public void onSuccess(List<EaseUser> result) {
                    saveStranger(result);
                    user = result.get(0);
                    strangerList.put(user.getUsername(), user);
                }
            });*/

            HttpUtil.getUserInfo(username).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s = response.body().string();
                    LogTool.i(TAG, "user:"+s);
                    List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                    if (result != null && result.size() > 0) {
                        saveStranger(result);
                        user = result.get(0);
                        strangerList.put(user.getUsername(), user);
                    }
                }
            });
        }
        return user;
    }


    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                    EMLog.d(TAG, "receive the event : " + event.getEvent() + ",id : " + message.getMsgId());
                }

                switch (event.getEvent()) {
                    case EventNewMessage:
                        //应用在后台，不需要刷新UI,通知栏提示新消息
                        if (!easeUI.hasForegroundActivies()) {
                            getNotifier().onNewMsg(message);
                        }
                        break;
                    case EventOfflineMessage:
                        if (!easeUI.hasForegroundActivies()) {
                            EMLog.d(TAG, "received offline messages");
                            List<EMMessage> messages = (List<EMMessage>) event.getData();
                            getNotifier().onNewMesg(messages);
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {
                        EMLog.d(TAG, "收到透传消息");
                        //获取消息body
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                        final String action = cmdMsgBody.action;//获取自定义action

                        //获取扩展属性 此处省略
                        //message.getStringAttribute("");
                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
                        final String str = appContext.getString(R.string.receive_the_passthrough);

                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);

                        if (broadCastReceiver == null) {
                            broadCastReceiver = new BroadcastReceiver() {

                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                                }
                            };

                            //注册广播接收者
                            appContext.registerReceiver(broadCastReceiver, cmdFilter);
                        }

                        Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                        broadcastIntent.putExtra("cmd_value", str + action);
                        appContext.sendBroadcast(broadcastIntent, null);

                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);
    }

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMChat.getInstance().isLoggedIn();
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * 获取消息通知类
     *
     * @return
     */
    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }

    public DemoModel getModel() {
        return (DemoModel) demoModel;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, EaseUser> contactList) {
        this.contactList = contactList;
    }

    /**
     * 保存单个user
     */
    public void saveContact(EaseUser user) {
        contactList.put(user.getUsername(), user);
        demoModel.saveContact(user);
    }

    public void saveStranger(List<EaseUser> list) {
        demoModel.saveStrangerList(list);
    }


    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        LogTool.i("easecontact",isLoggedIn()+":"+contactList );
        if (isLoggedIn() && contactList == null) {
            contactList = demoModel.getContactList();
        }
        if (contactList == null) {
            return new HashMap<String, EaseUser>();
        }
        LogTool.i("easechatact","返回的:"+contactList );
        return contactList;
    }

    /**
     * 设置当前用户的环信id
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    public void setCurrentUserAvatar(String avatar) {
        this.avatar = avatar;
        demoModel.setCurrentUserAvatar(avatar);
    }

    public void setCurrentUserNickName(String nickname) {
        this.nickname = nickname;
        demoModel.setCurrentUserNickName(nickname);
    }

    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUserName() {
        if (username == null) {
            username = demoModel.getCurrentUserName();
        }
        return username;
    }

    public String getCurrentUserAvatar() {
        if (avatar == null) {
            avatar = demoModel.getCurrentUserAvatar();
        }
        return avatar;
    }

    public String getCurrentUserNickName() {
        if (nickname == null) {
            nickname = demoModel.getCurrentUserNickName();
        }
        return nickname;
    }

    public void setRobotList(Map<String, RobotUser> robotList) {
        this.robotList = robotList;
    }

    public Map<String, RobotUser> getRobotList() {
        if (isLoggedIn() && robotList == null) {
            robotList = demoModel.getRobotList();
        }
        return robotList;
    }

    private Map<String, EaseUser> strangerList;

    /**
     * 获得陌生人列表
     *
     * @return
     */
    public Map<String, EaseUser> getStrangerList() {

        if (isLoggedIn() && strangerList == null || strangerList.size() < 1) {

            strangerList = demoModel.getStrangerList();
            Log.i(TAG, "getStrangerList2: " + strangerList.values().toString());
        }
        if (strangerList == null) {
            return new HashMap<String, EaseUser>();
        }
        return strangerList;

    }


    /**
     * update user list to cach And db
     *
     * @param
     */
    public void updateContactList(List<EaseUser> contactInfoList) {
        for (EaseUser u : contactInfoList) {
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        demoModel.saveContactList(mList);
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncGroupsListeners.contains(listener)) {
            syncGroupsListeners.remove(listener);
        }
    }

    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }

    public void addSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.add(listener);
        }
    }

    public void removeSyncBlackListListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncBlackListListeners.contains(listener)) {
            syncBlackListListeners.remove(listener);
        }
    }

    /**
     * 同步操作，从服务器获取群组列表
     * 该方法会记录更新状态，可以通过isSyncingGroupsFromServer获取是否正在更新
     * 和isGroupsSyncedWithServer获取是否更新已经完成
     *
     * @throws EaseMobException
     */
    StringBuilder sb;

    public synchronized void asyncFetchGroupsFromServer(final EMCallBack callback) {
        if (isSyncingGroupsWithServer) {
            return;
        }

        isSyncingGroupsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    final List<EMGroup> list = EMGroupManager.getInstance().getGroupsFromServer();
                    sb = new StringBuilder();
                    final Map<String, EaseUser> str = new HashMap<String, EaseUser>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    try {
                                        EMGroup g = EMGroupManager.getInstance().getGroupFromServer(list.get(i).getGroupId());
                                        EMGroupManager.getInstance().createOrUpdateLocalGroup(g);

                                        for (final String member : g.getMembers()) {
                                            if (!str.containsKey(member)) {

                                                sb.append(member).append(",");
                                                str.put(member, new EaseUser(member));
                                            }
                                        }


                                    } catch (EaseMobException e) {
                                        e.printStackTrace();
                                    }
                                }
                                LogTool.i(TAG, "同步组信息"+sb.toString());
                                HttpUtil.getUserInfo(sb.toString()).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String s = response.body().string();
                                        List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                                        if (result != null && result.size() > 0) {
                                            for (EaseUser user : result) {
                                                str.put(user.getUsername(), user);

                                            }
                                            strangerList = str;

                                            SPUtils.put(appContext,SPUtils.SP_THEIR_REFRESH,new Date().getTime());
                                        }
                                    }
                                });
                            }
                        }
                    }).start();


                    Log.i(TAG, "run: list:" + list);
                    // in case that logout already before server returns, we should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    demoModel.setGroupsSynced(true);

                    isGroupsSyncedWithServer = true;
                    isSyncingGroupsWithServer = false;

                    //通知listener同步群组完毕
                    noitifyGroupSyncListeners(true);
                    if (isContactsSyncedWithServer()) {
                        notifyForRecevingEvents();
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (EaseMobException e) {
                    demoModel.setGroupsSynced(false);
                    isGroupsSyncedWithServer = false;
                    isSyncingGroupsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }


    public void noitifyGroupSyncListeners(boolean success) {
        for (DataSyncListener listener : syncGroupsListeners) {
            listener.onSyncComplete(success);
        }
    }

    List<String> usernames = null;

    public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback) {
        if (isSyncingContactsWithServer) {
            return;
        }

        isSyncingContactsWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {

                    usernames = EMContactManager.getInstance().getContactUserNames();
                    if (usernames.size()<=0)
                        return;
                    // in case that logout already before server returns, we should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }
                    StringBuilder asb = new StringBuilder();
                    for (final String member : usernames) {
                        asb.append(member).append(",");
                    }

                    HttpUtil.getUserInfo(asb.toString()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String s = response.body().string();
                            List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                            if (result != null && result.size() > 0) {
                                Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                                for (EaseUser user : result) {
                                    EaseCommonUtils.setUserInitialLetter(user);
                                    userlist.put(user.getUsername(), user);

                                }
                                Log.i(TAG, "haoyoushu: " + userlist.size());
                                // 存入内存
                                getContactList().clear();
                                getContactList().putAll(userlist);
                                demoModel.saveStrangerList(result);
                                updateContactList(result);
                                demoModel.setContactSynced(true);
                                EMLog.d(TAG, "set contact syn status to true");

                                demoModel.setContactSynced(true);
                                EMLog.d(TAG, "set contact syn status to true");

                                isContactsSyncedWithServer = true;
                                isSyncingContactsWithServer = false;

                                //通知listeners联系人同步完毕
                                notifyContactsSyncListener(true);
                                if (isGroupsSyncedWithServer()) {
                                    notifyForRecevingEvents();
                                }

                                getUserProfileManager().notifyContactInfosSyncListener(true);

                                if (callback != null) {
                                    callback.onSuccess(usernames);
                                }
                            }
                        }
                    });


                } catch (EaseMobException e) {
                    demoModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    noitifyGroupSyncListeners(false);
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyContactsSyncListener(boolean success) {
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }
    private List<String> blackusernames=null;
    public void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {

        if (isSyncingBlackListWithServer) {
            return;
        }

        isSyncingBlackListWithServer = true;

        new Thread() {
            @Override
            public void run() {
                try {
                    blackusernames = EMContactManager.getInstance().getBlackListUsernamesFromServer();

                    // in case that logout already before server returns, we should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }

                    demoModel.setBlacklistSynced(true);

                    isBlackListSyncedWithServer = true;
                    isSyncingBlackListWithServer = false;

                    EMContactManager.getInstance().saveBlackList(blackusernames);
                    notifyBlackListSyncListener(true);
                    if (callback != null) {
                        callback.onSuccess(blackusernames);
                    }
                } catch (EaseMobException e) {
                    demoModel.setBlacklistSynced(false);

                    isBlackListSyncedWithServer = false;
                    isSyncingBlackListWithServer = true;
                    e.printStackTrace();

                    if (callback != null) {
                        callback.onError(e.getErrorCode(), e.toString());
                    }
                }

            }
        }.start();
    }

    public void notifyBlackListSyncListener(boolean success) {
        for (DataSyncListener listener : syncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }

    public synchronized void notifyForRecevingEvents() {
        if (alreadyNotified) {
            return;
        }

        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
        alreadyNotified = true;
    }

    synchronized void reset() {
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        getUserProfileManager().reset();
        DemoDBManager.getInstance().closeDB();
    }

    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }


    StringBuilder upsb;
    public void updateGroupInfo() {
        new Thread() {
            @Override
            public void run() {
                try {
                    final List<EMGroup> list = EMGroupManager.getInstance().getGroupsFromServer();
                    upsb = new StringBuilder();
                    final Map<String, EaseUser> str = new HashMap<String, EaseUser>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    try {
                                        EMGroup g = EMGroupManager.getInstance().getGroupFromServer(list.get(i).getGroupId());
                                        EMGroupManager.getInstance().createOrUpdateLocalGroup(g);

                                        for (final String member : g.getMembers()) {
                                            if (!str.containsKey(member)) {

                                                upsb.append(member).append(",");
                                                str.put(member, new EaseUser(member));
                                            }
                                        }


                                    } catch (EaseMobException e) {
                                        e.printStackTrace();
                                    }
                                }
                                LogTool.i(TAG, "更新群信息"+upsb.toString());
                                HttpUtil.getUserInfo(upsb.toString()).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String s = response.body().string();
                                        List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                                        if (result != null && result.size() > 0) {
                                            for (EaseUser user : result) {
                                                str.put(user.getUsername(), user);

                                            }
                                            strangerList = str;
                                            saveStranger(result);
                                            SPUtils.put(appContext,SPUtils.SP_THEIR_REFRESH,new Date().getTime());
                                        }
                                    }
                                });
                            }
                        }
                    }).start();


                    Log.i(TAG, "run: list:" + list);
                    // in case that logout already before server returns, we should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }


                } catch (EaseMobException e) {

                }

            }
        }.start();
    }

    List<String> getNames;
    //获取好友从服务器
    public  void getContactInfos(){
        new Thread() {
            @Override
            public void run() {
                try {

                    getNames = EMContactManager.getInstance().getContactUserNames();
                    if (getNames.size()<=0)
                        return;
                    // in case that logout already before server returns, we should return immediately
                    if (!EMChat.getInstance().isLoggedIn()) {
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (final String member : getNames) {
                        sb.append(member).append(",");
                    }

                    HttpUtil.getUserInfo(sb.toString()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String s = response.body().string();
                            List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                            if (result != null && result.size() > 0) {
                                Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                                for (EaseUser user : result) {
                                    EaseCommonUtils.setUserInitialLetter(user);
                                    userlist.put(user.getUsername(), user);

                                }
                                Log.i(TAG, "haoyoushu: " + userlist.size());
                                // 存入内存
                                getContactList().clear();
                                getContactList().putAll(userlist);
                                demoModel.saveStrangerList(result);
                                updateContactList(result);
                                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
                            }
                        }
                    });


                } catch (EaseMobException e) {

                }

            }
        }.start();
    }

    /**
     *刷新自己的信息
     */
    public void refreshMy(){
        LogTool.i(TAG, "开始刷自己" + getCurrentUserName());

        HttpUtil.getUserInfo(getCurrentUserName()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                if (result != null && result.size() > 0) {
                    final EaseUser user = result.get(0);
                    if (user != null) {
                        setCurrentUserAvatar(user.getAvatar());
                        setCurrentUserNickName(user.getNick());
                        SPUtils.put(appContext, SPUtils.SP_MY_REFRESH, new Date().getTime());
                    }
                }
            }
        });
    }

    /**
     * 刷新他人的信息
     */
    public void refreshTheir(){
        SPUtils.put(appContext,SPUtils.SP_THEIR_REFRESH,new Date().getTime());
        getContactInfos();
        updateGroupInfo();
    }

    /**
     * 检查是否会更新信息
     */
    public void checkUpdate(){
        //// TODO: 16-5-10 调用更新
        long now = new Date().getTime();
        long oldMy= (long) SPUtils.get(appContext,SPUtils.SP_MY_REFRESH,now);
        long oldTheir= (long) SPUtils.get(appContext,SPUtils.SP_THEIR_REFRESH,now);
        long myCha= now-oldMy;
        long thCha=now-oldTheir;
        LogTool.i(TAG, "检查时间："+myCha+"："+thCha);
        LogTool.i(TAG, "检查时间：now:"+now);
        LogTool.i(TAG, "检查时间：old:"+oldMy+"："+oldTheir);

        if (myCha>300000){  //300000
            SPUtils.put(appContext, SPUtils.SP_MY_REFRESH, new Date().getTime());
            refreshMy();
            LogTool.i(TAG, "刷新自己");
        }
        if (thCha>84600000){  //84600000
            refreshTheir();
            LogTool.i(TAG, "刷新其他");
        }
    }
}
