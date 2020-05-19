package com.example.mffhomedeliveryserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.EventBus.UpdateFoodItem;
import com.example.mffhomedeliveryserver.Model.Food;
import com.example.mffhomedeliveryserver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodListAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodList.get(position).getImage()).into(holder.foodIV);
        holder.foodPriceTV.setText(new StringBuilder("Total: \u20B9").append(foodList.get(position).getPrice()));
        holder.foodNameTV.setText(new StringBuilder("").append(foodList.get(position).getName()));
        holder.foodCategoryTV.setText(new StringBuilder("").append(Common.categorySelected.getName()));
        holder.vegetarianTV.setText(R.string.vegetarian);
        if (foodList.get(position).isAvailable())
            holder.availableSwitch.setChecked(true);

        holder.availableSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked)
                foodList.get(position).setAvailable(true);
            else
                foodList.get(position).setAvailable(false);

            Food foodItem = foodList.get(position);
            EventBus.getDefault().postSticky(new UpdateFoodItem(foodItem, isChecked, position));
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Unbinder unbinder;

        @BindView(R.id.img_best_deals_food_image)
        ImageView foodIV;
        @BindView(R.id.txt_best_deals_food_name)
        TextView foodNameTV;
        @BindView(R.id.txt_best_deals_food_price)
        TextView foodPriceTV;
        @BindView(R.id.switch_available)
        Switch availableSwitch;
        @BindView(R.id.txt_food_category)
        TextView foodCategoryTV;
        @BindView(R.id.vegetarian)
        TextView vegetarianTV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
