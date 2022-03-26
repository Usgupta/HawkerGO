package com.example.hawkergo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.hawkergo.R;
import com.example.hawkergo.models.HawkerCentre;
import com.example.hawkergo.models.HawkerStall;
import com.example.hawkergo.models.OpeningHours;
import com.example.hawkergo.models.Tags;
import com.example.hawkergo.services.firebase.interfaces.DbEventHandler;
import com.example.hawkergo.services.firebase.repositories.HawkerCentresRepository;
import com.example.hawkergo.services.firebase.repositories.TagsRepository;
import com.example.hawkergo.utils.textValidator.TextValidatorHelper;
import com.example.hawkergo.utils.ui.DynamicEditTextManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddHawkerStall extends AppCompatActivity {
    String[] openingDaysChipsOptions;
    List<String> categories;
    HawkerCentre hawkerCentre;

    // default opening and closing time
    int openingHour = 8, openingMinute = 30;
    int closingHour = 21, closingMinute = 30;

    // view controllers
    ChipGroup openingHoursChipGrpController, categoriesChipGrpController;
    EditText nameFieldController, floorFieldController, unitNumFieldController;
    TextView openingHoursErrorTextController, selectCategoryErrorTextController, mainTitleController;
    Button openingTimeButtonController, closingTimeButtonController, submitButtonController, addMoreFavFoodButton;


    /**
     * Dynamic edit text
     * This manager abstracts out logic required to programmatically add EditText views into the form
     * Enables user to add as many favourite foods as they want to
     */
    DynamicEditTextManager dynamicEditTextManager = new DynamicEditTextManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hawker_stall);
        this.initViews();
        this.handleIntent();
        this.inflateOpeningDaysChips();
        this.getAllTagsAndInflateChips();
        this.attachButtonEventListeners();
        dynamicEditTextManager.init(this, findViewById(R.id.favourite_food_container));
        dynamicEditTextManager.addEditTextField();
    }


    private void handleIntent() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        HawkerCentresRepository.getHawkerCentreByID(id, new DbEventHandler<HawkerCentre>() {
            @Override
            public void onSuccess(HawkerCentre o) {
                hawkerCentre = o;
                mainTitleController.setText("Adding a stall to " + hawkerCentre.name);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void initViews() {
        openingHoursChipGrpController = findViewById(R.id.opening_days_chip_group);
        categoriesChipGrpController = findViewById(R.id.categories_chip_group);
        mainTitleController = findViewById(R.id.hawker_centre_title);
        nameFieldController = findViewById(R.id.name_field);
        floorFieldController = findViewById(R.id.floor_field);
        unitNumFieldController = findViewById(R.id.unit_num_field);
        openingTimeButtonController = findViewById(R.id.opening_time_button);
        closingTimeButtonController = findViewById(R.id.closing_time_button);
        openingHoursErrorTextController = findViewById(R.id.opening_hours_error);
        selectCategoryErrorTextController = findViewById(R.id.select_category_error);
        addMoreFavFoodButton = findViewById(R.id.add_more_button);
        submitButtonController = findViewById(R.id.submit_button);
        openingHoursErrorTextController.setText("");
        if (openingHour != 0 && openingMinute != 0) {
            openingTimeButtonController.setText(String.format(Locale.getDefault(), "%02d:%02d", openingHour, openingMinute));
        }
        if (closingHour != 0 && closingMinute != 0) {
            closingTimeButtonController.setText(String.format(Locale.getDefault(), "%02d:%02d", closingHour, closingMinute));
        }
    }

    private void inflateOpeningDaysChips() {
        openingDaysChipsOptions = getResources().getStringArray(R.array.chip_options);
        for (int i = 0; i < openingDaysChipsOptions.length; i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip, openingHoursChipGrpController, false);
            chip.setText(openingDaysChipsOptions[i]);
            chip.setId(i);
            openingHoursChipGrpController.addView(chip);
        }
    }

    private void getAllTagsAndInflateChips() {
        TagsRepository.getAllTags(
                new DbEventHandler<Tags>() {
                    @Override
                    public void onSuccess(Tags o) {
                        categories = o.getCategories();
                        for (int i = 0; i < categories.size(); i++) {
                            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_chip, categoriesChipGrpController, false);
                            chip.setText(categories.get(i));
                            chip.setId(i);
                            categoriesChipGrpController.addView(chip);
                        }

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                }
        );
    }

    private void attachButtonEventListeners() {
        addMoreFavFoodButton.setOnClickListener(
                new View.OnClickListener(

                ) {
                    @Override
                    public void onClick(View view) {
                        dynamicEditTextManager.addEditTextField();
                    }
                }
        );
        submitButtonController.setOnClickListener(
                new View.OnClickListener(
                ) {
                    @Override
                    public void onClick(View view) {
                        onClickSubmitButton();
                    }
                }
        );
        openingTimeButtonController.setOnClickListener(
                new View.OnClickListener(
                ) {
                    @Override
                    public void onClick(View view) {
                        onOpeningTimeButtonClick();
                    }
                }
        );
        closingTimeButtonController.setOnClickListener(
                new View.OnClickListener(
                ) {
                    @Override
                    public void onClick(View view) {
                        onClosingTimeButtonClick();
                    }
                }
        );
    }

    private boolean validateFloorField() {
        boolean isValid = true;
        String text = floorFieldController.getText().toString();
        if (TextValidatorHelper.isNullOrEmpty(text)) {
            isValid = false;
            floorFieldController.setError("Please fill in the floor number");
        }
        if (!TextValidatorHelper.isNumeric(text)) {
            isValid = false;
            floorFieldController.setError("Please fill in only numeric values");
        }
        return isValid;
    }

    private boolean validateUnitNumField() {
        boolean isValid = true;
        String text = unitNumFieldController.getText().toString();
        if (TextValidatorHelper.isNullOrEmpty(text)) {
            isValid = false;
            unitNumFieldController.setError("Please fill in the unit number");
        }
        if (!TextValidatorHelper.isNumeric(text)) {
            isValid = false;
            unitNumFieldController.setError("Please fill in only numeric values");
        }
        return isValid;
    }

    private boolean validateNameField() {
        boolean isValid = true;
        String text = nameFieldController.getText().toString();
        if (TextValidatorHelper.isNullOrEmpty(text)) {
            isValid = false;
            nameFieldController.setError("Please fill in the name");
        }
        return isValid;
    }

    private boolean validateOpeningHoursChips() {
        boolean isValid = true;
        List<Integer> x = openingHoursChipGrpController.getCheckedChipIds();
        if (x.size() > 0) {
            openingHoursErrorTextController.setText("");
        } else {
            isValid = false;
            openingHoursErrorTextController.setText("Please select at least 1 day");
        }
        return isValid;
    }

    private boolean validateCategoriesChips() {
        boolean isValid = true;
        List<Integer> x = categoriesChipGrpController.getCheckedChipIds();
        if (x.size() > 0) {
            selectCategoryErrorTextController.setText("");
        } else {
            isValid = false;
            selectCategoryErrorTextController.setText("Please select at least 1 category");
        }
        return isValid;
    }

    private void onOpeningTimeButtonClick() {
        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                openingHour = selectedHour;
                openingMinute = selectedMinute;
                openingTimeButtonController.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerListener, openingHour, openingMinute, true);
        timePickerDialog.setTitle("Opening time");
        timePickerDialog.show();
    }

    private void onClosingTimeButtonClick() {
        TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                closingHour = selectedHour;
                closingMinute = selectedMinute;
                closingTimeButtonController.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timePickerListener, closingHour, closingMinute, true);
        timePickerDialog.setTitle("Closing time");
        timePickerDialog.show();
    }

    private void onClickSubmitButton() {
        dynamicEditTextManager.getAllFavFoodItems();
        Boolean[] validationArray = {
                validateOpeningHoursChips(),
                validateFloorField(),
                validateUnitNumField(),
                validateNameField(),
                validateCategoriesChips()
        };
        boolean isAllValid = !Arrays.asList(validationArray).contains(false);

        if (isAllValid) {
            List<Integer> checkedOpeningDays = openingHoursChipGrpController.getCheckedChipIds();
            List<Integer> checkedCategories = categoriesChipGrpController.getCheckedChipIds();
            // init fields needed to be saved to firestore
            String stallName, formattedAddress, formattedOpeningDays, formattedOpeningTime;
            ArrayList<String> selectedCategories = new ArrayList<>();

            stallName = nameFieldController.getText().toString();
            formattedAddress = "#" + floorFieldController.getText().toString() + "-" + unitNumFieldController.getText().toString();
            if (checkedOpeningDays.size() == openingDaysChipsOptions.length) {
                formattedOpeningDays = "Daily";
            } else {
                int lastElement = checkedOpeningDays.get(checkedOpeningDays.size() - 1);
                StringBuilder formattedOpeningDaysBuilder = new StringBuilder("Opens every");
                for (int i : checkedOpeningDays) {
                    String day = openingDaysChipsOptions[i];
                    formattedOpeningDaysBuilder.append(" ").append(day);
                    if (i != lastElement) formattedOpeningDaysBuilder.append(",");
                }
                formattedOpeningDays = formattedOpeningDaysBuilder.toString();
            }

            formattedOpeningTime = Integer.toString(openingHour) + ":" + Integer.toString(openingMinute) +
                    " - " +
                    Integer.toString(closingHour) + ":" + Integer.toString(closingMinute);

            for (int i : checkedCategories) {
                selectedCategories.add(categories.get(i));
            }


            OpeningHours newOpeningHours = new OpeningHours(
                    formattedOpeningDays,
                    formattedOpeningTime
            );
            HawkerStall newHawkerStall = new HawkerStall(
                    formattedAddress, stallName, newOpeningHours, "", new ArrayList<>(), new ArrayList<>(), selectedCategories
            );
        }

    }
}