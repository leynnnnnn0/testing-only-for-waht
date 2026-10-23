package com.example.restaurantapp.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.restaurantapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class AppModal {
    public static void confirm(Context context, String title, String message, Runnable onConfirm) {
        show(context, title, message, onConfirm, false);
    }

    public static void danger(Context context, String title, String message, Runnable onConfirm) {
        show(context, title, message, onConfirm, true);
    }

    private static void show(Context context, String title, String message, Runnable onConfirm, boolean danger) {
        java.util.Objects.requireNonNull(onConfirm);
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_RestaurantApp_Modal);
        View view = LayoutInflater.from(dialog.getContext()).inflate(R.layout.modal_status, null);
        TextView icon = view.findViewById(R.id.modal_icon);
        icon.setText(danger ? "!" : "?");
        icon.setBackgroundResource(danger ? R.drawable.status_danger_circle : R.drawable.status_success_circle);
        view.setBackgroundResource(danger ? R.drawable.danger_card : R.drawable.modal_card);
        ((TextView) view.findViewById(R.id.modal_title)).setText(title);
        ((TextView) view.findViewById(R.id.modal_message)).setText(message);
        AlertDialog modal = dialog.setBackground(new ColorDrawable(Color.TRANSPARENT)).setView(view).create();
        TextView confirm = view.findViewById(R.id.modal_confirm);
        confirm.setText(R.string.confirm_arrow);
        confirm.setBackgroundResource(danger ? R.drawable.modal_danger : R.drawable.modal_confirm);
        confirm.setOnClickListener(v -> { modal.dismiss(); onConfirm.run(); });
        View cancel = view.findViewById(R.id.modal_cancel);
        cancel.setOnClickListener(v -> modal.dismiss());
        modal.show();
        icon.animate().scaleX(1).scaleY(1).setDuration(1200)
                .setInterpolator(new OvershootInterpolator()).start();
    }
}
