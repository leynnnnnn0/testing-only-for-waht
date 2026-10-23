package com.example.restaurantapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.SampleCreatePageActivity;
import com.example.restaurantapp.adapters.ItemAdapter;
import com.example.restaurantapp.helpers.SampleSeeder;
import com.example.restaurantapp.models.Item;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class AdminMenuListActivity extends AppCompatActivity {
    ArrayList<Item> items = new ArrayList<>(), filtered = new ArrayList<>();
    ItemAdapter adapter;
    EditText search;
    ChipGroup categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_menu_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding = Math.round(16 * getResources().getDisplayMetrics().density);
            view.setPadding(bars.left + padding, bars.top, bars.right + padding, bars.bottom);
            return insets;
        });
        items.addAll(SampleSeeder.items());
        search = findViewById(R.id.menu_search);
        categories = findViewById(R.id.menu_categories);
        RecyclerView recycler = findViewById(R.id.menu_list);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, filtered);
        recycler.setAdapter(adapter);
        categories.setOnCheckedStateChangeListener((group, ids) -> refreshItems());
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { refreshItems(); }
            public void afterTextChanged(Editable s) {}
        });
        findViewById(R.id.menu_back).setOnClickListener(v -> finish());
        findViewById(R.id.menu_add).setOnClickListener(v ->
                startActivity(new Intent(this, SampleCreatePageActivity.class)));
        refreshItems();
    }

    void refreshItems() {
        int category = categories.getCheckedChipId();
        String query = search.getText().toString().trim().toLowerCase(Locale.ROOT);
        filtered.clear();
        for (Item item : items) {
            boolean matches = category == R.id.filter_all || category == R.id.filter_food
                    || category == R.id.filter_breakfast && item.id == 1
                    || category == R.id.filter_lunch && item.id == 2
                    || category == R.id.filter_dinner && item.id == 3;
            if (matches && item.name.toLowerCase(Locale.ROOT).contains(query)) filtered.add(item);
        }
        filtered.sort(Comparator.comparingInt((Item item) -> item.id).reversed());
        adapter.notifyDataSetChanged();
        findViewById(R.id.menu_empty).setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
