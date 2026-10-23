package com.example.restaurantapp.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.restaurantapp.R;
import com.example.restaurantapp.databinding.ActivityAdminItemShowBinding;
import com.example.restaurantapp.helpers.AppModal;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.helpers.SampleSeeder;
import com.example.restaurantapp.models.Item;
import java.text.NumberFormat;
import java.util.Locale;

public class AdminItemShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityAdminItemShowBinding binding = ActivityAdminItemShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        int id = getIntent().getIntExtra("itemId", 0);
        Item item = SampleSeeder.items().stream().filter(value -> value.id == id).findFirst().orElse(null);
        if (item == null) { finish(); return; }
        int image = item.image == null ? 0 : getResources().getIdentifier(item.image, "drawable", getPackageName());
        binding.itemShowImage.setImageResource(image == 0 ? R.drawable.sample_item_photo : image);
        binding.itemShowName.setText(item.name);
        binding.itemShowAboutHeading.setText("Vehicle".equalsIgnoreCase(item.tag)
                ? R.string.item_show_about_vehicle : R.string.item_show_about);
        binding.itemShowDescription.setText(item.description);
        binding.itemShowCategory.setText(item.category);
        binding.itemShowDetail.setText(item.detail);
        binding.itemShowTag.setText(item.tag);
        binding.itemShowStatus.setText(item.isAvailable == 1
                ? R.string.item_available : R.string.item_unavailable);
        binding.itemShowLocation.setText(item.location);
        binding.itemShowRating.setText(getString(R.string.item_rating, item.rating, item.reviewCount));
        NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
        money.setMaximumFractionDigits(2);
        binding.itemShowPrice.setText(money.format(item.price));
        binding.itemShowBack.setOnClickListener(v -> finish());
        binding.itemShowFavorite.setImageResource(
                item.isFavorite == 1 ? R.drawable.ic_item_heart_filled : R.drawable.ic_item_heart);
        binding.itemShowFavorite.setOnClickListener(v -> AppModal.confirm(this,
                getString(item.isFavorite == 1 ? R.string.item_remove_favorite : R.string.item_add_favorite),
                getString(R.string.item_favorite_message, item.name), () -> {
                    item.isFavorite = item.isFavorite == 1 ? 0 : 1;
                    binding.itemShowFavorite.setImageResource(item.isFavorite == 1
                            ? R.drawable.ic_item_heart_filled : R.drawable.ic_item_heart);
                    AppToast.success(this, getString(R.string.item_favorite_updated),
                            getString(R.string.item_favorite_preview));
                }));
        binding.itemShowShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, item.name + "\n" + item.description);
            startActivity(Intent.createChooser(share, getString(R.string.item_show_share)));
        });
    }
}
