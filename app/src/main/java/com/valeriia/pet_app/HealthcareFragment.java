package com.valeriia.pet_app;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.valeriia.pet_app.adapter.NoteAdapter;
import com.valeriia.pet_app.model.Note;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.widget.EditText;

public class HealthcareFragment extends Fragment implements NoteAdapter.OnNoteDeleteListener {

    private ArrayList<Note> notesList = new ArrayList<>();
    private NoteAdapter adapter;
    private FirebaseFirestore firestore;
    private int userId;

    private ArrayList<Note> allNotes = new ArrayList<>(); // Храним все заметки

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_healthcare, container, false);

        firestore = FirebaseFirestore.getInstance();
        userId = getUserIdFromPreferences();

        RecyclerView recyclerView = view.findViewById(R.id.healthcareFragmentRecyclerView);
        adapter = new NoteAdapter(notesList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadUserNotes();

        MaterialButton addNewNoteButton = view.findViewById(R.id.addNewNoteButton);
        addNewNoteButton.setOnClickListener(v -> openAddNoteFragment());

        MaterialButton filterNotesButton = view.findViewById(R.id.filterNotesButton);
        filterNotesButton.setOnClickListener(v -> openFilterDialog());

        return view;
    }

    private void openAddNoteFragment() {
        AddNoteFragment addNoteFragment = new AddNoteFragment();
        addNoteFragment.setTargetFragment(this, 1); // Устанавливаем текущий фрагмент как целевой для обратной связи

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, addNoteFragment) // Заменяем текущий фрагмент
                .addToBackStack(null) // Добавляем транзакцию в back stack для возможности вернуться назад
                .commit(); // Применяем транзакцию
    }


    private void openFilterDialog() {
        // Создаём диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter_notes, null);
        builder.setView(dialogView);

        EditText filterEditText = dialogView.findViewById(R.id.filterEditText);
        MaterialButton applyFilterButton = dialogView.findViewById(R.id.applyFilterButton);
        MaterialButton resetFilterButton = dialogView.findViewById(R.id.resetFilterButton);

        AlertDialog dialog = builder.create();

        applyFilterButton.setOnClickListener(v -> {
            String keyword = filterEditText.getText().toString().trim();
            if (!keyword.isEmpty()) {
                filterNotes(keyword);
            }
            dialog.dismiss();
        });

        resetFilterButton.setOnClickListener(v -> {
            resetFilter();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterNotes(String keyword) {
        ArrayList<Note> filteredNotes = new ArrayList<>();
        for (Note note : allNotes) {
            if (note.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    note.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                filteredNotes.add(note);
            }
        }
        adapter.updateNotes(filteredNotes);
    }

    private void resetFilter() {
        adapter.updateNotes(allNotes);
    }

    private void loadUserNotes() {
        notesList.clear();

        firestore.collection("notes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            allNotes.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Note note = new Note(
                                        document.getString("title"),
                                        document.getString("description"),
                                        document.getTimestamp("date").toDate(),
                                        document.getLong("userId").intValue()
                                );
                                allNotes.add(note);
                            }
                            adapter.updateNotes(allNotes);
                        } else {
                            Toast.makeText(getContext(), "No notes found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error getting notes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }

    @Override
    public void onNoteDelete(Note note) {
        deleteNoteFromFirestore(note);
    }

    private void deleteNoteFromFirestore(Note note) {
        firestore.collection("notes")
                .whereEqualTo("title", note.getTitle())
                .whereEqualTo("description", note.getDescription())
                .whereEqualTo("date", note.getDate())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Note deleted successfully!", Toast.LENGTH_SHORT).show();
                                        allNotes.remove(note);
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error deleting note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Note not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
