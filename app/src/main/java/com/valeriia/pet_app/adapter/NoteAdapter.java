package com.valeriia.pet_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.valeriia.pet_app.R;
import com.valeriia.pet_app.model.Note;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private OnNoteDeleteListener onNoteDeleteListener;

    // Конструктор адаптера
    public NoteAdapter(List<Note> notesList, OnNoteDeleteListener onNoteDeleteListener) {
        this.notesList = notesList;
        this.onNoteDeleteListener = onNoteDeleteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        // Получаем текущую заметку
        Note note = notesList.get(position);

        // Проверяем, чтобы объект заметки не был null
        if (note != null) {
            holder.titleTextView.setText(note.getTitle() != null ? note.getTitle() : "No Title");
            holder.descriptionTextView.setText(note.getDescription() != null ? note.getDescription() : "No Description");
            holder.dateTextView.setText(note.getDate() != null ? note.getDate().toString() : "No Date");

            // Устанавливаем обработчик нажатия для удаления заметки
            holder.deleteNoteButton.setOnClickListener(v -> {
                if (onNoteDeleteListener != null) {
                    onNoteDeleteListener.onNoteDelete(note);
                }
            });
        }
    }

    // Возвращаем количество элементов в списке
    @Override
    public int getItemCount() {
        return notesList != null ? notesList.size() : 0;
    }

    // Метод для обновления списка заметок
    public void updateNotes(List<Note> newNotes) {
        if (newNotes != null) {
            notesList.clear();
            notesList.addAll(newNotes);
            notifyDataSetChanged(); // Обновляем список
        }
    }

    // ViewHolder для заметки
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView;
        View deleteNoteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.itemNoteTitle);
            descriptionTextView = itemView.findViewById(R.id.itemNoteDesc);
            dateTextView = itemView.findViewById(R.id.itemNoteTime);
            deleteNoteButton = itemView.findViewById(R.id.deleteNoteButton);
        }
    }

    // Интерфейс для обработки удаления заметки
    public interface OnNoteDeleteListener {
        void onNoteDelete(Note note);
    }
}

