package com.mlxing.chatui.daoyou.utils;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mlxing.chatui.daoyou.entity.LocationVO;

/**
 * Created by Administrator on 2016/11/23.
 */
public class LocationUtil {
    private final static String TAG = "LocationUtil";

    private Context mContext;
    private static LocationUtil instance;// 单例模式

    private LocationClient mLocationClient;// 定位实例
    private LocationVO result;// 定位结果
    private OnLocationListener locationListener;// 定位结果监听
    private boolean isRealTimeLoc = true;// 是否实时定位

    public LocationUtil(Context context) {
        this.mContext = context;

        result = new LocationVO();
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(new MyLocationListener());
    }

    public static LocationUtil getInstance(Context context) {
        if (instance == null) {
            instance = new LocationUtil(context);
        }
        return instance;
    }

    /**
     * 开始执行定位
     *
     * @param isRealTime(是否实时定位)
     */
    public void startLocation(boolean isRealTime){
        if(mLocationClient == null){
            Log.i(TAG, "初始化定位错误...");
            return;
        }

        isRealTimeLoc = isRealTime;
        InitLocation();
        mLocationClient.start();
    }

    /**
     * 初始化定位项
     */
    public void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 返回定位结果
     *
     * @return
     */
    public LocationVO getLocationResult(){
        if(result == null){
            result = new LocationVO();
        }
        return result;
    }

    /**
     * 获取定位结果监听器
     *
     * @return
     */
    public OnLocationListener getLocationListener() {
        return locationListener;
    }

    /**
     * 设置定位结果监听器
     *
     * @param locationListener
     */
    public void setLocationListener(OnLocationListener locationListener) {
        this.locationListener = locationListener;
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(result == null){
                result = new LocationVO();
            }
            result.setAddress(location.getAddrStr());
            result.setCity(location.getCity());
            result.setDirection(location.getDirection());
            result.setLatitude(location.getLatitude());
            result.setLontitude(location.getLongitude());
            result.setRadius(location.getRadius());
            result.setSpeed(location.getSpeed());
            result.setTime(location.getTime());

            if(locationListener != null){
                locationListener.onLocationResult(result);
            }
            Log.i(TAG, "当前位置：" + location.getAddrStr());
//            UtilPreference.saveString(mContext, "locationCity", location.getCity());
//            UtilPreference.saveString(mContext, "locationAddress", location.getAddrStr());
//            UtilPreference.saveString(mContext, "locationLat", StringUtil.valueOf(location.getLatitude()));
//            UtilPreference.saveString(mContext, "locationLon", StringUtil.valueOf(location.getLongitude()));

            // 如果不是实时定位，第一次定位成功之后停止定位
            if(!isRealTimeLoc){
                isRealTimeLoc = true;
                mLocationClient.stop();
            }
        }
    }

    /**
     * 定位结果监听器
     *
     * @author morton
     *
     */
    public interface OnLocationListener {

        /**
         * 定位结果回调
         */
        public void onLocationResult(LocationVO locationResult);
    }

    /**
     * 停止定位
     */
    public void onStop(){
        if(mLocationClient != null){
            mLocationClient.stop();
        }
    }

}
