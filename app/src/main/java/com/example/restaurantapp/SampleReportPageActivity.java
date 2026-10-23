package com.example.restaurantapp;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.databinding.ActivitySampleReportPageBinding;
import com.example.restaurantapp.databinding.ReportBarRowBinding;
import com.example.restaurantapp.helpers.AppToast;
import java.io.OutputStream;

public class SampleReportPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivitySampleReportPageBinding binding = ActivitySampleReportPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.reportBack.setOnClickListener(v -> finish());
        SpannableString title = new SpannableString("↓  " + getString(R.string.report_download_pdf));
        title.setSpan(new ImageSpan(this, R.drawable.ic_report_download), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.reportDownload.setText(title);
        ActivityResultLauncher<String> savePdf = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/pdf"), uri -> {
                    if (uri == null) return;
                    View content = binding.reportContent;
                    PdfDocument pdf = new PdfDocument();
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        PdfDocument.Page page = pdf.startPage(new PdfDocument.PageInfo.Builder(595, 842, 1).create());
                        Canvas canvas = page.getCanvas();
                        canvas.translate(24, 24);
                        float scale = Math.min(547f / content.getWidth(), 794f / content.getHeight());
                        canvas.scale(scale, scale);
                        content.draw(canvas);
                        pdf.finishPage(page);
                        pdf.writeTo(out);
                        AppToast.success(this, "PDF saved", "Your report is ready.");
                    } catch (Exception e) {
                        AppToast.error(this, "Save failed", "Please try again.");
                    } finally {
                        pdf.close();
                    }
                });
        binding.reportDownload.setOnClickListener(v -> savePdf.launch("report.pdf"));
        int[] counts = {320, 180, 65};
        String[] labels = {"Completed", "In progress", "Cancelled"};
        int[] colors = {themeColor(R.attr.rolePrimaryDark), themeColor(R.attr.rolePrimary),
                themeColor(R.attr.roleAccent)};
        int total = counts[0] + counts[1] + counts[2];
        binding.reportTotal.setText(String.valueOf(total));
        ReportBarRowBinding[] rows = {binding.reportCompleted, binding.reportProgress, binding.reportCancelled};
        for (int i = 0; i < counts.length; i++) {
            rows[i].reportBarLabel.setText(labels[i]);
            rows[i].reportBarValue.setText(String.valueOf(counts[i]));
            rows[i].reportBar.setMax(total);
            rows[i].reportBar.setProgress(counts[i], true);
            rows[i].reportBar.setProgressTintList(ColorStateList.valueOf(colors[i]));
        }
    }

    private int themeColor(int attribute) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attribute, value, true);
        return value.data;
    }
}
