package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.databinding.ItemClaimTestBinding;
import com.example.restaurantapp.models.ClaimTestItem;
import java.util.ArrayList;

public class ClaimTestAdapter extends RecyclerView.Adapter<ClaimTestAdapter.Holder> {
    private final Context context;
    private final ArrayList<ClaimTestItem> list;

    public ClaimTestAdapter(Context context, ArrayList<ClaimTestItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemClaimTestBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ClaimTestItem item = list.get(position);
        holder.binding.claimImage.setImageResource(item.image);
        holder.binding.claimTitle.setText(item.title);
        holder.binding.claimLocation.setText(item.location);
        holder.binding.claimDetail.setText(item.detail);
        int background = R.drawable.claim_status_pending;
        int color = R.color.claim_pending_text;
        String status = "◷  Pending";
        if ("Approved".equals(item.status)) {
            background = R.drawable.claim_status_approved;
            color = R.color.claim_approved_text;
            status = "✓  Approved";
        } else if ("Rejected".equals(item.status)) {
            background = R.drawable.claim_status_rejected;
            color = R.color.claim_rejected_text;
            status = "×  Rejected";
        } else if ("Returned".equals(item.status)) {
            background = R.drawable.claim_status_returned;
            color = R.color.claim_returned_text;
            status = "↶  Returned";
        }
        holder.binding.claimStatus.setBackgroundResource(background);
        holder.binding.claimStatus.setTextColor(ContextCompat.getColor(context, color));
        holder.binding.claimStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemClaimTestBinding binding;

        public Holder(ItemClaimTestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
