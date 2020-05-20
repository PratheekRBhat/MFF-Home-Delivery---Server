package com.example.mffhomedeliveryserver.EventBus;

import com.example.mffhomedeliveryserver.Model.Orders;

public class CallEvent {
    private Orders orderItem;
    private int position;

    public CallEvent(Orders orderItem, int position) {
        this.orderItem = orderItem;
        this.position = position;
    }

    public Orders getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(Orders orderItem) {
        this.orderItem = orderItem;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
