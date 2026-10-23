package com.example.restaurantapp.adapters;

import android.content.Context;
import android.app.Activity;
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
import com.example.restaurantapp.models.User;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    public Context context;
    public ArrayList<User> list;

    public UserAdapter(Context context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sample_person_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User model = list.get(position);
        holder.name.setText(model.fullName);
        holder.role.setText(model.role);
        String name = model.fullName == null ? "" : model.fullName.trim();
        String[] names = name.isEmpty() ? new String[0] : name.split("\\s+");
        holder.initials.setText(names.length == 0 ? "?" : names[0].substring(0, 1)
                + (names.length > 1 ? names[names.length - 1].substring(0, 1) : ""));
        holder.credits.setText(context.getString(R.string.user_credits, model.creditPoints));
        holder.age.setText(context.getString(R.string.user_age, model.age));
        holder.gender.setText(model.gender);
        holder.location.setText(model.location);
        holder.email.setText(model.email);
        holder.joined.setText(context.getString(R.string.user_joined, model.joinedDate));
        holder.specialty.setText(model.specialty);
        boolean active = model.isActive == 1;
        holder.status.setText(active ? R.string.user_active : R.string.user_inactive);
        holder.status.setTextColor(context.getColor(active ? R.color.badge_green : R.color.badge_red));
        holder.status.setBackgroundResource(active ? R.drawable.green_badge : R.drawable.red_badge);
        int image = model.image == null ? 0 : context.getResources()
                .getIdentifier(model.image, "drawable", context.getPackageName());
        holder.image.setVisibility(image == 0 ? View.GONE : View.VISIBLE);
        holder.image.setImageResource(image);
        holder.edit.setOnClickListener(v -> {
            if (context instanceof Activity) AppToast.success((Activity) context,
                    context.getString(R.string.user_edit_preview, model.fullName), context.getString(R.string.user_edit_note));
        });
        holder.delete.setOnClickListener(v -> AppModal.danger(context,
                context.getString(R.string.user_delete_title),
                context.getString(R.string.user_delete_message, model.fullName), () -> {
                    int index = list.indexOf(model);
                    if (index >= 0) { list.remove(index); notifyItemRemoved(index); }
                }));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, role, credits, age, gender, location, email, joined, specialty, status, initials;
        View edit, delete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_image);
            name = itemView.findViewById(R.id.user_name);
            role = itemView.findViewById(R.id.user_role);
            initials = itemView.findViewById(R.id.user_initials);
            credits = itemView.findViewById(R.id.user_credits);
            age = itemView.findViewById(R.id.user_age);
            gender = itemView.findViewById(R.id.user_gender);
            location = itemView.findViewById(R.id.user_location);
            email = itemView.findViewById(R.id.user_email);
            joined = itemView.findViewById(R.id.user_joined);
            specialty = itemView.findViewById(R.id.user_specialty);
            status = itemView.findViewById(R.id.user_status);
            edit = itemView.findViewById(R.id.user_edit);
            delete = itemView.findViewById(R.id.user_delete);
        }
    }
}
