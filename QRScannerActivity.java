package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRScannerActivity extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final long QR_CODE_EXPIRY_DURATION = 2 * 60 * 1000; // 2 minutes

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrscanner);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the QR code");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                processScannedData(result.getContents());
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processScannedData(String scannedData) {
        try {
            JSONObject qrData = new JSONObject(scannedData);
            String sessionId = qrData.getString("sessionId");
            long timestamp = qrData.getLong("timestamp");

            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > QR_CODE_EXPIRY_DURATION) {
                Toast.makeText(this, "QR Code has expired", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            String userId = currentUser.getUid();
            DocumentReference attendanceRef = db.collection("attendance_logs")
                    .document(sessionId)
                    .collection("attendees")
                    .document(userId);

            attendanceRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(this, "Attendance already marked", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Map<String, Object> attendanceData = new HashMap<>();
                        attendanceData.put("userId", userId);
                        attendanceData.put("timestamp", currentTime);

                        attendanceRef.set(attendanceData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to mark attendance", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    }
                } else {
                    Toast.makeText(this, "Error checking attendance", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
