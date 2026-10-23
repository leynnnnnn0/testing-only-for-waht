package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.models.AdminTransaction;
import java.util.ArrayList;

public class AdminTransactionAdapter extends RecyclerView.Adapter<AdminTransactionAdapter.Holder> {
    public Context context;
    public ArrayList<AdminTransaction> list;

    public AdminTransactionAdapter(Context context, ArrayList<AdminTransaction> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_transaction_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        AdminTransaction item = list.get(position);
        holder.icon.setImageResource(item.icon);
        holder.title.setText(item.title);
        holder.date.setText(item.date);
        holder.status.setText(item.status);
        holder.reference.setText(item.reference);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title, date, status, reference;

        public Holder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.transaction_icon);
            title = view.findViewById(R.id.transaction_title);
            date = view.findViewById(R.id.transaction_date);
            status = view.findViewById(R.id.transaction_status);
            reference = view.findViewById(R.id.transaction_reference);
        }
    }
}
