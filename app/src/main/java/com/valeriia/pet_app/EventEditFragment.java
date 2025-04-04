package com.valeriia.pet_app;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.valeriia.pet_app.model.CalendarUtils;
import com.valeriia.pet_app.model.Event;

import java.time.LocalTime;
import java.util.Calendar;

public class EventEditFragment extends Fragment {

    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    private Button saveEventButton;
    private LocalTime selectedTime;
    private int userId; // userId is now an integer
    private FirebaseFirestore firestore; // Firestore instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets(view);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        userId = getUserIdFromPreferences(); // Retrieve userId as an integer

        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));

        eventTimeTV.setOnClickListener(v -> showTimePickerDialog());

        saveEventButton.setOnClickListener(v -> saveEventAction());
    }

    private void initWidgets(View view) {
        eventNameET = view.findViewById(R.id.eventNameInput);
        eventDateTV = view.findViewById(R.id.eventDateInput);
        eventTimeTV = view.findViewById(R.id.eventTimeInput);
        saveEventButton = view.findViewById(R.id.saveEventButton);
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute1) -> {
                    selectedTime = LocalTime.of(hourOfDay, minute1);
                    eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(selectedTime));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public void saveEventAction() {
        String eventName = eventNameET.getText().toString();

        if (eventName.isEmpty()) {
            Toast.makeText(getActivity(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null) {
            selectedTime = LocalTime.now();
        }

        // Create a new Event object
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, selectedTime, userId); // Pass userId

        // Save the new event to Firestore
        saveEventToFirestore(newEvent);
    }

    private void saveEventToFirestore(Event event) {
        DocumentReference newEventRef = firestore.collection("events").document(); // Create a new document reference
        newEventRef.set(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("userId", -1); // Default to -1 if no userId found
    }
}
