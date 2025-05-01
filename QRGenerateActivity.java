package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONObject;

import java.util.Hashtable;

public class QRGenerateActivity extends AppCompatActivity {

    private EditText sessionIdInput;
    private Button generateQRCodeButton;
    private ImageView qrImageView;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrgenerate);

        sessionIdInput = findViewById(R.id.sessionIdInput);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);
        qrImageView = findViewById(R.id.qrImageView);

        generateQRCodeButton.setOnClickListener(view -> {
            sessionId = sessionIdInput.getText().toString().trim();

            if (sessionId.isEmpty()) {
                Toast.makeText(this, "Session ID cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                generateQRCode(sessionId);
            }
        });
    }

    private void generateQRCode(String sessionId) {
        try {
            long timestamp = System.currentTimeMillis();
            JSONObject qrData = new JSONObject();
            qrData.put("sessionId", sessionId);
            qrData.put("timestamp", timestamp);
            String dataToEncode = qrData.toString();

            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(dataToEncode, BarcodeFormat.QR_CODE, 500, 500, hints);
            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);

            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }
    }
}