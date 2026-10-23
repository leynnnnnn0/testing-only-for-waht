package com.example.restaurantapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.admin.AdminItemShowActivity;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.Item;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    public Context context;
    public ArrayList<Item> list;

    public ItemAdapter(Context context, ArrayList<Item> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_item_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item model = list.get(position);
        holder.name.setText(model.name);
        holder.category.setText(model.category);
        holder.detail.setText(model.detail);
        holder.tag.setText(model.tag);
        holder.location.setText(model.location);
        holder.rating.setText(context.getString(R.string.item_rating, model.rating, model.reviewCount));
        NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        money.setMinimumFractionDigits(0);
        money.setMaximumFractionDigits(2);
        holder.price.setText(money.format(model.price));
        boolean available = model.isAvailable == 1;
        holder.status.setText(available ? R.string.item_available : R.string.item_unavailable);
        holder.status.setBackgroundResource(available ? R.drawable.green_badge : R.drawable.red_badge);
        holder.status.setTextColor(context.getColor(available ? R.color.badge_green : R.color.badge_red));
        int image = model.image == null ? 0 : context.getResources().getIdentifier(model.image, "drawable", context.getPackageName());
        holder.image.setImageResource(image == 0 ? R.drawable.sample_item_photo : image);
        holder.favorite.setImageResource(model.isFavorite == 1 ? R.drawable.ic_item_heart_filled : R.drawable.ic_item_heart);
        holder.favorite.setImageTintList(null);
        holder.favorite.setOnClickListener(v -> AppModal.confirm(context,
                context.getString(model.isFavorite == 1 ? R.string.item_remove_favorite : R.string.item_add_favorite),
                context.getString(R.string.item_favorite_message, model.name), () -> {
                    model.isFavorite = model.isFavorite == 1 ? 0 : 1;
                    int index = list.indexOf(model);
                    if (index >= 0) notifyItemChanged(index);
                    if (context instanceof Activity) AppToast.success((Activity) context,
                            context.getString(R.string.item_favorite_updated), context.getString(R.string.item_favorite_preview));
                }));
        holder.details.setOnClickListener(v -> context.startActivity(
                new Intent(context, AdminItemShowActivity.class).putExtra("itemId", model.id)));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageButton favorite;
        TextView name, category, detail, tag, rating, location, price, status;
        View details;

        public ItemViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.item_image);
            favorite = view.findViewById(R.id.item_favorite);
            name = view.findViewById(R.id.item_name);
            category = view.findViewById(R.id.item_category);
            detail = view.findViewById(R.id.item_detail);
            tag = view.findViewById(R.id.item_tag);
            rating = view.findViewById(R.id.item_rating);
            location = view.findViewById(R.id.item_location);
            price = view.findViewById(R.id.item_price);
            status = view.findViewById(R.id.item_status);
            details = view.findViewById(R.id.item_view_details);
        }
    }
}
