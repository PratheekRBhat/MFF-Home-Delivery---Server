package com.example.mffhomedeliveryserver.ui.order;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mffhomedeliveryserver.Adapter.OrdersAdapter;
import com.example.mffhomedeliveryserver.Common.BottomSheetOrderFragment;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.Common.SwipeHelper;
import com.example.mffhomedeliveryserver.EventBus.CallEvent;
import com.example.mffhomedeliveryserver.EventBus.LoadOrderEvent;
import com.example.mffhomedeliveryserver.Model.Orders;
import com.example.mffhomedeliveryserver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static java.lang.Math.round;

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

                updateTextCounter();
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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        SwipeHelper mySwipeHelper  = new SwipeHelper(getContext(), ordersRV,  width / 6) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Remove", 30, 0, Color.parseColor("#12005e"), pos->{
                   AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                           .setTitle("Delete")
                           .setMessage("Are you sure you want to delete the item?")
                           .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.dismiss()))
                           .setPositiveButton("Confirm", (dialogInterface, i) -> {
                               Orders orders = ordersAdapter.getItemAtPosition(pos);
                               FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                                       .child(orders.getKey())
                                       .removeValue()
                                       .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                                       .addOnSuccessListener(aVoid -> {
                                           ordersAdapter.removeItem(pos);
                                           ordersAdapter.notifyItemRemoved(pos);
                                           updateTextCounter();
                                           dialogInterface.dismiss();
                                           Toast.makeText(getContext(), "Order removed successfully", Toast.LENGTH_SHORT).show();
                                       });
                           });

                   AlertDialog dialog = builder.create();
                   dialog.show();
                }));

                buf.add(new MyButton(getContext(), "Edit", 30, 0, Color.parseColor("#336699"), pos->{
                    showEditDialog(ordersAdapter.getItemAtPosition(pos), pos);
                }));

            }
        };
    }

    private void showEditDialog(Orders orders, int pos) {
        View layout_dialog;
       AlertDialog.Builder builder;

        if(orders.getOrderStatus() == 0) //shipping
        {
           layout_dialog = LayoutInflater.from(getContext())
                   .inflate(R.layout.layout_order_shipping, null);
            builder = new AlertDialog.Builder(getContext());
            builder.setView(layout_dialog);
        }
        else if(orders.getOrderStatus() == -1) //cancelled
        {
            layout_dialog = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_order_cancelled, null);
            builder = new AlertDialog.Builder(getContext());
            builder.setView(layout_dialog);
        }
        else    //shipped
        {
            layout_dialog = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_order_shipped, null);
            builder = new AlertDialog.Builder(getContext());
            builder.setView(layout_dialog);
        }

        Button okBtn = layout_dialog.findViewById(R.id.btn_ok_d);
        Button cancelBtn = layout_dialog.findViewById(R.id.btn_cancel_d);

        RadioButton shippingRB = layout_dialog.findViewById(R.id.rdi_shipping_d);
        RadioButton shippedRB = layout_dialog.findViewById(R.id.rdi_shipped_d);
        RadioButton cancelledRB = layout_dialog.findViewById(R.id.rdi_cancelled_d);
        RadioButton deleteRB = layout_dialog.findViewById(R.id.rdi_delete);
        RadioButton restorePlacedRB = layout_dialog.findViewById(R.id.rdi_restore);

        TextView txt_status = layout_dialog.findViewById(R.id.txt_status_d);

        //set data.
        txt_status.setText(new StringBuilder("Order Status(").append(Common.convertStatusToString(orders.getOrderStatus())));

        //Create Dialog
        AlertDialog dialog = builder.create();
        dialog.show();


        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if(cancelledRB != null && cancelledRB.isChecked())
                updateOrder(pos, orders, -1);
            else if(shippingRB != null && shippingRB.isChecked())
                updateOrder(pos, orders, 1);
            else if(shippedRB != null && shippedRB.isChecked())
                updateOrder(pos, orders, 2);
            else if(restorePlacedRB != null && restorePlacedRB.isChecked())
                updateOrder(pos, orders, 0);
            else if(deleteRB != null && deleteRB.isChecked())
                deleteOrder(pos, orders);
        });
    }

    private void deleteOrder(int pos, Orders orders) {
        if (TextUtils.isEmpty(orders.getKey())){
            FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                    .child(orders.getKey())
                    .removeValue()
                    .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> {
                        ordersAdapter.removeItem(pos);
                        ordersAdapter.notifyItemRemoved(pos);
                        updateTextCounter();
                        Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Order number must not be null or empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrder(int pos, Orders orders, int status) {
        if (orders.getOrderNumber() != null){
            Map<String, Object> updateData= new HashMap<>();
            updateData.put("orderStatus", status);

            FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                    .child(orders.getOrderNumber())
                    .updateChildren(updateData)
                    .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(aVoid -> {
                        ordersAdapter.removeItem(pos);
                        ordersAdapter.notifyItemRemoved(pos);
                        updateTextCounter();
                        Toast.makeText(getContext(), "Order updated successfully", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Order number must not be null or empty", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), ""+orders.getOrderNumber(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTextCounter() {
        orderFilterTV.setText(new StringBuilder("Orders = ")
                .append(ordersAdapter.getItemCount()));
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.order_filter_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            BottomSheetOrderFragment bottomSheetOrderFragment = BottomSheetOrderFragment.getInstance();
            bottomSheetOrderFragment.show(getActivity().getSupportFragmentManager(), "OrderFilter");
            return true;
        } else
            return  super.onOptionsItemSelected(item);
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
