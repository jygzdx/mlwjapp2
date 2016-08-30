package com.mlxing.chatui.daoyou.entity;

import java.io.Serializable;

/**
 * 电话注册登录返回的数据
 * Created by root on 16-5-16.
 */
public class LoginEntity implements Serializable {

    /**
     * code : 1
     * msg : 成功
     * unionid : aaa
     * username : abc
     * userpw : cba
     * headImg : http://img.jpg
     * nickName : 美丽行
     */

    private String code;
    private String msg;
    private String unionid;
    private String username;
    private String userpw;
    private String headImg;
    private String nickName;

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

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpw() {
        return userpw;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
