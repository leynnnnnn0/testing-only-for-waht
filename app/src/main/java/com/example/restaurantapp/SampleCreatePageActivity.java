package com.example.restaurantapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.restaurantapp.databinding.ActivitySampleCreatePageBinding;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import java.util.Calendar;
import java.util.Locale;

public class SampleCreatePageActivity extends AppCompatActivity {
    ActivitySampleCreatePageBinding binding;
    ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                binding.createImage.setImageURI(uri);
                binding.createImage.setVisibility(View.VISIBLE);
                binding.createImagePrompt.setVisibility(View.GONE);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySampleCreatePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        binding.createBack.setOnClickListener(v -> finish());
        binding.createName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.createNameError.setVisibility(View.GONE);
            }
            public void afterTextChanged(Editable s) { }
        });
        binding.createImagePicker.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.createDate.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            new DatePickerDialog(this, (picker, year, month, day) ->
                    binding.createDate.setText(String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", month + 1, day, year)),
                    today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
        });
        binding.createTime.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new TimePickerDialog(this, (picker, hour, minute) ->
                    binding.createTime.setText(String.format(Locale.getDefault(),
                            "%02d:%02d", hour, minute)),
                    now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();
        });
        binding.createSave.setOnClickListener(v -> {
            if (binding.createName.getText().toString().trim().isEmpty()) {
                binding.createNameError.setVisibility(View.VISIBLE);
                binding.createName.requestFocus();
                return;
            }
            AppModal.confirm(this, getString(R.string.create_confirm_title),
                    getString(R.string.create_confirm_message), () ->
                            AppToast.success(this, getString(R.string.create_done_title),
                                    getString(R.string.create_done_message)));
        });
    }
}
