package easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.mlxing.chatui.R;

import easeui.EaseConstant;

/**
 * Created by Administrator on 2016/9/2.
 */
public class EaseChatRowShare extends EaseChatRow {
    private TextView share_title, share_content;
    private ImageView share_image;

    public EaseChatRowShare(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_share : R.layout.ease_row_sent_share, this);
    }

    @Override
    protected void onFindViewById() {
        share_content = (TextView) findViewById(R.id.share_content);
        share_title= (TextView) findViewById(R.id.share_title);
        share_image= (ImageView) findViewById(R.id.share_image);
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        String content = message.getStringAttribute(EaseConstant.SHARE_CONTENT,"kong");
        String image=message.getStringAttribute(EaseConstant.SHARE_IMAGE,"image");
        String title=message.getStringAttribute(EaseConstant.SHARE_TITLE,"title");
        share_content.setText(content);
        share_title.setText(title);
        handleSendMessage();
    }

    private void handleSendMessage() {
        if (message.direct == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status) {
                case CREATE:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                    message.isAcked = true;
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onBubbleClick() {
//点击事件
    }
}
