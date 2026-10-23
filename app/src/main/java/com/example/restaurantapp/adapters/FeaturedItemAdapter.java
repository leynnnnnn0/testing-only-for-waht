package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.restaurantapp.R;
import com.example.restaurantapp.models.Item;
import java.util.ArrayList;

public class FeaturedItemAdapter extends ItemAdapter {
    public FeaturedItemAdapter(Context context, ArrayList<Item> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sample_item_card_list, parent, false);
        int width = Math.round(240 * parent.getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams params = card.getLayoutParams();
        params.width = width;
        card.setLayoutParams(params);
        return new ItemViewHolder(card);
    }
}
