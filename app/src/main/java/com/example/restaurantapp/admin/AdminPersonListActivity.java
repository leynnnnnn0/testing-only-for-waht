package com.example.restaurantapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.UserAdapter;
import com.example.restaurantapp.databinding.ActivityAdminPersonListBinding;
import com.example.restaurantapp.helpers.SampleSeeder;
import com.example.restaurantapp.models.User;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class AdminPersonListActivity extends AppCompatActivity {
    ActivityAdminPersonListBinding binding;
    ArrayList<User> people = SampleSeeder.persons(), filtered = new ArrayList<>();
    UserAdapter adapter;
    int filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminPersonListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left + dp(16), bars.top, bars.right + dp(16), bars.bottom);
            return insets;
        });
        binding.personBack.setOnClickListener(v -> finish());
        binding.personAdd.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.menu_preview).setMessage(R.string.person_add_preview)
                .setPositiveButton(android.R.string.ok, null).show());
        int[] labels = {R.string.person_all, R.string.person_active, R.string.person_inactive};
        for (int i = 0; i < labels.length; i++) {
            int selected = i;
            Chip chip = new Chip(new ContextThemeWrapper(this, R.style.AdminStatusChipTheme));
            chip.setId(View.generateViewId());
            chip.setText(labels[i]);
            chip.setOnClickListener(v -> { filter = selected; refresh(); });
            binding.personFilters.addView(chip);
            if (i == 0) chip.setChecked(true);
        }
        binding.personList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(this, filtered);
        binding.personList.setAdapter(adapter);
        binding.personSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { refresh(); }
            public void afterTextChanged(Editable s) {}
        });
        refresh();
    }

    void refresh() {
        String query = binding.personSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        filtered.clear();
        for (User user : people) {
            if ((filter == 0 || user.isActive == (filter == 1 ? 1 : 0))
                    && (user.fullName.toLowerCase(Locale.ROOT).contains(query)
                    || user.role.toLowerCase(Locale.ROOT).contains(query))) filtered.add(user);
        }
        filtered.sort(Comparator.comparingInt((User user) -> user.id).reversed());
        adapter.notifyDataSetChanged();
        binding.personEmpty.getRoot().setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
