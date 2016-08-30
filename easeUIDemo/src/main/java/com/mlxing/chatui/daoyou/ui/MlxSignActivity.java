package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.entity.LoginEntity;
import com.mlxing.chatui.daoyou.entity.SendMsgEntity;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.ToastTool;
import com.mlxing.chatui.ui.LoginActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easeui.widget.EaseTitleBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MlxSignActivity extends Activity {

    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.edit_username)
    EditText editUsername;
    @BindView(R.id.edit_pswd1)
    EditText editPswd1;
    @BindView(R.id.edit_pswd2)
    EditText editPswd2;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.image_sign)
    ImageView imageSign;
    @BindView(R.id.btn_getCode)
    Button btnGetCode;

    private int TIME=60;
    private String username, pswd1, pswd2, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mlx_activity_mlx_sign);
        ButterKnife.bind(this);

        titleBar.setLeftImageResource(R.drawable.mlx_back);
        titleBar.setTitle("注册");
        titleBar.setRightLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * 注册
     */
    public void sign() {
        username = editUsername.getText().toString();
        pswd1 = editPswd1.getText().toString();
        pswd2 = editPswd2.getText().toString();
        code = editCode.getText().toString();

        if (isCanSign(username, pswd1, pswd2, code)) {


            HttpUtil.getClient().sign(username, pswd1, code).enqueue(new Callback<LoginEntity>() {
                @Override
                public void onResponse(Call<LoginEntity> call, Response<LoginEntity> response) {
                    final LoginEntity loginEntity = response.body();
                    if ("1".endsWith(loginEntity.getCode())) {

                        ToastTool.showShort(MlxSignActivity.this, loginEntity.getMsg());
                        Intent i = new Intent(MlxSignActivity.this, LoginActivity.class);
                        i.putExtra("user", loginEntity);
                        startActivity(i);
                    } else {
                        ToastTool.showShort(MlxSignActivity.this, loginEntity.getMsg());
                    }
                }

                @Override
                public void onFailure(Call<LoginEntity> call, Throwable t) {
                    ToastTool.showShort(MlxSignActivity.this, "注册失败:" + t.getMessage());
                }
            });
        }
    }

    private boolean isCanSign(String inUsername, String inPswd, String inPswd2, String code) {
        if (code.length() <= 0) {
            ToastTool.showLong(this, "请输入收到的验证码");
            return false;
        }
        if (inUsername.length() != 11) {
            ToastTool.showLong(this, "请输入正确的手机号");
            return false;
        }
        if (inPswd.length() <= 0 || inPswd2.length() <= 0) {
            ToastTool.showLong(this, "密码不能为空");
            return false;
        }
        if (!inPswd.equals(inPswd2)) {
            ToastTool.showLong(this, "两次输入的密码不一致");
            return false;
        }

        return true;

    }

    Integer time = 0;
    Subscription s;

    @OnClick({R.id.btn_getCode, R.id.image_sign})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getCode://获取验证码，倒计时
                username = editUsername.getText().toString();
                if (isCanSend(username)) {
                    sendMsg();
                    time = TIME;
                    btnGetCode.setClickable(false);
                    s = Observable.interval(1, TimeUnit.SECONDS)
                            .repeat(TIME)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    if (time >= 0)
                                        btnGetCode.setText("重新获取(" + time.toString() + ")");
                                    time--;
                                    if (time == -1) {
                                        btnGetCode.setText("重新获取");
                                        btnGetCode.setClickable(true);
                                        s.unsubscribe();
                                    }
                                }
                            });
                }
                break;
            case R.id.image_sign://注册
                sign();
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendMsg() {


        HttpUtil.getClient().sendMsg(username).enqueue(new Callback<SendMsgEntity>() {
            @Override
            public void onResponse(Call<SendMsgEntity> call, Response<SendMsgEntity> response) {
                if (response.body()!=null)
                ToastTool.showLong(getApplicationContext(), response.body().getMsg());
            }

            @Override
            public void onFailure(Call<SendMsgEntity> call, Throwable t) {
                ToastTool.showLong(getApplicationContext(), "获取失败");
                if(s!=null)
                    s.unsubscribe();
                btnGetCode.setText("重新获取");
                btnGetCode.setClickable(true);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (s!=null)
            s.unsubscribe();
    }

    private boolean isCanSend(String inUsername) {
        if (inUsername.length() != 11) {
            ToastTool.showLong(this, "请输入正确的手机号");
            return false;
        }
        return true;

    }

}
