package com.example.mffhomedeliveryserver.ui.categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mffhomedeliveryserver.Adapter.CategoryAdapter;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.Common.SpacesItemDecoration;
import com.example.mffhomedeliveryserver.EventBus.MenuItemBack;
import com.example.mffhomedeliveryserver.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CategoriesFragment extends Fragment {

    private CategoriesViewModel menuViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView menuRV;
    AlertDialog alertDialog;
    LayoutAnimationController layoutAnimationController;
    CategoryAdapter categoryAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(CategoriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_catgeories, container, false);

        unbinder = ButterKnife.bind(this, root);
        initViews();
        menuViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        menuViewModel.getCategoryListMutable().observe(getViewLifecycleOwner(), categories -> {
            alertDialog.dismiss();
            categoryAdapter = new CategoryAdapter(getContext(), categories);
            menuRV.setAdapter(categoryAdapter);
            menuRV.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

    private void initViews() {
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        alertDialog.show();

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryAdapter != null) {
                    switch (categoryAdapter.getItemViewType(position)){
                        case Common.DEFAULT_COLUMN_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default: return -1;
                    }
                }
                return -1;
            }
        });
        menuRV.setLayoutManager(layoutManager);
        menuRV.addItemDecoration(new SpacesItemDecoration(8));
    }
}
