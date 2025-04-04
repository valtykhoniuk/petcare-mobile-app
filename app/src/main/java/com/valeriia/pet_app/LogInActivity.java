package com.valeriia.pet_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.valeriia.pet_app.model.User; // Ensure this import points to your User model

public class LogInActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                checkUserExists(username, password);
            } else {
                Toast.makeText(LogInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to the register screen
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserExists(String username, String password) {
        firestore.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // User exists, log in and proceed to the main screen
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isRegistered", true);
                            editor.putString("username", username);
                            editor.putInt("userId", document.getLong("userId").intValue());
                            editor.apply();

                            // Navigate to next activity (e.g., main activity)
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // User does not exist or wrong credentials
                        Toast.makeText(LogInActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(LogInActivity.this, "Failed to log in: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
