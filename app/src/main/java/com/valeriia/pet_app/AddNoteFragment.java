package com.valeriia.pet_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.valeriia.pet_app.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNoteFragment extends Fragment {

    private FirebaseFirestore firestore;

    private EditText titleInput;
    private EditText descriptionInput;
    private EditText dateInput;
    private MaterialButton selectDateButton;
    private MaterialButton selectTimeButton;
    private MaterialButton saveButton;
    private Calendar calendar;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);

        titleInput = view.findViewById(R.id.titleNoteInput);
        descriptionInput = view.findViewById(R.id.descriptionNoteInput);
        dateInput = view.findViewById(R.id.dateNoteinput);
        selectDateButton = view.findViewById(R.id.selectDateNoteButton);
        selectTimeButton = view.findViewById(R.id.selectTimeNoteButton);
        saveButton = view.findViewById(R.id.saveNoteButton);

        calendar = Calendar.getInstance();
        userId = getUserIdFromPreferences();

        firestore = FirebaseFirestore.getInstance();

        selectDateButton.setOnClickListener(v -> showDatePickerDialog());
        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());
        saveButton.setOnClickListener(v -> saveNote());

        return view;
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInput();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateDateInput();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }

    private void updateDateInput() {
        String dateTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(calendar.getTime());
        dateInput.setText(dateTime);
    }

    private void saveNote() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        Date date = calendar.getTime();

        if (!title.isEmpty() && !description.isEmpty() && !dateInput.getText().toString().isEmpty()) {
            Note note = new Note(title, description, date, userId);

            DocumentReference newNoteRef = firestore.collection("notes").document();
            newNoteRef.set(note)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Note saved successfully!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Please enter title, description, and select date and time", Toast.LENGTH_SHORT).show();
        }
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}
