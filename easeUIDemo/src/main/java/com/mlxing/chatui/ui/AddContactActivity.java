/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mlxing.chatui.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mlxing.chatui.R;
import com.mlxing.chatui.adapter.CustomerAdapter;
import com.mlxing.chatui.daoyou.entity.Customer;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import easeui.widget.EaseAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddContactActivity extends BaseActivity{
	private static final String TAG = "AddContactActivity";
	private EditText editText;
//	private LinearLayout searchedUserLayout;,nameText
	private TextView mTextView;
	private Button searchBtn;
//	private ImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	private ListView lvShowFriend;
	private List<Customer> customers = new ArrayList<>();

	private CustomerAdapter adapter;
	private static final int HANDLER_GET_FRIEND = 1;
	private static final int HANDLER_GET_FRIEND_FAILURE = 2;
	private static final int HANDLER_CONNECTION_FAILURE = 3;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case HANDLER_CONNECTION_FAILURE:
					searchBtn.setClickable(true);
					Toast.makeText(AddContactActivity.this,"获取朋友信息失败，请检查自己的网络是否有信号！",Toast.LENGTH_SHORT).show();
					break;
				case HANDLER_GET_FRIEND:
					searchBtn.setClickable(true);
					adapter.notifyDataSetChanged();
					break;
				case HANDLER_GET_FRIEND_FAILURE:
					searchBtn.setClickable(true);
					Toast.makeText(AddContactActivity.this,"无法找到该用户，请重新输入！",Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_add_contact);
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		lvShowFriend = (ListView) findViewById(R.id.lv_show_friend);

		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
//		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
//		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
//		avatar = (ImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		adapter = new CustomerAdapter(this,customers);
		lvShowFriend.setAdapter(adapter);
		//设置监听器
		setlistener();

	}

	private void setlistener() {
		lvShowFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String acound = ((Customer) lvShowFriend.getItemAtPosition(position)).getHuanxin_account();
				Intent intent = new Intent(AddContactActivity.this, UserProfileActivity.class);
				intent.putExtra("username",acound);
				startActivity(intent);
			}
		});
	}


	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();

		searchBtn.setClickable(false);

		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				searchBtn.setClickable(true);
				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
				return;
			}


			
//			// TODO 从服务器获取此contact,如果不存在提示不存在此用户,此处没处理
//
//			//服务器存在此用户，显示此用户和添加按钮
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
			//获取搜索朋友数据
			HttpUtil.getFriendInfo(name).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Message msg = handler.obtainMessage();
					msg.what = HANDLER_CONNECTION_FAILURE;
					handler.sendMessage(msg);
					Log.i(TAG, "获取朋友信息失败，请检查自己的网络是否有信号！");
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					String jsonInfo = response.body().string();
					Log.i(TAG,"jsoninfo="+jsonInfo);

					List<Customer> allCustomer = JsonUtil.getCustomerList(jsonInfo);

					if(allCustomer==null||allCustomer.size()==0){
						Message msg = handler.obtainMessage();
						msg.what = HANDLER_GET_FRIEND_FAILURE;
						handler.sendMessage(msg);
					}else {
						Message msg = handler.obtainMessage();
						customers.clear();
						customers.addAll(allCustomer);
						msg.what = HANDLER_GET_FRIEND;
						handler.sendMessage(msg);
					}

				}
			});

		} 
	}	
	
//	/**
//	 *  添加contact
//	 * @param view
//	 */
//	public void addContact(View view){
//		if(EMChatManager.getInstance().getCurrentUser().equals(nameText.getText().toString())){
//			new EaseAlertDialog(this, R.string.not_add_myself).show();
//			return;
//		}
//
//		if(DemoHelper.getInstance().getContactList().containsKey(nameText.getText().toString())){
//		    //提示已在好友列表中(在黑名单列表里)，无需添加
//		    if(EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())){
//		        new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
//		        return;
//		    }
//			new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
//			return;
//		}
//
//		progressDialog = new ProgressDialog(this);
//		String stri = getResources().getString(R.string.Is_sending_a_request);
//		progressDialog.setMessage(stri);
//		progressDialog.setCanceledOnTouchOutside(false);
//		progressDialog.show();
//
//		new Thread(new Runnable() {
//			public void run() {
//
//				try {
//					//demo写死了个reason，实际应该让用户手动填入
//					String s = getResources().getString(R.string.Add_a_friend);
//					EMContactManager.getInstance().addContact(toAddUsername, s);
//					runOnUiThread(new Runnable() {
//						public void run() {
//							progressDialog.dismiss();
//							String s1 = getResources().getString(R.string.send_successful);
//							Toast.makeText(getApplicationContext(), s1, 1).show();
//						}
//					});
//				} catch (final Exception e) {
//					runOnUiThread(new Runnable() {
//						public void run() {
//							progressDialog.dismiss();
//							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
//							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast
//									.LENGTH_LONG).show();
//						}
//					});
//				}
//			}
//		}).start();
//	}
//
	public void back(View v) {
		finish();
	}


}
