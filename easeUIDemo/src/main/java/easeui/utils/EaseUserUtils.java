package easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.easemob.easeui.R;

import easeui.controller.EaseUI;
import easeui.controller.EaseUI.EaseUserProfileProvider;
import easeui.domain.EaseUser;
public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * 根据username获取相应user
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                EaseImageUtils.setImage(context,avatarResId,imageView);
//                Glide.with(context).load(avatarResId).transform(new GlideCircleTransform(context)).into(imageView);
            } catch (Exception e) {
                //正常的string路径
                EaseImageUtils.setImage(context,user.getAvatar(),imageView);
//                Glide.with(context).load(user.getAvatar()).transform(new GlideCircleTransform(context)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_avatar).into(imageView);
            }
        } else {

                 Glide.with(context).load(R.drawable.ease_default_avatar).transform(new GlideCircleTransform(context)).into(imageView);
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }



}
