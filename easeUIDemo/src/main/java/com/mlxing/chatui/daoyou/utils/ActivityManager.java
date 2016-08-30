package com.mlxing.chatui.daoyou.utils;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ActivityManager extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private List<Activity> picList = new ArrayList<>();
    private static ActivityManager instance;
    public synchronized static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public void addPicActivity(Activity activity){
        picList.add(activity);
    }

    public void exitPic(){
        try {
            for (Activity activity : picList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
