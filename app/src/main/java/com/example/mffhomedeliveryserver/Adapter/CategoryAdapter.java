package com.example.mffhomedeliveryserver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mffhomedeliveryserver.Callback.RecyclerClickListener;
import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.EventBus.CategoryClick;
import com.example.mffhomedeliveryserver.Model.Category;
import com.example.mffhomedeliveryserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_categories_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(categoryList.get(position).getImage()).into(holder.categoryIV);
        holder.categoryTV.setText(new StringBuilder(categoryList.get(position).getName()));

        //Send notification to the Home activity.
        holder.setRecyclerClickListener((view, position1) -> {
            Common.categorySelected = categoryList.get(position1);
            EventBus.getDefault().postSticky(new CategoryClick(true, categoryList.get(position1)));
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.img_category)
        ImageView categoryIV;
        @BindView(R.id.txt_category)
        TextView categoryTV;

        RecyclerClickListener recyclerClickListener;

        public void setRecyclerClickListener(RecyclerClickListener recyclerClickListener) {
            this.recyclerClickListener = recyclerClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerClickListener.onItemClickListener(view, getAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryList.size() == 1)
            return Common.DEFAULT_COLUMN_COUNT;
        else {
            if (categoryList.size() %2 == 0)
                return Common.DEFAULT_COLUMN_COUNT;
            else
                return (position > 1 && position == categoryList.size()-1) ? Common.FULL_WIDTH_COLUMN : Common.DEFAULT_COLUMN_COUNT;
        }
    }
}
