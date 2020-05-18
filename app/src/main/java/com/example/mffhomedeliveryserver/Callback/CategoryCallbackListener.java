package com.example.mffhomedeliveryserver.Callback;

import com.example.mffhomedeliveryserver.Model.Category;

import java.util.List;

public interface CategoryCallbackListener {
    void onCategoryLoadSuccess(List<Category> categoryList);
    void onCategoryLoadFailed(String message);
}
