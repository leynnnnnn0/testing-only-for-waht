package com.example.restaurantapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.adapters.ActivityCardAdapter;
import com.example.restaurantapp.adapters.FeaturedItemAdapter;
import com.example.restaurantapp.adapters.ItemAdapter;
import com.example.restaurantapp.adapters.NotificationAdapter;
import com.example.restaurantapp.databinding.ActivityPersonalDashboardBinding;
import com.example.restaurantapp.databinding.BottomSheetPersonalSearchBinding;
import com.example.restaurantapp.helpers.SampleSeeder;
import com.example.restaurantapp.models.Item;
import com.example.restaurantapp.models.ActivityCard;
import com.example.restaurantapp.models.NotificationItem;
import com.example.restaurantapp.models.StaticUserModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;

public class PersonalDashboardActivity extends AppCompatActivity {
    private ActivityPersonalDashboardBinding binding;
    private ObjectAnimator dotPulse;
    private ViewFlipper[] seatFlippers;
    private Runnable[] seatStarts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPersonalDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        seatFlippers = new ViewFlipper[] {
                bindSeat(R.id.seat_three, R.drawable.personal_seats_3,
                        R.drawable.personal_seats_3_alt, R.string.personal_seat_three),
                bindSeat(R.id.seat_five, R.drawable.personal_seats_5,
                        R.drawable.personal_seats_5_alt, R.string.personal_seat_five),
                bindSeat(R.id.seat_six, R.drawable.personal_seats_6,
                        R.drawable.personal_seats_6_alt, R.string.personal_seat_six),
                bindSeat(R.id.seat_eight, R.drawable.personal_seats_8,
                        R.drawable.personal_seats_8_alt, R.string.personal_seat_eight)
        };
        seatStarts = new Runnable[seatFlippers.length];
        for (int i = 0; i < seatFlippers.length; i++) {
            ViewFlipper flipper = seatFlippers[i];
            seatStarts[i] = flipper::startFlipping;
        }
        binding.personalSearchTrigger.setOnClickListener(v -> openSearch(null));
        ArrayList<NotificationItem> notifications = sampleNotifications();
        updateNotificationBell(!notifications.isEmpty());
        binding.personalNotificationsButton.setOnClickListener(v -> {
            BottomSheetDialog sheet = new BottomSheetDialog(this);
            sheet.setContentView(R.layout.bottom_sheet_notifications);
            androidx.recyclerview.widget.RecyclerView list = sheet.findViewById(R.id.notifications_list);
            list.setLayoutManager(new LinearLayoutManager(this));
            NotificationAdapter adapter = new NotificationAdapter(this, notifications);
            adapter.onChanged = () -> {
                updateNotificationBell(!notifications.isEmpty());
                sheet.findViewById(R.id.notifications_empty).setVisibility(
                        notifications.isEmpty() ? View.VISIBLE : View.GONE);
            };
            list.setAdapter(adapter);
            adapter.onChanged.run();
            sheet.findViewById(R.id.notifications_close).setOnClickListener(close -> sheet.dismiss());
            sheet.show();
        });
        binding.personalAvatarButton.setOnClickListener(v -> showAccountMenu());
        setupSections();
    }

    private ViewFlipper bindSeat(int tileId, int firstImage, int secondImage, int label) {
        View tile = binding.getRoot().findViewById(tileId);
        ((ImageView) tile.findViewById(R.id.seat_image_first)).setImageResource(firstImage);
        ((ImageView) tile.findViewById(R.id.seat_image_second)).setImageResource(secondImage);
        ((TextView) tile.findViewById(R.id.seat_label)).setText(label);
        tile.setContentDescription(getString(label));
        tile.setOnClickListener(v -> openSearch(getString(label)));
        return tile.findViewById(R.id.seat_flipper);
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < seatFlippers.length; i++) {
            seatFlippers[i].postDelayed(seatStarts[i], 2000L * i);
        }
    }

    @Override
    protected void onStop() {
        for (int i = 0; i < seatFlippers.length; i++) {
            seatFlippers[i].removeCallbacks(seatStarts[i]);
            seatFlippers[i].stopFlipping();
        }
        super.onStop();
    }

    private void setupSections() {
        ArrayList<Item> featured = SampleSeeder.items();
        featured.sort(Comparator.comparingInt((Item item) -> item.id).reversed());
        binding.personalFeaturedItems.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.personalFeaturedItems.setAdapter(new FeaturedItemAdapter(this, featured));
        binding.personalFeaturedCount.setText(getString(R.string.admin_items_count, featured.size()));
        binding.personalFeaturedEmpty.getRoot().setVisibility(featured.isEmpty() ? View.VISIBLE : View.GONE);

        ArrayList<ActivityCard> activities = SampleSeeder.activities();
        ArrayList<ActivityCard> visible = new ArrayList<>();
        ActivityCardAdapter adapter = new ActivityCardAdapter(this, visible);
        binding.personalActivityList.setLayoutManager(new LinearLayoutManager(this));
        binding.personalActivityList.setAdapter(adapter);
        int[] labels = {R.string.personal_activity_filter_all,
                R.string.personal_activity_filter_online, R.string.personal_activity_filter_in_person};
        for (int i = 0; i < labels.length; i++) {
            Chip chip = new Chip(new ContextThemeWrapper(this, R.style.AdminStatusChipTheme));
            chip.setId(View.generateViewId());
            chip.setText(labels[i]);
            chip.setTag(i);
            binding.personalActivityFilters.addView(chip);
        }
        binding.personalActivityFilters.check(binding.personalActivityFilters.getChildAt(0).getId());
        Runnable refresh = () -> {
            Chip selected = binding.personalActivityFilters.findViewById(
                    binding.personalActivityFilters.getCheckedChipId());
            int filter = selected == null ? 0 : (int) selected.getTag();
            String query = binding.personalActivitiesSearch.getText().toString()
                    .trim().toLowerCase(Locale.ROOT);
            visible.clear();
            for (ActivityCard activity : activities) {
                boolean online = "Online".equalsIgnoreCase(activity.mode);
                boolean matchesFilter = filter == 0 || (filter == 1 ? online : !online);
                String searchable = (activity.title + " " + activity.subtitle + " " + activity.date
                        + " " + activity.time + " " + activity.mode).toLowerCase(Locale.ROOT);
                if (matchesFilter && searchable.contains(query)) visible.add(activity);
            }
            adapter.notifyDataSetChanged();
            binding.personalActivityCount.setText(getString(R.string.admin_items_count, visible.size()));
            binding.personalActivityList.setVisibility(visible.isEmpty() ? View.GONE : View.VISIBLE);
            binding.personalActivitiesEmpty.getRoot().setVisibility(visible.isEmpty() ? View.VISIBLE : View.GONE);
        };
        binding.personalActivityFilters.setOnCheckedStateChangeListener((group, ids) -> refresh.run());
        binding.personalActivitiesSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { refresh.run(); }
            public void afterTextChanged(Editable s) {}
        });
        refresh.run();
    }

    @Override
    protected void onDestroy() {
        if (dotPulse != null) dotPulse.cancel();
        super.onDestroy();
    }

    private void updateNotificationBell(boolean hasNotifications) {
        binding.personalBellIcon.setSelected(hasNotifications);
        binding.personalNotificationDot.setVisibility(hasNotifications ? View.VISIBLE : View.GONE);
        if (!hasNotifications && dotPulse != null) {
            dotPulse.cancel();
            dotPulse = null;
        } else if (hasNotifications && dotPulse == null) {
            dotPulse = ObjectAnimator.ofPropertyValuesHolder(binding.personalNotificationDot,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.35f, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.35f, 1f, 1.2f, 1f));
            dotPulse.setDuration(1600);
            dotPulse.setRepeatCount(ObjectAnimator.INFINITE);
            dotPulse.start();
        }
    }

    private ArrayList<NotificationItem> sampleNotifications() {
        ArrayList<NotificationItem> notifications = new ArrayList<>();
        String[] titles = {getString(R.string.personal_notification_reservation_title),
                getString(R.string.personal_notification_item_title),
                getString(R.string.personal_notification_appointment_title)};
        String[] messages = {getString(R.string.personal_notification_reservation_message),
                getString(R.string.personal_notification_item_message),
                getString(R.string.personal_notification_appointment_message)};
        String[] times = {getString(R.string.personal_notification_now),
                getString(R.string.personal_notification_hour),
                getString(R.string.personal_notification_yesterday)};
        for (int i = 0; i < titles.length; i++) {
            NotificationItem item = new NotificationItem();
            item.title = titles[i];
            item.message = messages[i];
            item.time = times[i];
            notifications.add(item);
        }
        return notifications;
    }

    private void showAccountMenu() {
        View content = LayoutInflater.from(this).inflate(R.layout.popup_admin_account, null);
        float density = getResources().getDisplayMetrics().density;
        PopupWindow popup = new PopupWindow(content, Math.round(188 * density),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setElevation(10 * density);
        popup.setOutsideTouchable(true);
        content.findViewById(R.id.account_profile).setOnClickListener(v -> {
            popup.dismiss();
            startActivity(new Intent(this, PersonalProfileActivity.class));
        });
        content.findViewById(R.id.account_logout).setOnClickListener(v -> {
            popup.dismiss();
            StaticUserModel.user = null;
            startActivity(new Intent(this, SampleLoginActivity.class));
            finishAffinity();
        });
        popup.showAsDropDown(binding.personalAvatarButton, 0, Math.round(8 * density), Gravity.END);
    }

    private void openSearch(String selectedCategory) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        BottomSheetPersonalSearchBinding search = BottomSheetPersonalSearchBinding.inflate(getLayoutInflater());
        sheet.setContentView(search.getRoot());
        ArrayList<Item> all = SampleSeeder.items();
        ArrayList<Item> visible = new ArrayList<>();
        ItemAdapter adapter = new ItemAdapter(this, visible);
        search.personalSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        search.personalSearchResults.setAdapter(adapter);

        addFilter(search, R.string.personal_search_all, "all");
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        int[] seats = {R.string.personal_seat_three, R.string.personal_seat_five,
                R.string.personal_seat_six, R.string.personal_seat_eight};
        for (int seat : seats) categories.add(getString(seat));
        for (Item item : all) if (item.category != null) categories.add(item.category);
        int selectedChipId = search.personalSearchFilters.getChildAt(0).getId();
        for (String category : categories) {
            Chip chip = addFilter(search, category, "category:" + category);
            if (category.equals(selectedCategory)) selectedChipId = chip.getId();
        }
        search.personalSearchFilters.check(selectedChipId);

        Runnable refresh = () -> {
            Chip selected = search.personalSearchFilters.findViewById(
                    search.personalSearchFilters.getCheckedChipId());
            String filter = selected == null ? "all" : (String) selected.getTag();
            String query = search.personalSearchInput.getText().toString().trim().toLowerCase(Locale.ROOT);
            visible.clear();
            for (Item item : all) {
                boolean matchesFilter = filter.equals("all")
                        || filter.startsWith("category:") && filter.substring(9).equals(item.category);
                String searchable = (item.name + " " + item.category + " " + item.detail + " "
                        + item.tag).toLowerCase(Locale.ROOT);
                if (matchesFilter && searchable.contains(query)) visible.add(item);
            }
            visible.sort(Comparator.comparingInt((Item item) -> item.id).reversed());
            adapter.notifyDataSetChanged();
            search.personalSearchEmpty.getRoot().setVisibility(visible.isEmpty() ? View.VISIBLE : View.GONE);
        };
        search.personalSearchFilters.setOnCheckedStateChangeListener((group, ids) -> refresh.run());
        search.personalSearchInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { refresh.run(); }
            public void afterTextChanged(Editable s) {}
        });
        search.personalSearchClose.setOnClickListener(v -> sheet.dismiss());
        sheet.setOnShowListener(dialog -> {
            View bottom = sheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams params = bottom.getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * .9f);
            bottom.setLayoutParams(params);
            sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        refresh.run();
        sheet.show();
    }

    private Chip addFilter(BottomSheetPersonalSearchBinding search, int label, String filter) {
        return addFilter(search, getString(label), filter);
    }

    private Chip addFilter(BottomSheetPersonalSearchBinding search, String label, String filter) {
        Chip chip = new Chip(new ContextThemeWrapper(this, R.style.AdminStatusChipTheme));
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setTag(filter);
        search.personalSearchFilters.addView(chip);
        return chip;
    }
}
