package com.example.mffhomedeliveryserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedeliveryserver.Model.CartItem;
import com.example.mffhomedeliveryserver.Model.Orders;
import com.example.mffhomedeliveryserver.R;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {
    Context context;
    List<CartItem> cartItems;
    Gson gson;

    public OrderDetailAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        gson = new Gson();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItems.get(position).getFoodImage())
                .into(holder.orderDetailIV);
        holder.orderQuantityTV.setText(new StringBuilder("Quantity: ").append(cartItems.get(position).getFoodQuantity()));
        holder.orderNameTV.setText(new StringBuilder("").append(cartItems.get(position).getFoodName()));
        holder.orderPriceTV.setText(new StringBuilder("\u20B9").append(cartItems.get(position).getFoodPrice()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_detail_image)
        ImageView orderDetailIV;
        @BindView(R.id.order_detail_item_name)
        TextView orderNameTV;
        @BindView(R.id.order_detail_quantity)
        TextView orderQuantityTV;
        @BindView(R.id.order_detail_price)
        TextView orderPriceTV;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
