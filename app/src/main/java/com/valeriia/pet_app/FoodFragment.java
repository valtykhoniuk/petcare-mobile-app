package com.valeriia.pet_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.valeriia.pet_app.model.Item;
import com.valeriia.pet_app.model.Note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FoodFragment extends Fragment {

    private ArrayList<Item> listItems = new ArrayList<>();
    private Calendar selectedDateTime = Calendar.getInstance();
    private int userId; // ID пользователя
    private FirebaseFirestore firestore; // Экземпляр Firestore
    private ListView listView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        // Инициализация Firestore
        firestore = FirebaseFirestore.getInstance();

        userId = getUserIdFromPreferences(); // Получаем userId из SharedPreferences

        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(requireContext(), R.layout.item_food, listItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_food, parent, false);
                }

                Item item = getItem(position);
                TextView textView = convertView.findViewById(R.id.itemFoodTitle);
                if (item != null) {
                    String formattedDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                            .format(item.getCustomDate().toDate()); // Форматируем дату
                    textView.setText(item.getText() + " at " + formattedDate);
                }

                Button deleteButton = convertView.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(v -> {
                    Item itemToDelete = getItem(position);
                    deleteItemFromFirestore(itemToDelete); // Вызов метода удаления
                });
                return convertView;
            }
        };

        listView = view.findViewById(R.id.listViewFood);
        listView.setAdapter(adapter);

        EditText editTextFood = view.findViewById(R.id.editTextFood);
        Button datePickerButton = view.findViewById(R.id.datePickerButton);
        Button timePickerButton = view.findViewById(R.id.timePickerButton);
        Button buttonAdd = view.findViewById(R.id.addFoodToListButton);

        datePickerButton.setOnClickListener(v -> {
            int year = selectedDateTime.get(Calendar.YEAR);
            int month = selectedDateTime.get(Calendar.MONTH);
            int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year1);
                        selectedDateTime.set(Calendar.MONTH, monthOfYear);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }, year, month, day);
            datePickerDialog.show();
        });

        timePickerButton.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view12, hourOfDay, minute1) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute1);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        buttonAdd.setOnClickListener(v -> {
            String text = editTextFood.getText().toString();

            if (!text.isEmpty()) {
                // Создаем новый объект Item
                Item newItem = new Item(text, new Timestamp(selectedDateTime.getTime()), userId);
                listItems.add(0, newItem); // Добавляем в список
                adapter.notifyDataSetChanged();
                editTextFood.getText().clear();
                listView.setSelection(0);

                // Сохраняем новый элемент в Firestore
                saveItemToFirestore(newItem);
            } else {
                Toast.makeText(getContext(), "Please enter food", Toast.LENGTH_SHORT).show();
            }
        });

        fetchItemsFromFirestore(); // Загружаем элементы из Firestore

        return view;
    }

    private void fetchItemsFromFirestore() {
        firestore.collection("items")
                .whereEqualTo("userId", userId) // Фильтр по userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listItems.clear(); // Очищаем текущий список
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = new Item(
                                    document.getString("text"),
                                    document.getTimestamp("customDate"),
                                    document.getLong("userId").intValue()    // Преобразуем Long в int
                            );
                            listItems.add(item);
                        }
                        // Обновляем адаптер после загрузки данных
                        ((ArrayAdapter<Item>) listView.getAdapter()).notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error getting items: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteItemFromFirestore(Item item) {
        firestore.collection("items")
                .whereEqualTo("text", item.getText())
                .whereEqualTo("customDate", item.getCustomDate())
                .whereEqualTo("userId", item.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Удаляем документ
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Item deleted successfully!", Toast.LENGTH_SHORT).show();
                                        listItems.remove(item); // Удаляем элемент из списка
                                        ((ArrayAdapter<Item>) listView.getAdapter()).notifyDataSetChanged(); // Уведомляем адаптер
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error deleting item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveItemToFirestore(Item item) {
        DocumentReference newItemRef = firestore.collection("items").document(); // Создаем новый документ
        newItemRef.set(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Item added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("userId", -1); // По умолчанию -1, если userId не найден
    }
}
