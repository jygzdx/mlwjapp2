package com.mlxing.chatui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.mlxing.chatui.R;
import com.mlxing.chatui.daoyou.entity.Customer;
import com.mlxing.chatui.widget.CircleImageView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/16.
 */
public class CustomerAdapter extends BaseAdapter{
    private Context context;
    private HashMap<String,SoftReference<Bitmap>> map = new HashMap<String,SoftReference<Bitmap>>();
    private List<Customer> customers;
    private RequestQueue requestQueue;
    public CustomerAdapter(Context context, List<Customer> customers) {
        this.context = context;
        this.customers = customers;
        requestQueue = Volley.newRequestQueue(context);

    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Customer getItem(int position) {
        return customers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Adapter","position="+position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.customer_layout,null);
            viewHolder.civCustomer = (CircleImageView) convertView.findViewById(R.id.civ_customer);
            viewHolder.tvCustomerNickname = (TextView) convertView.findViewById(R.id.tv_customer_nickname);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Customer customer = getItem(position);
        String nickname = customer.getNickname();
        if(nickname!=null&&!"null".equals(nickname)){
            viewHolder.tvCustomerNickname.setText("客服"+customer.getId());
        }

        //加载图片
        loadImage(customer,viewHolder);

        return convertView;
    }

    private void loadImage(Customer customer, final ViewHolder viewHolder) {
        final String url = customer.getHeadimgurl();
        SoftReference<Bitmap> ref = map.get(url);
        if(ref!=null){
            Bitmap bitmap =ref.get();
            if(bitmap!=null){
                viewHolder.civCustomer.setImageBitmap(bitmap);
                return;
            }
        }


        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                SoftReference<Bitmap> softbitmap = new SoftReference<Bitmap>(bitmap);
                map.put(url,softbitmap);
                viewHolder.civCustomer.setImageBitmap(bitmap);
            }
        }, 60, 60, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                viewHolder.civCustomer.setImageResource(R.drawable.ease_default_avatar);
            }
        });
        requestQueue.add(request);
    }

    class ViewHolder{
        CircleImageView civCustomer;
        TextView tvCustomerNickname;
    }

}
