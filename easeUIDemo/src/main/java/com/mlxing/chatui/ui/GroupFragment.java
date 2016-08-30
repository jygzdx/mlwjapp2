package com.mlxing.chatui.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.mlxing.chatui.Constant;
import com.mlxing.chatui.R;
import com.mlxing.chatui.adapter.GroupAdapter;
import com.mlxing.chatui.daoyou.utils.PopupUtils;

import java.util.List;

import easeui.ui.EaseBaseFragment;
import easeui.widget.EaseTitleBar;

/**
 * 首页群聊的fragment
 */
public class GroupFragment extends EaseBaseFragment {
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private GroupAdapter groupAdapter;
    private InputMethodManager inputMethodManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private Context context;
    private EaseTitleBar titleBar;

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            swipeRefreshLayout.setRefreshing(false);
            switch (msg.what) {
                case 0:
                    refresh();
                    break;
                case 1:
                    Toast.makeText(getActivity(), R.string.Failed_to_get_group_chat_information, Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group,null);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inputMethodManager = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        grouplist = EMGroupManager.getInstance().getAllGroups();
        groupListView = (ListView) view.findViewById(R.id.list);
        //show group list
        groupAdapter = new GroupAdapter(context, 1, grouplist);
        groupListView.setAdapter(groupAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                /*EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {

                    @Override
                    public void onSuccess(List<EMGroup> value) {
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        handler.sendEmptyMessage(1);
                    }
                });*/
            }
        });

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 1) {
//                    // 新建群聊
//                    startActivityForResult(new Intent(context, NewGroupActivity.class), 0);
//                } else if (position == 2) {
//                    // 添加公开群
//                    startActivityForResult(new Intent(context, PublicGroupsActivity.class), 0);
//                } else {
                // 进入群聊
                Intent intent = new Intent(context, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                intent.putExtra("userId", groupAdapter.getItem(position - 1).getGroupId());
                startActivityForResult(intent, 0);
//                }
            }

        });

        titleBar= (EaseTitleBar) view.findViewById(R.id.title_bar);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.getInstance().creatRightPop(getActivity(),titleBar.getRightLayout(),getActivity());
            }

            });

//        groupListView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
//                    if (getCurrentFocus() != null)
//                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//                                InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//                return false;
//            }
//        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setUpView() {

    }


    private void refresh(){
        grouplist = EMGroupManager.getInstance().getAllGroups();
        groupAdapter = new GroupAdapter(context, 1, grouplist);
        groupListView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

    }
}
