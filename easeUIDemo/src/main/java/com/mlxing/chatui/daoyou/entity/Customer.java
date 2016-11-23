package com.mlxing.chatui.daoyou.entity;

/**
 * Created by Administrator on 2016/11/16.
 */
public class Customer {
    private String huanxin_account;
    private String nickname;
    private String headimgurl;
    private int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
    public String getHuanxin_account() {
        return huanxin_account;
    }

    public String getNickname() {
        return nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHuanxin_account(String huanxin_account) {
        this.huanxin_account = huanxin_account;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "huanxin_account='" + huanxin_account + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                '}';
    }
}
