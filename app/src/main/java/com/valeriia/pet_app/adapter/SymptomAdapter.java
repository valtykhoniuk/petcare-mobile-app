package com.valeriia.pet_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.valeriia.pet_app.R;
import com.valeriia.pet_app.model.Symptom;

import java.util.ArrayList;
import java.util.List;

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder> {
    private final List<Symptom> symptoms;
    private final OnSymptomSelectedListener listener;

    public SymptomAdapter(List<Symptom> symptoms, OnSymptomSelectedListener listener) {
        this.symptoms = symptoms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SymptomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symptom, parent, false);
        return new SymptomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SymptomViewHolder holder, int position) {
        Symptom symptom = symptoms.get(position);
        holder.bind(symptom);
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public List<Symptom> getSelectedSymptoms() {
        List<Symptom> selectedSymptoms = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            if (symptom.isSelected()) {
                selectedSymptoms.add(symptom);
            }
        }
        return selectedSymptoms;
    }

    class SymptomViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;

        public SymptomViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bind(Symptom symptom) {
            checkBox.setText(symptom.getName());

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(symptom.isSelected());

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                symptom.setSelected(isChecked);
                listener.onSymptomSelected();
            });
        }
    }

    public interface OnSymptomSelectedListener {
        void onSymptomSelected();
    }
}
