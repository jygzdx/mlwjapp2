package com.mlxing.chatui.daoyou.entity;

/**
 * 微信授权返回的数据
 * Created by sunll on 2016/3/8.
 */
public class WxHuanXinEntity {


    /**
     * code : 200
     * error :
     * unionid : oCIF1s97ZErW5hTyplpCqgqHbpP8
     * username : y1hk3j8dgtm3796
     * userpw : aairpbgnkq
     * headImg : http://wx.qlogo.cn/mmopen/Q3auHgzwzM5ebjtH9UqBiaudVtFevStNTWw3icKetickXX0NrWiaPau85QIDibt9eJAUiaf9PCecEUY8QmleKuHJDXRA/0
     * nickName : 林林
     */

    private int code;
    private String error;
    private String unionid;
    private String username;
    private String userpw;
    private String headImg;
    private String nickName;

    public void setCode(int code) {
        this.code = code;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getUnionid() {
        return unionid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserpw() {
        return userpw;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public String toString() {
        return "WxHuanXinEntity{" +
                "code=" + code +
                ", error='" + error + '\'' +
                ", unionid='" + unionid + '\'' +
                ", username='" + username + '\'' +
                ", userpw='" + userpw + '\'' +
                ", headImg='" + headImg + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
