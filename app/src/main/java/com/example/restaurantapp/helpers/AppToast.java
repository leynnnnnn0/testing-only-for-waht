package com.example.restaurantapp.helpers;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.restaurantapp.R;

public final class AppToast {
    public static void success(Activity activity, String title, String message) {
        show(activity, title, message, false);
    }

    public static void error(Activity activity, String title, String message) {
        show(activity, title, message, true);
    }

    private static void show(Activity activity, String title, String message, boolean error) {
        FrameLayout root = activity.findViewById(android.R.id.content);
        View previous = root.findViewWithTag(AppToast.class);
        if (previous != null) { previous.animate().cancel(); root.removeView(previous); }
        View banner = activity.getLayoutInflater().inflate(R.layout.toast_banner, root, false);
        banner.setTag(AppToast.class);
        banner.setBackgroundResource(error ? R.drawable.danger_card : R.drawable.modal_card);
        TextView icon = banner.findViewById(R.id.toast_icon);
        icon.setText(error ? "!" : "✓");
        icon.setBackgroundResource(error ? R.drawable.status_danger_circle : R.drawable.status_success_circle);
        ((TextView) banner.findViewById(R.id.toast_title)).setText(title);
        ((TextView) banner.findViewById(R.id.toast_message)).setText(message);
        int gap = Math.round(12 * activity.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, -2, Gravity.TOP);
        params.setMargins(gap, gap, gap, 0);
        ViewCompat.setOnApplyWindowInsetsListener(banner, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            int[] position = new int[2];
            root.getLocationOnScreen(position);
            params.setMargins(gap + bars.left, gap + Math.max(0, bars.top - position[1]), gap + bars.right, 0);
            banner.setLayoutParams(params);
            return insets;
        });
        root.addView(banner, params);
        ViewCompat.requestApplyInsets(banner);
        banner.setTranslationY(-gap * 4);
        banner.setAlpha(0);
        banner.animate().translationY(0).alpha(1).setDuration(300).start();
        banner.setOnClickListener(v -> root.removeView(banner));
        banner.postDelayed(() -> banner.animate().alpha(0).translationY(-gap * 2).setDuration(200)
                .withEndAction(() -> root.removeView(banner)).start(), 4000);
    }
}
