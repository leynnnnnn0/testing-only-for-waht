package com.example.restaurantapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.restaurantapp.adapters.FindChatAdapter;
import com.example.restaurantapp.databinding.ActivityCurveTestBinding;
import com.example.restaurantapp.databinding.BottomSheetFindItemBinding;
import com.example.restaurantapp.helpers.AppToast;
import com.example.restaurantapp.models.FindChatMessage;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CurveTestActivity extends AppCompatActivity {
    private final ArrayList<FindChatMessage> messages = new ArrayList<>();
    private BottomSheetFindItemBinding chatBinding;
    private FindChatAdapter chatAdapter;
    private int step;
    private int requiredPhotos = 2;
    private int photoCount;
    private String itemName, description, location, lossDate;

    private final ActivityResultLauncher<String> photoPicker = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), photos -> {
                boolean invalid = requiredPhotos == 3 ? photos.size() != 3
                        : photos.size() < 2 || photos.size() > 3;
                if (invalid) {
                    String amount = requiredPhotos == 3 ? "exactly 3 photos" : "2–3 photos";
                    AppToast.error(this, "Select " + amount, "Please choose " + amount + " of your item.");
                    return;
                }
                addUser(photos.size() + " photos attached");
                photoCount = photos.size();
                chatBinding.findChatAttach.setVisibility(View.GONE);
                showReportSummary();
            });

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(getColor(R.color.campus_background_start));
        getWindow().setNavigationBarColor(getColor(R.color.campus_background_start));
        getWindow().getDecorView().setSystemUiVisibility(0);
        ActivityCurveTestBinding binding = ActivityCurveTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ObjectAnimator jump = ObjectAnimator.ofFloat(binding.campusHelperPet, "translationY",
                0, -28 * getResources().getDisplayMetrics().density, 0, 0, 0);
        jump.setDuration(2000);
        jump.setRepeatCount(ValueAnimator.INFINITE);
        jump.start();
        binding.findItemButton.setOnClickListener(v -> openFindChat());
    }

    private void openFindChat() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        chatBinding = BottomSheetFindItemBinding.inflate(getLayoutInflater());
        sheet.setContentView(chatBinding.getRoot());
        messages.clear();
        step = 0;
        requiredPhotos = 2;
        itemName = "";
        description = "";
        location = "";
        lossDate = "";
        photoCount = 0;
        chatAdapter = new FindChatAdapter(this, messages);
        chatBinding.findChatList.setLayoutManager(new LinearLayoutManager(this));
        chatBinding.findChatList.setAdapter(chatAdapter);
        chatBinding.findChatClose.setOnClickListener(v -> sheet.dismiss());
        chatBinding.findChatSend.setOnClickListener(v -> sendAnswer());
        chatBinding.findChatInput.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_SEND) {
                sendAnswer();
                return true;
            }
            return false;
        });
        chatBinding.findChatAttach.setOnClickListener(v -> photoPicker.launch("image/*"));
        addPet("Did you lose an item?");
        chatBinding.findChatInput.setHint("Reply Yes to begin…");
        sheet.show();
        View panel = sheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        panel.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * .9f);
        panel.requestLayout();
        sheet.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void sendAnswer() {
        String answer = chatBinding.findChatInput.getText().toString().trim();
        if (answer.isEmpty() || step == 5 || step == 6) return;
        chatBinding.findChatInput.setText("");
        addUser(answer);
        if (step == 8) {
            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                addPet("Your lost report has been submitted. I hope you find your item soon.");
                step = 6;
                finishConversation("Lost report submitted");
            } else if (answer.equalsIgnoreCase("no") || answer.toLowerCase(Locale.ROOT).contains("not")
                    || answer.equalsIgnoreCase("cancel")) {
                addPet("Your report was not submitted.");
                step = 6;
                finishConversation("Report cancelled");
            } else {
                addPet("Please reply Yes to submit the report or No to cancel.");
            }
        } else if (step == 7) {
            if (answer.toLowerCase(Locale.ROOT).contains("not")) {
                requiredPhotos = 3;
                addPet("Please send exactly 3 clear photos of your item so I can create a lost report.");
                step = 5;
                chatBinding.findChatInput.setEnabled(false);
                chatBinding.findChatInput.setHint("Attach exactly 3 photos");
                chatBinding.findChatAttach.setVisibility(View.VISIBLE);
                chatBinding.findChatSend.setVisibility(View.GONE);
            } else {
                addPet("Great! Open the matching item to continue your claim.");
                step = 6;
                chatBinding.findChatInput.setEnabled(false);
                chatBinding.findChatInput.setHint("Match selected");
                chatBinding.findChatSend.setEnabled(false);
            }
        } else if (step == 0) {
            if (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("y")) {
                addPet("No problem. Reply Yes whenever you need help finding something.");
                return;
            }
            step = 1;
            addPet("What is the name of the item you lost?");
            chatBinding.findChatInput.setHint("Example: Wallet");
        } else if (step == 1) {
            itemName = answer;
            step = 2;
            addPet("Could you please describe it?");
            chatBinding.findChatInput.setHint("Describe color, pattern, or marks…");
        } else if (step == 2) {
            description = answer;
            step = 3;
            addPet("Where did you lose it?");
            chatBinding.findChatInput.setHint("Example: Cafeteria");
        } else if (step == 3) {
            location = answer;
            step = 4;
            addPet("When did you lose it?");
            chatBinding.findChatInput.setHint("Select a date…");
            showDatePicker();
        } else {
            lossDate = answer;
            showResults();
        }
    }

    private void showDatePicker() {
        Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (picker, year, month, day) -> {
            date.set(year, month, day);
            lossDate = new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date.getTime());
            addUser(lossDate);
            showResults();
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showResults() {
        boolean matched = itemName.toLowerCase(Locale.ROOT).contains("wallet")
                && location.toLowerCase(Locale.ROOT).contains("cafeteria");
        if (matched) {
            addMessage(new FindChatMessage(FindChatMessage.MATCHES, ""));
            addPet("Is one of these yours? Reply not if neither item matches.");
            step = 7;
            chatBinding.findChatInput.setHint("Reply not if neither matches…");
        } else {
            addPet("I couldn’t find a possible match yet.");
            addPet("Please attach at least 2 photos of your item, with a maximum of 3.");
            step = 5;
            chatBinding.findChatInput.setEnabled(false);
            chatBinding.findChatInput.setHint("Attach 2–3 photos");
            chatBinding.findChatAttach.setVisibility(View.VISIBLE);
            chatBinding.findChatSend.setVisibility(View.GONE);
        }
    }

    private void addPet(String text) {
        addMessage(new FindChatMessage(FindChatMessage.PET, text));
    }

    private void showReportSummary() {
        addPet("Please review your lost report:\n\nItem: " + itemName
                + "\nDescription: " + description
                + "\nLocation: " + location
                + "\nDate lost: " + lossDate
                + "\nPhotos: " + photoCount);
        addPet("Submit this lost report? Reply Yes to submit or No to cancel.");
        step = 8;
        chatBinding.findChatInput.setEnabled(true);
        chatBinding.findChatInput.setHint("Reply Yes or No…");
        chatBinding.findChatSend.setVisibility(View.VISIBLE);
        chatBinding.findChatSend.setEnabled(true);
    }

    private void finishConversation(String hint) {
        chatBinding.findChatInput.setEnabled(false);
        chatBinding.findChatInput.setHint(hint);
        chatBinding.findChatSend.setEnabled(false);
    }

    private void addUser(String text) {
        addMessage(new FindChatMessage(FindChatMessage.USER, text));
    }

    private void addMessage(FindChatMessage message) {
        messages.add(message);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatBinding.findChatList.post(() -> chatBinding.findChatList.smoothScrollToPosition(messages.size() - 1));
    }
}
