package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.databinding.ItemFindChatMessageBinding;
import com.example.restaurantapp.models.FindChatMessage;
import java.util.ArrayList;

public class FindChatAdapter extends RecyclerView.Adapter<FindChatAdapter.Holder> {
    private final Context context;
    private final ArrayList<FindChatMessage> list;

    public FindChatAdapter(Context context, ArrayList<FindChatMessage> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemFindChatMessageBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        FindChatMessage message = list.get(position);
        holder.binding.chatPetRow.setVisibility(message.type == FindChatMessage.PET ? View.VISIBLE : View.GONE);
        holder.binding.chatUserText.setVisibility(message.type == FindChatMessage.USER ? View.VISIBLE : View.GONE);
        holder.binding.chatMatchesRow.setVisibility(message.type == FindChatMessage.MATCHES ? View.VISIBLE : View.GONE);
        holder.binding.chatPetText.setText(message.text);
        holder.binding.chatUserText.setText(message.text);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ItemFindChatMessageBinding binding;

        public Holder(ItemFindChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
