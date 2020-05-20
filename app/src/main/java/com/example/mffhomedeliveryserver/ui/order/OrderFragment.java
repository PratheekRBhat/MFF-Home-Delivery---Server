package com.example.mffhomedeliveryserver.ui.order;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mffhomedeliveryserver.Adapter.OrdersAdapter;
import com.example.mffhomedeliveryserver.Common.BottomSheetOrderFragment;
import com.example.mffhomedeliveryserver.EventBus.CallEvent;
import com.example.mffhomedeliveryserver.EventBus.LoadOrderEvent;
import com.example.mffhomedeliveryserver.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class OrderFragment extends Fragment {
    @BindView(R.id.recycler_orders_server)
    RecyclerView ordersRV;
    @BindView(R.id.txt_order_filter)
    TextView orderFilterTV;

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

                orderFilterTV.setText(new StringBuilder("Orders = ")
                .append(orders.size()));
            }
        });

        initViews();
        return root;
    }

    private void initViews() {
        setHasOptionsMenu(true);

        ordersRV.setHasFixedSize(true);
        ordersRV.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.order_filter_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                BottomSheetOrderFragment bottomSheetOrderFragment = BottomSheetOrderFragment.getInstance();
                bottomSheetOrderFragment.show(getActivity().getSupportFragmentManager(), "OrderFilter");
                break;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent.class))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().postSticky(new ChangeMenu);
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoadOrderEvent(LoadOrderEvent event) {
        orderViewModel.loadOrderByStatus(event.getStatus());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCallButtonCLicked(CallEvent event){
        if (event.getOrderItem() != null) {
            String number = event.getOrderItem().getUserPhone();
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+number));
            startActivity(callIntent);
        }
    }
}
