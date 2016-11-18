package com.mlxing.chatui.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import easeui.domain.EaseUser;
import easeui.utils.EaseImageUtils;
import easeui.utils.EaseUserUtils;
import easeui.widget.EaseAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 用户信息页面
 */
public class UserProfileActivity extends BaseActivity implements OnClickListener {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private ImageView headAvatar;
    private ImageView headPhotoUpdate;
    private ImageView iconRightArrow;
    private TextView tvNickName;
    private TextView tvUsername;
    private ProgressDialog dialog;
    private RelativeLayout rlNickName;
    private RelativeLayout rlAdd, rlSend, rlBlack, rlDelete;
    private String username;
    private ProgressDialog progressDialog;
    /**
     * 好友申请信息
     */
    private String msg;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);
        initView();
        initListener();
    }

    private void initView() {
        headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
        headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
        tvUsername = (TextView) findViewById(R.id.user_username);
        tvNickName = (TextView) findViewById(R.id.user_nickname);
        rlNickName = (RelativeLayout) findViewById(R.id.rl_nickname);
        iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);
        rlAdd = (RelativeLayout) findViewById(R.id.rl_add_friend);
        rlSend = (RelativeLayout) findViewById(R.id.rl_send_msg);
        rlDelete = (RelativeLayout) findViewById(R.id.rl_delete_chat);
        rlBlack = (RelativeLayout) findViewById(R.id.rl_add_to_black);
    }

    private void initListener() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        boolean enableUpdate = intent.getBooleanExtra("setting", false);
        //  无论如何都跳到可以修改的页面
        //        boolean enableUpdate=true;
        if (enableUpdate) {
            headPhotoUpdate.setVisibility(View.VISIBLE);
            iconRightArrow.setVisibility(View.VISIBLE);
            rlNickName.setOnClickListener(this);
            headAvatar.setOnClickListener(this);
        } else {
            headPhotoUpdate.setVisibility(View.GONE);
            iconRightArrow.setVisibility(View.INVISIBLE);
        }
        rlAdd.setOnClickListener(this);
        rlSend.setOnClickListener(this);
        rlDelete.setOnClickListener(this);
        rlBlack.setOnClickListener(this);

        if (isSelf(username)) {
            rlAdd.setVisibility(View.GONE);
            rlSend.setVisibility(View.GONE);
            rlDelete.setVisibility(View.GONE);
            rlBlack.setVisibility(View.GONE);
        } else if (isFriend(username)) {
            rlAdd.setVisibility(View.GONE);
        }

        if (username != null) {
            if (username.equals(EMChatManager.getInstance().getCurrentUser())) {
                tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
                EaseUserUtils.setUserNick(username, tvUsername);
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
            } else {
                tvUsername.setText(username);
                EaseUserUtils.setUserNick(username, tvUsername);
                EaseUserUtils.setUserNick(username, tvNickName);
                EaseUserUtils.setUserAvatar(this, username, headAvatar);
                asyncFetchUserInfo(username);
            }
        }
    }

    /**
     * 判断是否是自己
     *
     * @param username
     * @return
     */
    private boolean isSelf(String username) {
        return username.equals(DemoHelper.getInstance().getCurrentUserName()) ? true : false;
    }

    private boolean isFriend(String username) {
        return DemoHelper.getInstance().getContactList().containsKey(username) ? true : false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head_avatar:
                uploadHeadPhoto();
                break;
            case R.id.rl_nickname:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                        .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nickString = editText.getText().toString();
                                if (TextUtils.isEmpty(nickString)) {
                                    Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                updateRemoteNick(nickString);
                            }
                        }).setNegativeButton(R.string.dl_cancel, null).show();
                break;
            case R.id.rl_add_friend:
                addContact(username);
                break;
            case R.id.rl_send_msg:
                Log.i("UserProfileActivity","userId="+username);
                startActivity(new Intent(this, SChatActivity.class).putExtra("userId", username));
                break;
            case R.id.rl_delete_chat:
                emptyHistory();
                break;
            case R.id.rl_add_to_black:
                // TODO: 2016/3/30 添加到黑名单
                moveToBlacklist(username);
                break;
            default:
                break;
        }

    }

    public void asyncFetchUserInfo(final String username) {

        //从parse获取
//        DemoHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {
//
//            @Override
//            public void onSuccess(EaseUser user) {
//                if (user != null) {
//
//                    if (isFinishing()) {
//                        return;
//                    }
//                    tvNickName.setText(user.getNick());
//                    if (!TextUtils.isEmpty(user.getAvatar())) {
//                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
//                    } else {
//                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//            }
//        });

        EaseUser user =DemoHelper.getInstance().getStrangerList().get(username);
        if (user == null) {


            //批量获取图像和昵称
           /* HttpUtil.getInstance().getUserInfo(username, new HttpUtil.InfoCallBack() {
                @Override
                public void onSuccess(List<EaseUser> result) {
                    EaseUser user = result.get(0);
                    DemoHelper.getInstance().getDemoModel().saveStrangerList(result);
                    if (user != null) {
                        if (isFinishing()) {
                            return;
                        }
                        DemoHelper.getInstance().getStrangerList().put(username,user);
                        tvUsername.setText(user.getNick());
                        tvNickName.setText(user.getNick());
                        if (!TextUtils.isEmpty(user.getAvatar())) {
                            Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
                        } else {
                            Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
                        }
                    }
                }
            });*/

            HttpUtil.getUserInfo(username).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String s=response.body().string();
                    Log.i("meizhaodao", "asyncFetchUserInfo: " + s);
                    List<EaseUser> result = JsonUtil.getUserListFromWxJson(s);
                    if (result != null && result.size() > 0) {
                        final EaseUser user = result.get(0);
                        DemoHelper.getInstance().getDemoModel().saveStrangerList(result);
                        if (user != null) {
                            if (isFinishing()) {
                                return;
                            }
                            DemoHelper.getInstance().getStrangerList().put(username, user);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvUsername.setText(user.getNick());
                                    tvNickName.setText(user.getNick());
                                    if (!TextUtils.isEmpty(user.getAvatar())) {
                                        EaseImageUtils.setImage(UserProfileActivity.this, user.getAvatar(), headAvatar);
//                                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
                                    } else {
                                        EaseImageUtils.setImage(UserProfileActivity.this, R.drawable.em_default_avatar, headAvatar);
//                                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
                                    }
                                }
                            });


                        }
                    }
                }
            });

        } else {
            tvUsername.setText(user.getNick());
            tvNickName.setText(user.getNick());
            if (!TextUtils.isEmpty(user.getAvatar())) {
                EaseImageUtils.setImage(UserProfileActivity.this, user.getAvatar(), headAvatar);
//                Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
            } else {
                EaseImageUtils.setImage(UserProfileActivity.this, R.drawable.em_default_avatar, headAvatar);
//                Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
            }
        }
    }


    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    private void updateRemoteNick(final String nickName) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            headAvatar.setImageDrawable(drawable);
            uploadUserAvatar(Bitmap2Bytes(photo));
        }

    }

    private void uploadUserAvatar(final byte[] data) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));


        dialog.show();
    }


    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
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
//        progressDialog.show();

        new EaseAlertDialog(this, "申请信息", "加个好友吧", new EaseAlertDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {

                    progressDialog.show();
                    msg = bundle.getString("alert");
                    if (msg != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    EMContactManager.getInstance().addContact(userName, msg);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressDialog.dismiss();
                                            String s1 = getResources().getString(R.string.send_successful);
                                            Toast.makeText(getApplicationContext(), s1, 1).show();
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
            }
        }, true, true).show();

        /*new Thread(new Runnable() {
            public void run() {

                try {
                    //demo写死了个reason，实际应该让用户手动填入
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMContactManager.getInstance().addContact(userName, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, 1).show();
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
        }).start();*/
    }

    /**
     * 点击清空聊天记录
     */
    protected void emptyHistory() {
        String msg = getResources().getString(com.easemob.easeui.R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(this, null, msg, null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    // 清空会话
                    EMChatManager.getInstance().clearConversation(username);

                }
            }
        }, true).show();
    }

    /**
     * 把user移入到黑名单
     */
    protected void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        String st1 = getResources().getString(com.easemob.easeui.R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(com.easemob.easeui.R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(com.easemob.easeui.R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(UserProfileActivity.this, st2, Toast.LENGTH_SHORT).show();
//                            new ContactListFragment().refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(UserProfileActivity.this, st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}
