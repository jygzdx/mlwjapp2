package com.mlxing.chatui.daoyou.utils;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import easeui.domain.EaseUser;

/**
 * json转换工具类
 *
 * @author quan
 */
public class JsonUtil {

    /**
     * 通过用户名返回
     *
     * @param json
     * @return
     */
    public static List<EaseUser> getUserListFromWxJson(String json) {
        List<EaseUser> list = new ArrayList<EaseUser>();
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            if (jsonObject.getString("code").equals("200")) {
                org.json.JSONArray jsonArray = jsonObject.getJSONArray("result");
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        org.json.JSONObject obj = jsonArray.getJSONObject(i);
                        String name = obj.getString("huanxinAccount");
                        String nick = obj.getString("nickname");
                        String avatar = obj.getString("headimgurl");
                        if (name != null) {
                            EaseUser user = new EaseUser(name);
                            user.setAvatar(avatar);
                            user.setNick(nick);
                            list.add(user);

                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
