package com.example.restaurantapp.admin;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.R;
import com.example.restaurantapp.SampleLoginActivity;
import com.example.restaurantapp.SampleReportPageActivity;
import com.example.restaurantapp.adapters.ActivityCardAdapter;
import com.example.restaurantapp.adapters.ItemAdapter;
import com.example.restaurantapp.adapters.NotificationAdapter;
import com.example.restaurantapp.databinding.ActivityAdminDashboardBinding;
import com.example.restaurantapp.databinding.ViewAdminFeatureSlideBinding;
import com.example.restaurantapp.models.ActivityCard;
import com.example.restaurantapp.models.Item;
import com.example.restaurantapp.models.NotificationItem;
import com.example.restaurantapp.helpers.SampleSeeder;
import com.example.restaurantapp.models.StaticUserModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private ObjectAnimator dotPulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        ArrayList<NotificationItem> notifications = sampleNotifications();
        updateNotificationBell(!notifications.isEmpty());
        binding.adminNotificationsButton.setOnClickListener(v -> {
            BottomSheetDialog sheet = new BottomSheetDialog(this);
            sheet.setContentView(R.layout.bottom_sheet_notifications);
            RecyclerView list = sheet.findViewById(R.id.notifications_list);
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
        binding.adminAvatarButton.setOnClickListener(v -> showAccountMenu());

        int[] statuses = {R.string.admin_all, R.string.admin_pending, R.string.admin_active,
                R.string.admin_inactive, R.string.admin_rejected, R.string.admin_cancelled};
        for (int i = 0; i < statuses.length; i++) {
            Chip chip = new Chip(new ContextThemeWrapper(this, R.style.AdminStatusChipTheme));
            chip.setId(View.generateViewId());
            chip.setText(statuses[i]);
            binding.adminStatuses.addView(chip);
            if (i == 0) chip.setChecked(true);
        }

        ArrayList<Item> allItems = SampleSeeder.items();
        ArrayList<Item> items = new ArrayList<>(allItems.subList(0, Math.min(2, allItems.size())));
        binding.adminTransactionsList1.setLayoutManager(new GridLayoutManager(this, 2));
        binding.adminTransactionsList1.setAdapter(new ItemAdapter(this, items));
        binding.adminTransactionCount1.setText(getString(R.string.admin_items_count, items.size()));
        binding.adminItemsMetricValue.setText(String.valueOf(allItems.size()));
        binding.adminItemsMetric.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMenuListActivity.class)));
        binding.adminPersonsMetricValue.setText(String.valueOf(SampleSeeder.persons().size()));
        binding.adminPersonsMetric.setOnClickListener(v ->
                startActivity(new Intent(this, AdminPersonListActivity.class)));

        ArrayList<ActivityCard> activities = new ArrayList<>();
        String[] titles = {"You're invited to Web Development Class", "You're invited to Design Workshop", "You're invited to Project Presentation"};
        for (int i = 0; i < titles.length; i++) {
            ActivityCard activity = new ActivityCard();
            activity.title = titles[i];
            activity.subtitle = "by Alex Rivera";
            activity.date = "Feb " + (5 + i) + ", 2024";
            activity.time = "6:00 PM – 7:00 PM";
            activity.mode = i == 1 ? "Manila, PH" : "Online";
            activities.add(activity);
        }
        ArrayList<ActivityCard> visibleActivities = new ArrayList<>(activities);
        ActivityCardAdapter activityAdapter = new ActivityCardAdapter(this, visibleActivities);
        binding.adminTransactionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.adminTransactionsList.setAdapter(activityAdapter);
        int[] selectedActivityFilter = {0};
        Runnable filterActivities = () -> {
            String query = binding.adminActivitiesSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
            visibleActivities.clear();
            for (ActivityCard activity : activities) {
                boolean online = "Online".equalsIgnoreCase(activity.mode);
                boolean matchesMode = selectedActivityFilter[0] == 0
                        || (selectedActivityFilter[0] == 1 ? online : !online);
                String text = (activity.title + " " + activity.subtitle + " " + activity.date + " "
                        + activity.time + " " + activity.mode).toLowerCase(Locale.ROOT);
                if (matchesMode && text.contains(query)) visibleActivities.add(activity);
            }
            activityAdapter.notifyDataSetChanged();
            binding.adminTransactionCount.setText(getString(R.string.admin_items_count, visibleActivities.size()));
            binding.adminTransactionsList.setVisibility(visibleActivities.isEmpty() ? View.GONE : View.VISIBLE);
            binding.adminActivitiesEmpty.getRoot().setVisibility(visibleActivities.isEmpty() ? View.VISIBLE : View.GONE);
        };
        int[] activityFilters = {R.string.admin_all, R.string.admin_activities_online,
                R.string.admin_activities_in_person};
        for (int i = 0; i < activityFilters.length; i++) {
            int filter = i;
            Chip chip = new Chip(new ContextThemeWrapper(this, R.style.AdminStatusChipTheme));
            chip.setId(View.generateViewId());
            chip.setText(activityFilters[i]);
            binding.adminActivityFilters.addView(chip);
            chip.setOnClickListener(v -> {
                selectedActivityFilter[0] = filter;
                filterActivities.run();
            });
            if (i == 0) chip.setChecked(true);
        }
        binding.adminActivitiesSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterActivities.run();
            }
            public void afterTextChanged(Editable s) { }
        });
        filterActivities.run();

        String[] featureTitles = {"●  Update", "●  Items", "●  Activities"};
        String[] featureMeta = {"Feb 12th 2024", "Available now", "Latest activity"};
        String[] featureMessages = {"Sales revenue increased 40% in 1 week",
                allItems.size() + " items ready to browse",
                activities.size() + " activities to review"};
        String[] featureHighlights = {"40%", String.valueOf(allItems.size()),
                String.valueOf(activities.size())};
        int[] featureColors = {themeColor(R.attr.rolePrimaryDark),
                themeColor(R.attr.rolePrimary), themeColor(R.attr.roleAccent)};
        for (int i = 0; i < featureTitles.length; i++) {
            ViewAdminFeatureSlideBinding slide = ViewAdminFeatureSlideBinding.inflate(
                    getLayoutInflater(), binding.adminFeature, false);
            slide.featureLabel.setText(featureTitles[i]);
            slide.featureMeta.setText(featureMeta[i]);
            slide.featureMessage.setText(featureMessages[i]);
            slide.getRoot().setBackgroundTintList(ColorStateList.valueOf(featureColors[i]));
            highlight(slide.featureLabel, "●", i == 0
                    ? getColor(R.color.destructive) : themeColor(R.attr.roleAccent));
            highlight(slide.featureMessage, featureHighlights[i], themeColor(R.attr.roleAccent));
            binding.adminFeature.addView(slide.getRoot());
        }
        binding.adminFeature.setOnClickListener(v ->
                startActivity(new Intent(this, SampleReportPageActivity.class)));
    }

    @Override protected void onStart() {
        super.onStart();
        binding.adminFeature.startFlipping();
    }

    @Override protected void onStop() {
        binding.adminFeature.stopFlipping();
        super.onStop();
    }

    private void highlight(TextView view, String part, int color) {
        String text = view.getText().toString();
        int start = text.indexOf(part);
        if (start < 0) return;
        SpannableString styled = new SpannableString(text);
        styled.setSpan(new ForegroundColorSpan(color), start,
                start + part.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(styled);
    }

    private int themeColor(int attribute) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attribute, value, true);
        return value.data;
    }

    @Override
    protected void onDestroy() {
        if (dotPulse != null) dotPulse.cancel();
        super.onDestroy();
    }

    private void updateNotificationBell(boolean hasNotifications) {
        binding.adminBellIcon.setSelected(hasNotifications);
        binding.adminNotificationDot.setVisibility(hasNotifications ? View.VISIBLE : View.GONE);
        if (!hasNotifications && dotPulse != null) {
            dotPulse.cancel();
            dotPulse = null;
        } else if (hasNotifications && dotPulse == null) {
            dotPulse = ObjectAnimator.ofPropertyValuesHolder(binding.adminNotificationDot,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.35f, 1f, 1.2f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.35f, 1f, 1.2f, 1f));
            dotPulse.setDuration(1600);
            dotPulse.setRepeatCount(ObjectAnimator.INFINITE);
            dotPulse.start();
        }
    }

    private ArrayList<NotificationItem> sampleNotifications() {
        ArrayList<NotificationItem> notifications = new ArrayList<>();
        String[] titles = {"New request received", "Item updated", "Task completed", "New message", "Reminder"};
        String[] messages = {"A new request is ready to review.", "An item was recently updated.",
                "A task was completed successfully.", "You received a new message.",
                "You have an upcoming activity."};
        String[] times = {"Just now", "5 min ago", "18 min ago", "1 hour ago", "Yesterday"};
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
        int width = Math.round(188 * getResources().getDisplayMetrics().density);
        PopupWindow popup = new PopupWindow(content, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setElevation(10 * getResources().getDisplayMetrics().density);
        popup.setOutsideTouchable(true);
        content.findViewById(R.id.account_profile).setOnClickListener(v -> {
            popup.dismiss();
            startActivity(new Intent(this, AdminProfileActivity.class));
        });
        content.findViewById(R.id.account_logout).setOnClickListener(v -> {
            popup.dismiss();
            StaticUserModel.user = null;
            startActivity(new Intent(this, SampleLoginActivity.class));
            finishAffinity();
        });
        int gap = Math.round(8 * getResources().getDisplayMetrics().density);
        popup.showAsDropDown(binding.adminAvatarButton, 0, gap, Gravity.END);
    }
}
