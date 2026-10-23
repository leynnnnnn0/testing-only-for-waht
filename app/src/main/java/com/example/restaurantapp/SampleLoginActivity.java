package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.admin.AdminDashboardActivity;
import com.example.restaurantapp.databinding.ActivitySampleLoginBinding;
import com.example.restaurantapp.helpers.AuthPasswordVisibility;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.helpers.DBHelper;
import com.example.restaurantapp.models.StaticUserModel;
import com.example.restaurantapp.models.User;

public class SampleLoginActivity extends AppCompatActivity {
    private ActivitySampleLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySampleLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.authBack.setOnClickListener(v -> finish());
        binding.authSwitchLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SampleRegisterActivity.class));
            finish();
        });
        AuthPasswordVisibility.install(binding.authPassword);
        binding.authForgot.setOnClickListener(v -> AppToast.success(this,
                getString(R.string.auth_sample_title), getString(R.string.auth_sample_message)));

        binding.authSubmit.setOnClickListener(v -> {
            if (binding.authEmail.getText().toString().trim().isEmpty()) {
                binding.authEmail.setError(getString(R.string.auth_required));
                return;
            }
            if (binding.authPassword.getText().toString().isEmpty()) {
                binding.authPassword.setError(getString(R.string.auth_required));
                return;
            }
            String email = binding.authEmail.getText().toString().trim();
            String password = binding.authPassword.getText().toString();
            User user = new DBHelper(this).login(email, password);
            if (user == null) {
                AppToast.error(this, getString(R.string.auth_login_failed),
                        getString(R.string.auth_login_failed_message));
                return;
            }
            StaticUserModel.user = user;
            startActivity(new Intent(this, "Admin".equalsIgnoreCase(user.role)
                    ? AdminDashboardActivity.class : PersonalDashboardActivity.class));
            finish();
        });
    }
}
