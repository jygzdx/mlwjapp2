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
package com.mlxing.chatui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.mlxing.chatui.DemoHelper;
import com.mlxing.chatui.R;

import java.util.List;

import easeui.domain.EaseUser;
import easeui.utils.EaseImageUtils;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

	private LayoutInflater inflater;
	private String newGroup;
	private String addPublicGroup;
	private Context context;
	String[] avatars=new String[5];
	ImageView[] imgs=new ImageView[5];

	public GroupAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.context=context;
		this.inflater = LayoutInflater.from(context);
		newGroup = context.getResources().getString(R.string.The_new_group_chat);
		addPublicGroup = context.getResources().getString(R.string.add_public_group_chat);
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		} else if (position == 1) {
			return 1;
		} else if (position == 2) {
			return 2;
		} else {
			return 3;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == 0) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.em_search_bar_with_padding, null);
			}

			final EditText query = (EditText) convertView.findViewById(R.id.query);
			final ImageButton clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
			query.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					getFilter().filter(s);
					if (s.length() > 0) {
						clearSearch.setVisibility(View.VISIBLE);
					} else {
						clearSearch.setVisibility(View.INVISIBLE);
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				public void afterTextChanged(Editable s) {
				}
			});
			clearSearch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					query.getText().clear();
				}
			});
		}
//        else if (getItemViewType(position) == 1) {
//			if (convertView == null) {
//				convertView = inflater.inflate(R.layout.em_row_add_group, null);
//			}
//			//让新建群组不可见
//
//			((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_create_group);
//			((TextView) convertView.findViewById(R.id.name)).setText(newGroup);
//
//		} else if (getItemViewType(position) == 2) {
//			if (convertView == null) {
//				convertView = inflater.inflate(R.layout.em_row_add_group, null);
//			}
//			//让添加公开群不可见
//
//			((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_add_public_group);
//			((TextView) convertView.findViewById(R.id.name)).setText(addPublicGroup);
//			convertView.findViewById(R.id.header).setVisibility(View.VISIBLE);
//
//
//		}
        else {
            EMGroup group = EMGroupManager.getInstance().getGroup(getItem(position - 1).getGroupId());
			initGroupTopAvatars(group);
			convertView = inflater.inflate(R.layout.em_row_group, null);

			imgs[0]= (ImageView) convertView.findViewById(R.id.avatar1);
			imgs[1]= (ImageView) convertView.findViewById(R.id.avatar2);
			imgs[2]= (ImageView) convertView.findViewById(R.id.avatar3);
			imgs[3]= (ImageView) convertView.findViewById(R.id.avatar4);
			imgs[4]= (ImageView) convertView.findViewById(R.id.avatar5);
			for (int i = 0; i <avatars.length; i++) {
				EaseImageUtils.setImage(context, avatars[i], imgs[i]);
				avatars[i]=null;
//				Glide.with(context).load(avatars[i]).placeholder(com.easemob.easeui.R.drawable.ease_default_avatar).into(imgs[i]);
			}
			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position-1).getGroupName());

		}

		return convertView;
	}

	@Override
    public int getCount() {
        return super.getCount()+1;
    }
//	public int getCount() {
//		return super.getCount() + 3;
//	}


	/**
	 * 初始化群的前几人头像
	 */
	private void initGroupTopAvatars(EMGroup group) {
				for (int i = 0;i<(group.getMembers().size()>5?5:group.getMembers().size());i++){
					EaseUser user = DemoHelper.getInstance().getUserInfo(group.getMembers().get(i));
					if (user!=null){
						avatars[i]=user.getAvatar();
					}
				}
	}

}