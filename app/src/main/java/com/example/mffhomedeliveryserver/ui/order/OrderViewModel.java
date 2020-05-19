package com.example.mffhomedeliveryserver.ui.order;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedeliveryserver.Callback.OrderCallbackListener;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.Model.Orders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderViewModel extends ViewModel implements OrderCallbackListener {
    private MutableLiveData<List<Orders>> orderMutableLiveData;
    private  MutableLiveData<String> messageError;

    private OrderCallbackListener listener;

    public OrderViewModel() {
        orderMutableLiveData = new MutableLiveData<>();
        messageError = new MutableLiveData<>();
        listener = this;
    }

    public MutableLiveData<List<Orders>> getOrderMutableLiveData() {
        loadOrderByStatus(0);
        return orderMutableLiveData;
    }

    public void loadOrderByStatus(int orderStatus) {
        List<Orders> tempList = new ArrayList<>();
        Query orderRef = FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("orderStatus")
                .equalTo(orderStatus);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Orders orders = itemSnapshot.getValue(Orders.class);
                    orders.setKey(itemSnapshot.getKey());   //Important! If path is incorrect, can return null !!
                    tempList.add(orders);
                }
                listener.onOrderLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onOrderLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onOrderLoadSuccess(List<Orders> ordersList) {
        if (ordersList.size() > 0) {
            Collections.sort(ordersList, ((orders, t1) -> {
                if (orders.getCreateDate() < t1.getCreateDate()) return -1;
                return orders.getCreateDate() == t1.getCreateDate() ? 0 : 1;
            }));
        }
        orderMutableLiveData.setValue(ordersList);
    }

    @Override
    public void onOrderLoadFailed(String message) {
        messageError.setValue(message);
    }
}
