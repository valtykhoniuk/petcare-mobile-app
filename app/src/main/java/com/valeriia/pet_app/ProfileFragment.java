package com.valeriia.pet_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.valeriia.pet_app.model.Pet;

public class ProfileFragment extends Fragment {

    private TextView usernameIdView, petNameTextView, petWeightTextView,
            petAgeTextView, petBreedTextView, petGenderTextView;
    private Button deleteAccountButton, editPetNameButton, editPetWeightButton, editPetAgeButton, editPetBreedButton, editPetGenderButton;
    private ImageView profileImageView;

    private FirebaseFirestore db;

    private int userId; // The user ID of the currently logged-in user
    private String petId; // The pet ID for updates

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        usernameIdView = view.findViewById(R.id.userid);
        petNameTextView = view.findViewById(R.id.petName);
        petWeightTextView = view.findViewById(R.id.petWeight);
        petAgeTextView = view.findViewById(R.id.petAge);
        petBreedTextView = view.findViewById(R.id.petBreed);
        petGenderTextView = view.findViewById(R.id.petGender);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        editPetNameButton = view.findViewById(R.id.editPetNameButton);
        editPetWeightButton = view.findViewById(R.id.editPetWeightButton);
        editPetAgeButton = view.findViewById(R.id.editPetAgeButton);
        editPetBreedButton = view.findViewById(R.id.editPetBreedButton);
        editPetGenderButton = view.findViewById(R.id.editPetGenderButton);
        profileImageView = view.findViewById(R.id.imageView3);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch user ID (this could be from a logged-in user session)
        userId = getUserIdFromPreferences();

        // Load pet data from Firestore
        loadPetData();

        // Set up edit button listeners
        setEditButtonListeners();

        return view;
    }

    private void loadPetData() {
        db.collection("pets")
                .whereEqualTo("userId", userId) // Filter pets by userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            petId = document.getId(); // Capture pet ID for updates
                            Pet pet = new Pet(
                                    document.getString("name"),
                                    document.getLong("age").intValue(),
                                    document.getString("breed"),
                                    document.getString("gender"),
                                    userId, // Include the userId for reference
                                    document.getLong("weight").intValue()

                            );
                            displayPetData(pet);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load pet data: " + (task.getException() != null ? task.getException().getMessage() : "No results found"), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayPetData(Pet pet) {
        // Update the UI with pet data
        usernameIdView.setText(String.valueOf(getUserIdFromPreferences()));
        petNameTextView.setText(pet.getName());
        petWeightTextView.setText(String.valueOf(pet.getWeight()));
        petAgeTextView.setText(String.valueOf(pet.getAge()));
        petBreedTextView.setText(pet.getBreed());
        petGenderTextView.setText(pet.getGender());
    }

    private int getUserIdFromPreferences() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", getActivity().MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }

    private void setEditButtonListeners() {
        editPetNameButton.setOnClickListener(v -> showEditDialog("Edit Pet Name", petNameTextView.getText().toString(), "name"));
        editPetWeightButton.setOnClickListener(v -> showEditDialog("Edit Pet Weight", String.valueOf(petWeightTextView.getText()), "weight"));
        editPetAgeButton.setOnClickListener(v -> showEditDialog("Edit Pet Age", String.valueOf(petAgeTextView.getText()), "age"));
        editPetBreedButton.setOnClickListener(v -> showEditDialog("Edit Pet Breed", petBreedTextView.getText().toString(), "breed"));
        editPetGenderButton.setOnClickListener(v -> showEditDialog("Edit Pet Gender", petGenderTextView.getText().toString(), "gender"));
    }

    private void showEditDialog(String title, String currentValue, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setText(currentValue);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            updatePetData(field, newValue);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updatePetData(String field, String newValue) {
        db.collection("pets").document(petId)
                .update(field, newValue)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                    loadPetData(); // Refresh data after update
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
