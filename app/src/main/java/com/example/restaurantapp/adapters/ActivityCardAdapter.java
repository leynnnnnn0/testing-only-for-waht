package com.example.restaurantapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.SampleActivityShowActivity;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.ActivityCard;
import java.util.ArrayList;

public class ActivityCardAdapter extends RecyclerView.Adapter<ActivityCardAdapter.ActivityViewHolder> {
    public Context context;
    public ArrayList<ActivityCard> list;

    public ActivityCardAdapter(Context context, ArrayList<ActivityCard> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_activity_card_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityCard model = list.get(position);
        holder.title.setText(model.title);
        holder.subtitle.setText(model.subtitle);
        holder.date.setText(model.date);
        holder.time.setText(model.time);
        holder.mode.setText(model.mode);
        holder.itemView.setOnClickListener(v -> show(model, position));
        holder.more.setOnClickListener(v -> show(model, position));
        holder.accept.setOnClickListener(v -> AppModal.confirm(context,
                context.getString(R.string.activity_accept_title), model.title,
                () -> AppToast.success((Activity) context,
                        context.getString(R.string.activity_accepted), model.title)));
        holder.decline.setOnClickListener(v -> AppModal.danger(context,
                context.getString(R.string.activity_decline_title), model.title,
                () -> AppToast.error((Activity) context,
                        context.getString(R.string.activity_declined), model.title)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void show(ActivityCard model, int position) {
        Intent intent = new Intent(context, SampleActivityShowActivity.class);
        intent.putExtra("number", position + 1);
        intent.putExtra("title", model.title);
        intent.putExtra("subtitle", model.subtitle);
        intent.putExtra("date", model.date);
        intent.putExtra("time", model.time);
        intent.putExtra("mode", model.mode);
        context.startActivity(intent);
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, date, time, mode;
        View accept, decline, more;

        public ActivityViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.activity_title);
            subtitle = view.findViewById(R.id.activity_subtitle);
            date = view.findViewById(R.id.activity_date);
            time = view.findViewById(R.id.activity_time);
            mode = view.findViewById(R.id.activity_mode);
            accept = view.findViewById(R.id.activity_primary_action);
            decline = view.findViewById(R.id.activity_secondary_action);
            more = view.findViewById(R.id.activity_more);
        }
    }
}
