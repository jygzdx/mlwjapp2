package com.mlxing.chatui.daoyou.entity;

/**
 * Created by Administrator on 2016/12/22.
 */
public class PoiInfo {
    private String addr;
    private String name;
    private double lng;
    private double lat;

    @Override
    public String toString() {
        return "PoiInfo{" +
                "addr='" + addr + '\'' +
                ", name='" + name + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                '}';
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
