package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashBoardAdmin extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView attendanceListView;
    private Button exportCsvButton;
    private List<String> attendanceList;
    private ArrayAdapter<String> adapter;
    private String sessionId = "session123"; // Example session ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board_admin);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Bind the ListView and Button to their respective views in the XML layout
        attendanceListView = findViewById(R.id.attendanceListView);

        exportCsvButton = findViewById(R.id.exportCsvButton);

        // Initialize the list and adapter for displaying attendance data
        attendanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendanceList);
        attendanceListView.setAdapter(adapter);

        // Load attendance data from Firestore
        loadAttendanceData();

        // Set up the export CSV button
        exportCsvButton.setOnClickListener(view -> exportToCsv());
    }
    private void loadAttendanceData() {
        // Retrieve attendance logs from Firestore
        db.collection("attendance_logs")
                .document(sessionId)
                .collection("students")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear the previous data and add new data
                    attendanceList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String userId = doc.getId();
                        long timestamp = doc.getLong("timestamp");
                        attendanceList.add("User ID: " + userId + ", Time: " + new Date(timestamp).toString());
                    }
                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Show error message if loading data fails
                    Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }
    private void exportToCsv() {
        try {
            // Create a CSV file and write the attendance data
            FileWriter writer = new FileWriter(getExternalFilesDir(null) + "/attendance.csv");
            writer.append("User ID,Timestamp\n");
            for (String record : attendanceList) {
                // Format the record and write to the file
                writer.append(record.replace("User ID: ", "").replace(", Time: ", ",")).append("\n");
            }
            writer.flush();
            writer.close();
            // Show success message after exporting CSV
            Toast.makeText(this, "CSV exported successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle any exceptions and show an error message
            e.printStackTrace();
            Toast.makeText(this, "Failed to export CSV", Toast.LENGTH_SHORT).show();
        }
    }
}