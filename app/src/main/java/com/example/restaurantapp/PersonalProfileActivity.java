package com.example.restaurantapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.restaurantapp.databinding.ActivityPersonalProfileBinding;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.StaticUserModel;
import java.util.Locale;

public class PersonalProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityPersonalProfileBinding binding = ActivityPersonalProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        SharedPreferences profile = getSharedPreferences("personal_profile", MODE_PRIVATE);
        String name = profile.getString("name", getString(R.string.personal_profile_name));
        String email = profile.getString("email", getString(R.string.personal_profile_email));
        binding.profileDisplayName.setText(name);
        binding.profileAvatar.setText(initials(name));
        binding.profileDisplayEmail.setText(email);
        binding.profileNameInput.setText(name);
        binding.profileEmailInput.setText(email);
        binding.profileNotificationsSwitch.setChecked(profile.getBoolean("notifications", true));
        binding.profileBack.setOnClickListener(v -> finish());
        binding.profileCancel.setOnClickListener(v -> finish());
        binding.profileSave.setOnClickListener(v -> {
            String updatedName = binding.profileNameInput.getText().toString().trim();
            String updatedEmail = binding.profileEmailInput.getText().toString().trim();
            boolean nameValid = !updatedName.isEmpty();
            boolean emailValid = Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches();
            binding.profileNameError.setVisibility(nameValid ? View.GONE : View.VISIBLE);
            binding.profileEmailError.setVisibility(emailValid ? View.GONE : View.VISIBLE);
            if (!nameValid || !emailValid) return;
            AppModal.confirm(this, getString(R.string.profile_save_title),
                    getString(R.string.profile_save_message), () -> {
                        boolean saved = profile.edit().putString("name", updatedName)
                                .putString("email", updatedEmail)
                                .putBoolean("notifications", binding.profileNotificationsSwitch.isChecked())
                                .commit();
                        if (!saved) {
                            AppToast.error(this, getString(R.string.profile_save_error_title),
                                    getString(R.string.profile_save_error_message));
                            return;
                        }
                        binding.profileDisplayName.setText(updatedName);
                        binding.profileAvatar.setText(initials(updatedName));
                        binding.profileDisplayEmail.setText(updatedEmail);
                        AppToast.success(this, getString(R.string.profile_saved_title),
                                getString(R.string.profile_saved_message));
                    });
        });
        binding.profileLogout.setOnClickListener(v -> {
            StaticUserModel.user = null;
            startActivity(new Intent(this, SampleLoginActivity.class));
            finishAffinity();
        });
    }

    private String initials(String name) {
        String[] words = name.trim().split("\\s+");
        String first = words[0].substring(0, 1);
        String last = words.length > 1 ? words[words.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase(Locale.ROOT);
    }
}
