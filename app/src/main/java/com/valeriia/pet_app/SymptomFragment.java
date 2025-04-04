package com.valeriia.pet_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.valeriia.pet_app.R;
import com.valeriia.pet_app.adapter.SymptomAdapter;
import com.valeriia.pet_app.model.Symptom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymptomFragment extends Fragment {
    private RecyclerView recyclerView;
    private SymptomAdapter symptomAdapter;
    private Button diagnoseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_symptom, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        diagnoseButton = view.findViewById(R.id.diagnoseButton);

        List<Symptom> symptoms = getSymptomsList();
        symptomAdapter = new SymptomAdapter(symptoms, this::updateDiagnoseButtonState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(symptomAdapter);

        diagnoseButton.setOnClickListener(v -> {
            showPossibleDiseases(symptomAdapter.getSelectedSymptoms());
        });

        updateDiagnoseButtonState();

        return view;
    }

    private void updateDiagnoseButtonState() {
        diagnoseButton.setEnabled(!symptomAdapter.getSelectedSymptoms().isEmpty());
    }

    private List<Symptom> getSymptomsList() {
        return Arrays.asList(
                new Symptom("Loss of appetite"),
                new Symptom("Vomiting"),
                new Symptom("Diarrhea"),
                new Symptom("Lethargy"),
                new Symptom("Coughing"),
                new Symptom("Sneezing"),
                new Symptom("Increased thirst"),
                new Symptom("Frequent urination"),
                new Symptom("Weight loss"),
                new Symptom("Hair loss"),
                new Symptom("Itching or scratching"),
                new Symptom("Red or swollen gums"),
                new Symptom("Limping"),
                new Symptom("Shaking head"),
                new Symptom("Difficulty breathing"),
                new Symptom("Nasal discharge"),
                new Symptom("Swelling in abdomen"),
                new Symptom("Unusual aggression or anxiety"),
                new Symptom("Excessive drooling"),
                new Symptom("Seizures")
        );
    }

    private void showPossibleDiseases(List<Symptom> selectedSymptoms) {
        Map<String, Integer> diseaseProbabilities = diagnose(selectedSymptoms);

        StringBuilder message = new StringBuilder();
        if (diseaseProbabilities.isEmpty()) {
            message.append("No diseases found for the selected symptoms.");
        } else {
            for (Map.Entry<String, Integer> entry : diseaseProbabilities.entrySet()) {
                message.append(entry.getKey()).append(": ").append(entry.getValue()).append("% probability\n");
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Diagnosis Result")
                .setMessage(message.toString())
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private Map<String, Integer> diagnose(List<Symptom> selectedSymptoms) {
        Map<String, Integer> diseaseProbabilities = new HashMap<>();

        // Define symptoms for each disease
        Map<String, List<String>> diseaseSymptoms = new HashMap<>();
        diseaseSymptoms.put("Gastroenteritis", Arrays.asList("Loss of appetite", "Vomiting", "Diarrhea", "Lethargy"));
        diseaseSymptoms.put("Respiratory Infection", Arrays.asList("Coughing", "Sneezing", "Nasal discharge", "Difficulty breathing"));
        diseaseSymptoms.put("Diabetes", Arrays.asList("Increased thirst", "Frequent urination", "Weight loss"));
        diseaseSymptoms.put("Allergies", Arrays.asList("Hair loss", "Itching or scratching"));
        diseaseSymptoms.put("Dental Disease", Arrays.asList("Red or swollen gums", "Loss of appetite"));
        diseaseSymptoms.put("Arthritis or Joint Issues", Arrays.asList("Limping", "Swelling in abdomen"));
        diseaseSymptoms.put("Kidney Disease", Arrays.asList("Lethargy", "Increased thirst", "Weight loss"));
        diseaseSymptoms.put("Neurological Disorder", Arrays.asList("Unusual aggression or anxiety", "Seizures"));

        // Calculate probability for each disease
        for (Map.Entry<String, List<String>> entry : diseaseSymptoms.entrySet()) {
            String disease = entry.getKey();
            List<String> symptomsForDisease = entry.getValue();

            // Count selected symptoms that match this disease
            int matchedSymptoms = 0;
            for (Symptom symptom : selectedSymptoms) {
                if (symptomsForDisease.contains(symptom.getName())) {
                    matchedSymptoms++;
                }
            }

            // Calculate probability as a percentage
            if (matchedSymptoms > 0) {
                int probability = (int) ((matchedSymptoms / (double) symptomsForDisease.size()) * 100);
                diseaseProbabilities.put(disease, probability);
            }
        }

        return diseaseProbabilities;
    }

}



