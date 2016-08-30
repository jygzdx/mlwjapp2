package com.mlxing.chatui.parse;

import android.content.Context;

import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import easeui.domain.EaseUser;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<DemoHelper.DataSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private EaseUser currentUser;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		syncContactInfosListeners = new ArrayList<DemoHelper.DataSyncListener>();
		sdkInited = true;
		return true;
	}

	public void addSyncContactInfoListener(DemoHelper.DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (!syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.add(listener);
		}
	}

	public void removeSyncContactInfoListener(DemoHelper.DataSyncListener listener) {
		if (listener == null) {
			return;
		}
		if (syncContactInfosListeners.contains(listener)) {
			syncContactInfosListeners.remove(listener);
		}
	}





	public void notifyContactInfosSyncListener(boolean success) {
		for (DemoHelper.DataSyncListener listener : syncContactInfosListeners) {
			listener.onSyncComplete(success);
		}
	}

	public boolean isSyncingContactInfoWithServer() {
		return isSyncingContactInfosWithServer;
	}

	public synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}


	public EaseUser getCurrentUserInfo() {
		EaseUser user = new EaseUser(DemoHelper.getInstance().getCurrentUserName());
		user.setAvatar(DemoHelper.getInstance().getCurrentUserAvatar());
		user.setNick(DemoHelper.getInstance().getCurrentUserNickName());
		return user;
	}
}
