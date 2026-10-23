package com.example.restaurantapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.restaurantapp.databinding.ActivitySampleActivityShowBinding;
import com.example.restaurantapp.models.StaticUserModel;

public class SampleActivityShowActivity extends AppCompatActivity {
    private ObjectAnimator iconPulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (StaticUserModel.user != null && "Admin".equalsIgnoreCase(StaticUserModel.user.role)) {
            setTheme(R.style.Theme_RestaurantApp_Admin);
        } else {
            setTheme(R.style.Theme_RestaurantApp_Personal);
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivitySampleActivityShowBinding binding = ActivitySampleActivityShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        binding.activityShowBack.setOnClickListener(v -> finish());
        binding.activityShowReference.setText(getString(R.string.activity_show_reference,
                getIntent().getIntExtra("number", 1)));
        binding.activityShowTitle.setText(getIntent().getStringExtra("title"));
        binding.activityShowOrganizer.setText(getIntent().getStringExtra("subtitle"));
        binding.activityShowDate.setText(getIntent().getStringExtra("date"));
        binding.activityShowTime.setText(getIntent().getStringExtra("time"));
        binding.activityShowMode.setText(getIntent().getStringExtra("mode"));
        iconPulse = ObjectAnimator.ofPropertyValuesHolder(binding.activityShowIcon,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.08f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.08f));
        iconPulse.setDuration(1100);
        iconPulse.setRepeatMode(ObjectAnimator.REVERSE);
        iconPulse.setRepeatCount(ObjectAnimator.INFINITE);
        iconPulse.start();
    }

    @Override
    protected void onDestroy() {
        iconPulse.cancel();
        super.onDestroy();
    }
}
