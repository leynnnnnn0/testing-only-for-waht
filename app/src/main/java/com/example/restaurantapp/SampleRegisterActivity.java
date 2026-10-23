package com.example.restaurantapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.databinding.ActivitySampleRegisterBinding;
import com.example.restaurantapp.helpers.AuthPasswordVisibility;
import com.example.restaurantapp.helpers.DBHelper;
import com.example.restaurantapp.models.StaticUserModel;
import com.example.restaurantapp.models.User;

public class SampleRegisterActivity extends AppCompatActivity {
    private ActivitySampleRegisterBinding binding;
    private Uri selectedImage;
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                selectedImage = uri;
                binding.authImagePreview.setImageURI(uri);
                binding.authImagePreview.setVisibility(View.VISIBLE);
                binding.authImagePrompt.setVisibility(View.GONE);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySampleRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.authBack.setOnClickListener(v -> finish());
        binding.authSwitchLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SampleLoginActivity.class));
            finish();
        });
        AuthPasswordVisibility.install(binding.authPassword);
        binding.authImagePicker.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.authSubmit.setOnClickListener(v -> submitPreview());
    }

    private void submitPreview() {
        if (binding.authFirstName.getText().toString().trim().isEmpty()) {
            binding.authFirstName.setError(getString(R.string.auth_required));
            return;
        }
        if (binding.authLastName.getText().toString().trim().isEmpty()) {
            binding.authLastName.setError(getString(R.string.auth_required));
            return;
        }
        if (binding.authEmail.getText().toString().trim().isEmpty()) {
            binding.authEmail.setError(getString(R.string.auth_required));
            return;
        }
        if (binding.authPassword.getText().toString().isEmpty()) {
            binding.authPassword.setError(getString(R.string.auth_required));
            return;
        }
        if (!binding.authPassword.getText().toString()
                .equals(binding.authConfirmPassword.getText().toString())) {
            binding.authConfirmPassword.setError(getString(R.string.auth_password_mismatch));
            return;
        }
        String email = binding.authEmail.getText().toString().trim();
        DBHelper db = new DBHelper(this);
        if (db.emailExists(email)) {
            binding.authEmail.setError(getString(R.string.auth_email_exists));
            return;
        }
        User user = new User();
        user.fullName = binding.authFirstName.getText().toString().trim() + " "
                + binding.authLastName.getText().toString().trim();
        user.email = email;
        user.password = binding.authPassword.getText().toString();
        user.role = "Member";
        user.isActive = 1;
        user.image = selectedImage == null ? null : selectedImage.toString();
        if (!db.register(user)) {
            binding.authEmail.setError(getString(R.string.auth_email_exists));
            return;
        }
        StaticUserModel.user = user;
        startActivity(new Intent(this, PersonalDashboardActivity.class));
        finish();
    }
}
