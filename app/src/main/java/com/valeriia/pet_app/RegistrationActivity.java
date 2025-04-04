package com.valeriia.pet_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.valeriia.pet_app.model.User;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;  // Firebase Authentication instance
    private FirebaseFirestore firestore;  // Firestore instance
    private FirebaseUser currentUser;

    private EditText usernameInput, emailInput, passwordInput;
    private Button sendEmailButton, nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // UI elements
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        sendEmailButton = findViewById(R.id.sendEmailButton);
        nextButton = findViewById(R.id.nextButton);

        // Send verification email button
        sendEmailButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                createUserAndSendVerification(username, email, password);
            } else {
                Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Next button
        nextButton.setOnClickListener(v -> {
            if (currentUser != null) {
                currentUser.reload(); // Refresh user data
                if (currentUser.isEmailVerified()) {
                    saveUserToFirestore();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Email is not verified. Please verify and try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegistrationActivity.this, "Please send verification email first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserAndSendVerification(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        assert currentUser != null;

                        // Send email verification
                        currentUser.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore() {
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (currentUser != null) {
            User newUser = new User();
            newUser.setUserId(currentUser.getUid().hashCode()); // Use Firebase UID as unique identifier
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            firestore.collection("users").document(currentUser.getUid())
                    .set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", newUser.getUserId());
                        editor.apply();
                        Toast.makeText(RegistrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                        // Proceed to next activity
                        Intent intent = new Intent(RegistrationActivity.this, RegisterPetActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegistrationActivity.this, "Failed to save user to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
