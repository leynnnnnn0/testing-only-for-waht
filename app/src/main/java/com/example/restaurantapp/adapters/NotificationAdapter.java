package com.example.restaurantapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.NotificationItem;
import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    public Context context;
    public ArrayList<NotificationItem> list;
    public Runnable onChanged;

    public NotificationAdapter(Context context, ArrayList<NotificationItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.sample_notification_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.title.setText(item.title);
        holder.message.setText(item.message);
        holder.time.setText(item.time);
        holder.remove.setOnClickListener(v -> AppModal.danger(context,
                context.getString(R.string.notification_remove_title),
                context.getString(R.string.notification_remove_message), () -> {
                    int index = list.indexOf(item);
                    if (index < 0) return;
                    list.remove(index);
                    notifyItemRemoved(index);
                    if (onChanged != null) onChanged.run();
                    AppToast.success((Activity) context,
                            context.getString(R.string.notification_removed_title),
                            context.getString(R.string.notification_removed_message));
                }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView remove;
        TextView title, message, time;

        public NotificationViewHolder(@NonNull View view) {
            super(view);
            remove = view.findViewById(R.id.notification_remove);
            title = view.findViewById(R.id.notification_title);
            message = view.findViewById(R.id.notification_message);
            time = view.findViewById(R.id.notification_time);
        }
    }
}
