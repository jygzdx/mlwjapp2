package easeui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easeui.domain.EaseUser;
import easeui.utils.DateUtils;
import easeui.utils.EaseCommonUtils;
import easeui.utils.EaseImageUtils;
import easeui.utils.EaseSmileUtils;
import easeui.utils.EaseUserUtils;

/**
 * 会话列表adapter
 *
 */
public class EaseConversationAdapater extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    private boolean isGroup;
    private Context context;
    
    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;
    String[] avatars=new String[5];
    ImageView[] imgs=new ImageView[5];
    EMGroup group;

    public EaseConversationAdapater(Context context, int resource,
                                    List<EMConversation> objects) {
        super(context, resource, objects);
        this.context=context;
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);

    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取与此用户/群组的会话
        EMConversation conversation = getItem(position);
        String username = conversation.getUserName();


/*
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            convertView.setTag(holder);
        }*/


        //判断是不是群组
        ViewHolder holder = new ViewHolder();
        if (conversation.getType() == EMConversationType.GroupChat){
             group = EMGroupManager.getInstance().getGroup(username);
            initGroupTopAvatars(group);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history_4, parent, false);
            holder.avatar1 = (ImageView) convertView.findViewById(R.id.avatar1);
            holder.avatar2 = (ImageView) convertView.findViewById(R.id.avatar2);
            holder.avatar3 = (ImageView) convertView.findViewById(R.id.avatar3);
            holder.avatar4 = (ImageView) convertView.findViewById(R.id.avatar4);
            holder.avatar5 = (ImageView) convertView.findViewById(R.id.avatar5);
            imgs[0]=holder.avatar1;
            imgs[1]=holder.avatar2;
            imgs[2]=holder.avatar3;
            imgs[3]=holder.avatar4;
            imgs[4]=holder.avatar5;
        }else{
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
        }
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
        holder.message = (TextView) convertView.findViewById(R.id.message);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.msgState = convertView.findViewById(R.id.msg_state);
        holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);

        //置顶
        if(conversation.getExtField()!=null){
            if (conversation.getExtField().equals("true")){
                holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem_disable);
            }
        }

        
        if (conversation.getType() == EMConversationType.GroupChat) {
            // 群聊消息，显示群聊头像
            for (int i = 0; i <avatars.length; i++) {
                EaseImageUtils.setImage(context,avatars[i],imgs[i]);
                avatars[i]=null;
//                Glide.with(context).load(avatars[i]).placeholder(R.drawable.ease_default_avatar).into(imgs[i]);
            }
            EMGroup group = EMGroupManager.getInstance().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if(conversation.getType() == EMConversationType.ChatRoom){
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
        }else {
            EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
            EaseUserUtils.setUserNick(username, holder.name);
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                    BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }
        
        //设置自定义属性
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if(primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if(timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }
    
    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }
    

    public void setPrimaryColor(int primaryColor)
    {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }





    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();
                    
                    EMGroup group = EMGroupManager.getInstance().getGroup(username);
                    if(group != null){
                        username = group.getGroupName();
                    }else{
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        if(user != null && user.getNick() != null)
                            username = user.getNick();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else{
                          final String[] words = username.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            conversationList.addAll((List<EMConversation>) results.values);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    }
    
    
    private static class ViewHolder {
        /** 和谁的聊天记录 */
        TextView name;
        /** 消息未读数 */
        TextView unreadLabel;
        /** 最后一条消息的内容 */
        TextView message;
        /** 最后一条消息的时间 */
        TextView time;
        /** 用户头像 */
        ImageView avatar;
        ImageView avatar1;
        ImageView avatar2;
        ImageView avatar3;
        ImageView avatar4;
        ImageView avatar5;
        /** 最后一条消息的发送状态 */
        View msgState;
        /** 整个list中每一行总布局 */
        RelativeLayout list_itease_layout;

    }
    /**
     * 初始化群的前几人头像
     */
    private void initGroupTopAvatars(EMGroup group) {

        if (group!=null){

            for (int i = 0;i<(group.getMembers().size()>5?5:group.getMembers().size());i++){
                EaseUser user = DemoHelper.getInstance().getUserInfo(group.getMembers().get(i));
                if (user!=null){
                    avatars[i]=user.getAvatar();
                }
            }
        }
    }

}

