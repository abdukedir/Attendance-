package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SigningUpActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signUpButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signing_up);
        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        signUpButton = findViewById(R.id.signupBtn);

        // Sign-up button listener
        signUpButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SigningUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                createUserDocument(user); // Create user document after sign up
                                Toast.makeText(SigningUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SigningUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Create a Firestore document for the user
    private void createUserDocument(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = user.getUid();
        String email = user.getEmail();

        // Prepare the user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", "John Doe"); // Replace with actual name input if you have one
        userData.put("email", email);
        userData.put("role", "student"); // You can assign the role as "student" or "admin"

        // Save the user data in Firestore under 'users' collection
        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "User profile created."))
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error creating user", e));
    }
}
