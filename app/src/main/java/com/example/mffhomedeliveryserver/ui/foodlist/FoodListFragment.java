package com.example.mffhomedeliveryserver.ui.foodlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.mffhomedeliveryserver.Adapter.FoodListAdapter;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.EventBus.MenuItemBack;
import com.example.mffhomedeliveryserver.EventBus.UpdateFoodItem;
import com.example.mffhomedeliveryserver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FoodListFragment extends Fragment {

    private FoodListViewModel foodListViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView foodListRV;
    private FoodListAdapter foodListAdapter;

    private LayoutAnimationController layoutAnimationController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodListViewModel =
                ViewModelProviders.of(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        foodListViewModel.getMutableLiveDataFoodList().observe(getViewLifecycleOwner(), food -> {
            foodListAdapter = new FoodListAdapter(getContext(), food);
            foodListRV.setAdapter(foodListAdapter);
            foodListRV.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    private void initViews() {
        //Set action bar to show the title of the food category.
        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        foodListRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodListRV.setLayoutManager(layoutManager);
        foodListRV.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));  //To add dividers between list items.

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateFoodItem(UpdateFoodItem event) {
        if (event.getFoodItem() != null) {
            String foodItem = String.valueOf(event.getPosition());
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/"+Common.CATEGORY_REF+"/"+Common.categorySelected.getMenu_id()+"/foods/"+foodItem+"/available", event.isChecked());

            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful())
////                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
//
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
