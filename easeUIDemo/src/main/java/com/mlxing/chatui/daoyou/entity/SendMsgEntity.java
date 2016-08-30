package com.mlxing.chatui.daoyou.entity;

/**
 * 发送手机验证码
 * Created by root on 16-5-20.
 */
public class SendMsgEntity {

    /**
     * code : 1
     * msg : 成功
     */

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
