package com.example.restaurantapp;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restaurantapp.adapters.NotificationAdapter;
import com.example.restaurantapp.models.NotificationItem;
import java.util.ArrayList;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DashboardSampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_sample);
        ArrayList<NotificationItem> notifications = sampleNotifications();
        boolean hasNotifications = !notifications.isEmpty();
        findViewById(R.id.dashboard_bell_icon).setSelected(hasNotifications);
        findViewById(R.id.dashboard_notification_dot).setVisibility(
                hasNotifications ? View.VISIBLE : View.GONE);
        findViewById(R.id.dashboard_notifications_button).setOnClickListener(v -> {
            BottomSheetDialog sheet = new BottomSheetDialog(this);
            sheet.setContentView(R.layout.bottom_sheet_notifications);
            RecyclerView list = sheet.findViewById(R.id.notifications_list);
            list.setLayoutManager(new LinearLayoutManager(this));
            NotificationAdapter adapter = new NotificationAdapter(this, notifications);
            adapter.onChanged = () -> {
                boolean available = !notifications.isEmpty();
                findViewById(R.id.dashboard_bell_icon).setSelected(available);
                findViewById(R.id.dashboard_notification_dot).setVisibility(
                        available ? View.VISIBLE : View.GONE);
                sheet.findViewById(R.id.notifications_empty).setVisibility(
                        available ? View.GONE : View.VISIBLE);
            };
            list.setAdapter(adapter);
            adapter.onChanged.run();
            sheet.findViewById(R.id.notifications_close).setOnClickListener(close -> sheet.dismiss());
            sheet.show();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding = Math.round(20 * getResources().getDisplayMetrics().density);
            view.setPadding(bars.left + padding, bars.top, bars.right + padding, bars.bottom);
            return insets;
        });
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
}
