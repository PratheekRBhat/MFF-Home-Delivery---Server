package com.example.mffhomedeliveryserver.Callback;

import com.example.mffhomedeliveryserver.Model.Category;
import com.example.mffhomedeliveryserver.Model.Orders;

import java.util.List;

public interface OrderCallbackListener {
    void onOrderLoadSuccess(List<Orders> ordersList);
    void onOrderLoadFailed(String message);
}
