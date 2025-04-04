package com.valeriia.pet_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.valeriia.pet_app.adapter.EventAdapter;
import com.valeriia.pet_app.model.CalendarUtils;
import com.valeriia.pet_app.model.Event;
import com.valeriia.pet_app.adapter.CalendarAdapter;
import com.valeriia.pet_app.model.Note;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import static com.valeriia.pet_app.model.CalendarUtils.daysInWeekArray;
import static com.valeriia.pet_app.model.CalendarUtils.monthYearFromDate;

public class CalendarWeekFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private Button previousButton2, nextButton2, newEventButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_week, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidgets(view);
        setWeekView();

        previousButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousWeekAction();
            }
        });

        nextButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWeekAction();
            }
        });

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventAction();
            }
        });
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearWeek);
        eventListView = view.findViewById(R.id.eventListView);
        previousButton2 = view.findViewById(R.id.previousButtonWeek);
        nextButton2 = view.findViewById(R.id.nextButtonWeek);
        newEventButton = view.findViewById(R.id.newEventButton);
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter(); // Call to set events for the selected date
    }

    public void previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter(); // Refresh events when the fragment resumes
    }

    private void setEventAdapter() {
        int userId = getUserIdFromPreferences(); // Get the current user ID
        LocalDate selectedDate = CalendarUtils.selectedDate; // Get the selected date

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("events")
                .whereEqualTo("userId", userId) // Filter by user ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> userEvents = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");

                            // Extracting date fields
                            Map<String, Object> dateMap = (Map<String, Object>) document.get("date");
                            int year = ((Long) dateMap.get("year")).intValue();
                            int monthValue = ((Long) dateMap.get("monthValue")).intValue();
                            int dayOfMonth = ((Long) dateMap.get("dayOfMonth")).intValue();

                            // Creating LocalDate
                            LocalDate localDate = LocalDate.of(year, monthValue, dayOfMonth);

                            // Extracting time fields
                            Map<String, Object> timeMap = (Map<String, Object>) document.get("time");
                            int hour = ((Long) timeMap.get("hour")).intValue();
                            int minute = ((Long) timeMap.get("minute")).intValue();
                            // Assuming seconds and nanoseconds are not needed; can be added if required

                            // Creating LocalTime
                            LocalTime localTime = LocalTime.of(hour, minute);

                            // Create the Event object
                            Event event = new Event(name, localDate, localTime, userId);

                            // Check if the event date matches the selected date
                            if (event.getDate().equals(selectedDate)) {
                                userEvents.add(event); // Add event to the list if it matches the selected date
                            }
                        }
                        // Set the adapter with the fetched events
                        EventAdapter eventAdapter = new EventAdapter(getContext(), userEvents, userId);
                        eventListView.setAdapter(eventAdapter);
                    } else {
                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("userId", -1); // Retrieve user ID from shared preferences
    }

    public void newEventAction() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new EventEditFragment())
                .addToBackStack(null)
                .commit();
    }
}
