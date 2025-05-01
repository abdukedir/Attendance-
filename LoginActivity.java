package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signupButton;
    private FirebaseAuth mAuth; // Declare FirebaseAuth variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        signupButton = findViewById(R.id.signupBtn);

        // Set the sign-up button click listener
        signupButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase SignUp
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser(); // Get the current user
                            if (user != null) {
                                // Create user document after successful sign-up
                                createUserDocument(user);
                                Toast.makeText(LoginActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Error: User not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Public method to create a user document in Firestore
    public void createUserDocument(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get user details
        String uid = user.getUid();
        String email = user.getEmail();

        // Prepare user data to be saved
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", "John Doe"); // Default name, replace with input if needed
        userData.put("email", email);
        userData.put("role", "student"); // Or "admin" if needed

        // Store user data in Firestore
        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "User profile created."))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error creating user", e));
    }
}
