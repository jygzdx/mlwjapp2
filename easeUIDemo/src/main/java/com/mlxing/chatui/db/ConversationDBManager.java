package com.mlxing.chatui.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * Created by Administrator on 2016/3/31.
 */
public class ConversationDBManager {
    private static String TAG="ConversationDBManager";
    private static String TABLE_NAME="conversation_list";
    private static String DB_NAME="conversation_list";
    private static String PACKAGE_NAME="com.mlxing.chatui";
    private static String URL="/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME+"/databases/";


    public static SQLiteDatabase getDB(){
        Log.i(TAG, "getDB: "+URL+DB_NAME);
        return null;
    }
}
