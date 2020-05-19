package com.example.mffhomedeliveryserver.ui.order;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.mffhomedeliveryserver.Adapter.OrdersAdapter;
import com.example.mffhomedeliveryserver.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderFragment extends Fragment {
    @BindView(R.id.recycler_orders_server)
    RecyclerView ordersRV;

    Unbinder unbinder;
    private LayoutAnimationController layoutAnimationController;
    private OrdersAdapter ordersAdapter;

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order, container, false);

        unbinder = ButterKnife.bind(this, root);

        orderViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        });
        orderViewModel.getOrderMutableLiveData().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                ordersAdapter = new OrdersAdapter(getContext(), orders);
                ordersRV.setAdapter(ordersAdapter);

                ordersRV.setLayoutAnimation(layoutAnimationController);
            }
        });

        initViews();
        return root;
    }

    private void initViews() {
        ordersRV.setHasFixedSize(true);
        ordersRV.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }
}
