package com.mlxing.chatui.daoyou.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mlxing.chatui.R;
import com.mlxing.chatui.adapter.CustomerAdapter;
import com.mlxing.chatui.daoyou.Constant;
import com.mlxing.chatui.daoyou.entity.Customer;
import com.mlxing.chatui.daoyou.utils.HttpUtil;
import com.mlxing.chatui.daoyou.utils.JsonUtil;
import com.mlxing.chatui.ui.SChatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import easeui.widget.EaseTitleBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CustomerActivity extends Activity {
    private static final String TAG = "CustomerActivity";
    private static final int LOAD_CUSTOMER_INFO = 1;
    private EaseTitleBar titleBar;
    private ListView lvCustomer;
    private List<Customer> customers = new ArrayList<Customer>();
    private CustomerAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_CUSTOMER_INFO:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        //初始化控件
        initView();
        //设置监听
        setListener();
        //配置adapter
        initAdapter();
        //获取数据
        getData();
    }

    private void initAdapter() {
        adapter = new CustomerAdapter(this, customers);
        lvCustomer.setAdapter(adapter);
    }

    private void getData() {
        HttpUtil.getString(Constant.CUSTOMER_URL).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "获取客服信息失败，请检查自己的网络是否有信号！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonInfo = response.body().string();
                customers.clear();
                List<Customer> allCustomer = JsonUtil.getCustomerList(jsonInfo);
                customers.addAll(allCustomer);
                Message msg = handler.obtainMessage();
                msg.what = LOAD_CUSTOMER_INFO;
                handler.sendMessage(msg);
            }
        });
    }

    private void initView() {
        titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        lvCustomer = (ListView) findViewById(R.id.lv_chat_customer);
        titleBar.setLeftImageResource(R.drawable.ease_back);
        titleBar.setRightLayoutVisibility(View.INVISIBLE);

    }

    private void setListener() {
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        //监听客服列表点击事件
        lvCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String acound = ((Customer) lvCustomer.getItemAtPosition(position)).getHuanxin_account();
                Intent intent = new Intent(CustomerActivity.this, SChatActivity.class);
                intent.putExtra("userId",acound);
                startActivity(intent);
            }
        });
    }


}
