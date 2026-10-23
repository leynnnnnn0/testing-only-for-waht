package com.example.restaurantapp.helpers;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;

public final class AuthPasswordVisibility {
    private AuthPasswordVisibility() { }

    public static void install(EditText field) {
        field.setOnTouchListener((view, event) -> {
            if (event.getAction() != MotionEvent.ACTION_UP
                    || event.getX() < field.getWidth() - field.getCompoundPaddingRight()) {
                return false;
            }
            int cursor = field.getSelectionStart();
            boolean hidden = field.getTransformationMethod() instanceof PasswordTransformationMethod;
            field.setTransformationMethod(hidden
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            field.setSelection(Math.max(0, cursor));
            return true;
        });
    }
}
