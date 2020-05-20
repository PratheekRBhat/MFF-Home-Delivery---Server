package com.example.mffhomedeliveryserver.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.EventBus.CallEvent;
import com.example.mffhomedeliveryserver.Model.Orders;
import com.example.mffhomedeliveryserver.R;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {
    private Context context;
    private List<Orders> ordersList;
    private SimpleDateFormat simpleDateFormat;

    public OrdersAdapter(Context context, List<Orders> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(ordersList.get(position).getCartItemList().get(0).getFoodImage())
                .into(holder.orderIV);
        Common.setSpanStringColor("Order date", simpleDateFormat.format(ordersList.get(position).getCreateDate()),
                holder.orderDateTV, Color.parseColor("#333639"));
        Common.setSpanStringColor("Order status", Common.convertStatusToString(ordersList.get(position).getOrderStatus()),
                holder.orderStatusTV, Color.parseColor("#00579A"));
        Common.setSpanStringColor("Name ", ordersList.get(position).getUserName(),
                holder.orderNameTV, Color.parseColor("#00574B"));
        Common.setSpanStringColor("Number of items  ", ordersList.get(position).getCartItemList() == null ? "0" :
                String.valueOf(ordersList.get(position).getCartItemList().size()),
                holder.numberOfItemsTV, Color.parseColor("#4B647D"));
        holder.callButton.setOnClickListener(view -> {
            Orders orders = ordersList.get(position);
            EventBus.getDefault().postSticky(new CallEvent(orders, position));
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public Orders getItemAtPosition(int pos) {
        return ordersList.get(pos);
    }

    public void removeItem(int pos) {
        ordersList.remove(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txt_order_date)
        TextView orderDateTV;
        @BindView(R.id.txt_order_status)
        TextView orderStatusTV;
        @BindView(R.id.txt_order_name)
        TextView orderNameTV;
        @BindView(R.id._txt_order_items_number)
        TextView numberOfItemsTV;
        @BindView(R.id.img_order_item)
        ImageView orderIV;
        @BindView(R.id.call_btn)
        Button callButton;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
