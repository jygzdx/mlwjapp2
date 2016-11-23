package com.mlxing.chatui.daoyou.entity;

/**
 * Created by Administrator on 2016/11/23.
 */
public class LocationVO {
    private String address;
    private String city;
    private float direction;
    private double latitude;
    private double lontitude;
    private float radius;
    private float speed;
    private String time;

    @Override
    public String toString() {
        return "LocationVO{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", direction=" + direction +
                ", latitude=" + latitude +
                ", lontitude=" + lontitude +
                ", radius=" + radius +
                ", speed=" + speed +
                ", time='" + time + '\'' +
                '}';
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLontitude(double lontitude) {
        this.lontitude = lontitude;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {

        return address;
    }

    public String getCity() {
        return city;
    }

    public float getDirection() {
        return direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public float getRadius() {
        return radius;
    }

    public float getSpeed() {
        return speed;
    }

    public String getTime() {
        return time;
    }
}
