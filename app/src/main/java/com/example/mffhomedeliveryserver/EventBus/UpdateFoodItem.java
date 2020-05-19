package com.example.mffhomedeliveryserver.EventBus;

import com.example.mffhomedeliveryserver.Model.Food;

public class UpdateFoodItem {
    public Food foodItem;
    public boolean isChecked;
    public int position;

    public UpdateFoodItem(Food foodItem, boolean isChecked, int position) {
        this.foodItem = foodItem;
        this.isChecked = isChecked;
        this.position = position;
    }

    public Food getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(Food foodItem) {
        this.foodItem = foodItem;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
