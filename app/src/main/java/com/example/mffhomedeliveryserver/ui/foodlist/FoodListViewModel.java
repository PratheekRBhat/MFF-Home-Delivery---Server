package com.example.mffhomedeliveryserver.ui.foodlist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.Model.Food;

import java.util.List;

public class FoodListViewModel extends ViewModel {
    private MutableLiveData<List<Food>> mutableLiveDataFoodList;

    public FoodListViewModel() {
    }

    public MutableLiveData<List<Food>> getMutableLiveDataFoodList() {
        if (mutableLiveDataFoodList == null){
            mutableLiveDataFoodList = new MutableLiveData<>();
        }
        mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());
        return mutableLiveDataFoodList;
    }
}
