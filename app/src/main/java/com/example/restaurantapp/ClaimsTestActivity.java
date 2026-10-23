package com.example.restaurantapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.adapters.ClaimTestAdapter;
import com.example.restaurantapp.databinding.ActivityClaimsTestBinding;
import com.example.restaurantapp.models.ClaimTestItem;
import java.util.ArrayList;

public class ClaimsTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(getColor(R.color.campus_background_start));
        getWindow().setNavigationBarColor(getColor(R.color.campus_background_start));
        getWindow().getDecorView().setSystemUiVisibility(0);
        ActivityClaimsTestBinding binding = ActivityClaimsTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<ClaimTestItem> claims = new ArrayList<>();
        claims.add(new ClaimTestItem("Black Wallet", "Cafeteria", "Claim submitted Oct 22, 2026", "Pending", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Student ID", "Library", "Claim approved Oct 20, 2026", "Approved", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Mobile Phone", "Learning Hub", "Claim rejected Oct 15, 2026", "Rejected", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Keys", "Gym", "Claim returned Oct 10, 2026", "Returned", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Cafeteria", "Claim submitted Oct 5, 2026", "Pending", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Student Center", "Claim approved Oct 3, 2026", "Approved", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Mobile Phone", "Science Hall", "Claim submitted Sep 29, 2026", "Pending", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Student ID", "Registrar", "Claim returned Sep 26, 2026", "Returned", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Keys", "Parking Area", "Claim rejected Sep 22, 2026", "Rejected", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Main Building", "Claim approved Sep 18, 2026", "Approved", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Cafeteria", "Claim returned Sep 15, 2026", "Returned", R.drawable.claim_wallet));
        claims.add(new ClaimTestItem("Mobile Phone", "Library", "Claim submitted Sep 12, 2026", "Pending", R.drawable.claim_phone));
        claims.add(new ClaimTestItem("Student ID", "Learning Hub", "Claim approved Sep 9, 2026", "Approved", R.drawable.claim_student_id));
        claims.add(new ClaimTestItem("Keys", "Gym", "Claim returned Sep 6, 2026", "Returned", R.drawable.claim_keys));
        claims.add(new ClaimTestItem("Water Bottle", "Cafeteria", "Claim rejected Sep 3, 2026", "Rejected", R.drawable.claim_bottle));
        claims.add(new ClaimTestItem("Black Wallet", "Admin Office", "Claim submitted Sep 1, 2026", "Pending", R.drawable.claim_wallet));

        binding.claimsCount.setText(String.valueOf(claims.size()));
        binding.claimsList.setLayoutManager(new LinearLayoutManager(this));
        binding.claimsList.setAdapter(new ClaimTestAdapter(this, claims));
        binding.claimsBack.setOnClickListener(v -> finish());
    }
}
