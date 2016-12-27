package com.mlxing.chatui.daoyou.utils;


import android.util.Log;

import com.mlxing.chatui.daoyou.entity.Customer;
import com.mlxing.chatui.daoyou.entity.PoiInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
     * 获取周边位置poi
     * @param json
     * @return
     */
    public static List<PoiInfo> getPoiInfo(String json) {
        List<PoiInfo> poiInfos = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray pois = obj.getJSONObject("result").getJSONArray("pois");
            for (int i = 0; i < pois.length(); i++) {
                JSONObject poi = pois.getJSONObject(i);
                PoiInfo info = new PoiInfo();
                info.setAddr(poi.getString("addr"));
                info.setName(poi.getString("name"));
                info.setLat(poi.getJSONObject("point").getDouble("y"));
                info.setLng(poi.getJSONObject("point").getDouble("x"));
                Log.i("jsonutil",poi.getString("addr")+"--"+poi.getString("addr"));
                poiInfos.add(info);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return poiInfos;
    }

    public static List<Customer> getCustomerList(String json) {
        List<Customer> list = new ArrayList<Customer>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = null;
            Customer customer = null;
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    String account = jsonObject.getString("huanxin_account");
                    if (account != null) {
                        if (account.trim().length() != 0 && !"null".equals(account)) {
                            customer = new Customer();
                            customer.setHuanxin_account(account);
                            customer.setNickname(jsonObject.getString("nickname"));
                            customer.setHeadimgurl(jsonObject.getString("headimgurl"));
                            list.add(customer);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

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
                        String mid = obj.getString("mid");
                        if (name != null) {
                            EaseUser user = new EaseUser(name);
                            user.setMid(mid);
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
