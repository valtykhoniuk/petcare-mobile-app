package com.valeriia.pet_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.valeriia.pet_app.R;
import com.valeriia.pet_app.model.CalendarUtils;
import com.valeriia.pet_app.model.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventAdapter extends ArrayAdapter<Event> {
    private int userId;
    private FirebaseFirestore firestore; // Firestore instance

    public EventAdapter(@NonNull Context context, List<Event> events, int userId) {
        super(context, 0, events);
        this.userId = userId;
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_cell, parent, false);
        }

        TextView eventCellTV = convertView.findViewById(R.id.eventHeadingCell);
        Button deleteEventButton = convertView.findViewById(R.id.deleteEventButton);

        String eventTitle = event.getName() + " " + CalendarUtils.formattedTime(event.getTime());
        eventCellTV.setText(eventTitle);

        deleteEventButton.setOnClickListener(v -> {
            deleteEventFromFirestore(event); // Call the new method
        });

        return convertView;
    }

    public void deleteEventFromFirestore(Event event) {
        // Extracting event details
        String eventName = event.getName();
        LocalDate eventDate = event.getDate();
        LocalTime eventTime = event.getTime();

        // Prepare query fields based on event details
        int eventYear = eventDate.getYear();
        int eventMonthValue = eventDate.getMonthValue(); // This corresponds to the 'monthValue' field in Firestore
        int eventDayOfMonth = eventDate.getDayOfMonth(); // This corresponds to the 'dayOfMonth' field in Firestore
        int eventHour = eventTime.getHour(); // This corresponds to the 'hour' field in Firestore
        int eventMinute = eventTime.getMinute(); // This corresponds to the 'minute' field in Firestore

        // Querying Firestore
        firestore.collection("events")
                .whereEqualTo("name", eventName)
                .whereEqualTo("date.year", eventYear) // Check year
                .whereEqualTo("date.monthValue", eventMonthValue) // Check monthValue
                .whereEqualTo("date.dayOfMonth", eventDayOfMonth) // Check dayOfMonth
                .whereEqualTo("time.hour", eventHour) // Check hour
                .whereEqualTo("time.minute", eventMinute) // Check minute
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Deleting the document from Firestore
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                                        remove(event); // Remove from the local adapter list
                                        Event.eventsList.remove(event); // Also remove from the static events list
                                        notifyDataSetChanged(); // Notify the adapter about the data change
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
