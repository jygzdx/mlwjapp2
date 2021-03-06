package com.mlxing.chatui.daoyou.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.WebkitActivity;
import com.mlxing.chatui.daoyou.utils.zxing.CaptureActivity;
import com.mlxing.chatui.db.InviteMessgeDao;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/3/28.
 */
public class PopupUtils {
    private String TAG = getClass().getSimpleName();
    private static Context mcontext;
    private static PopupUtils popupUtils = new PopupUtils();
    private static Intent intent;
    InviteMessgeDao inviteMessgeDao;
    private PopupWindow popupWindow;
    TextView tv_dot;
    private Bitmap bitmap;

    public static PopupUtils getInstance() {
        return popupUtils;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void creatRightPop(final Context context, View v, Activity activity) {
        mcontext = context;
//        bitmap = getMyShot(activity);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mlx_tou);


        View view = LayoutInflater.from(context).inflate(R.layout.pop_right_menu_layout, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.popwindow);
//        ll.setBackground(new BitmapDrawable(bitmap));

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xfff));
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.showAsDropDown(v, 0, -v.getHeight());

        ButterKnife.bind(this, view);
        /*inviteMessgeDao = new InviteMessgeDao(context);



       /* int count1 = inviteMessgeDao.getUnreadMessagesCount();
        int count2 = EMChatManager.getInstance().getUnreadMsgsCount();
        if ((count1 + count2) > 0) {
            Integer i = count1 + count2;
            tv_dot.setVisibility(View.VISIBLE);
//            tv_dot.setText(i.toString());
//            SPUtils.put(WebkitActivity.this, SPUtils.SP_DOT, SPUtils.isDot);
        } else {
            tv_dot.setVisibility(View.INVISIBLE);
//            SPUtils.put(WebkitActivity.this, SPUtils.SP_DOT, SPUtils.noDot);
        }*/


    }

    /**
     * 获取背景图
     *
     * @param activity
     */
    private Bitmap getMyShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    @OnClick({R.id.pop_chart, R.id.pop_share, R.id.pop_sao, R.id.pop_set, R.id.pop_shou, R.id
            .pop_kefu, R.id.pop_qq, R.id.img_cancel, R.id.popwindow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_chart:
                DemoHelper.getInstance().getContactInfos();

                if (!DemoHelper.getInstance().isLoggedIn()) {

                    EMChatManager.getInstance().login(SPUtils.get(mcontext, SPUtils.USERNAME, "")
                                    .toString(), SPUtils.get(mcontext, SPUtils.PASSWORD, "")
                            .toString(),
                            new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    UIHelper.goToMainActivity(mcontext, 4);
                                    popupWindow.dismiss();
                                }

                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                } else {
                    UIHelper.goToMainActivity(mcontext, 4);
                    popupWindow.dismiss();
                }
                break;
            case R.id.pop_share:
                ((WebkitActivity) ((Activity) mcontext)).share();

                popupWindow.dismiss();
                break;
            case R.id.pop_sao:
                intent = new Intent(mcontext, CaptureActivity.class);
                ((Activity) mcontext).startActivityForResult(intent, WebkitActivity.QRCODE_REQUEST);
                popupWindow.dismiss();
                break;
            case R.id.pop_set:
                UIHelper.goToSetActivity(mcontext);
                popupWindow.dismiss();
                break;
            case R.id.pop_shou:
                UIHelper.goToWebView(mcontext, Constant.URL_WANJIA);
                ((Activity) mcontext).finish();
                popupWindow.dismiss();
                break;
            case R.id.pop_kefu:
                Uri uri = Uri.parse("tel:4007776716");
                intent = new Intent(Intent.ACTION_DIAL, uri);
                mcontext.startActivity(intent);
                popupWindow.dismiss();
                break;
            case R.id.pop_qq:
                UIHelper.goToCustomer(mcontext);
                popupWindow.dismiss();
                break;
            case R.id.img_cancel:
                popupWindow.dismiss();
                break;
            case R.id.popwindow:
                popupWindow.dismiss();
                break;
        }
    }
}
