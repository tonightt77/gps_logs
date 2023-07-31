package com.yijingcode.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import com.yijingcode.gps.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        double initialLatitude = 37.7749; // Initial latitude value
        double initialLongitude = -122.4194; // Initial longitude value
        currentLocation = new Location("");
        currentLocation.setLatitude(initialLatitude);
        currentLocation.setLongitude(initialLongitude);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        System.out.println("Initial location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
        simulateMovement();

        // add a clickable button to stop the app
        findViewById(R.id.buttonStop).setOnClickListener(v -> {
            finish();
            System.exit(0);
        });
    }

    private void simulateMovement() {
        // Launch a new thread for simulating movement
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);  // Wait for 10 seconds

                    // Move to a random direction that is 2km away
                    double randomAngle = Math.random() * 2 * Math.PI;
                    double deltaLatitude = 0.018 * Math.sin(randomAngle);  // Roughly equivalent to 2km
                    double deltaLongitude = 0.018 * Math.cos(randomAngle);  // Roughly equivalent to 2km

                    if (currentLocation != null) {
                        currentLocation.setLatitude(currentLocation.getLatitude() + deltaLatitude);
                        currentLocation.setLongitude(currentLocation.getLongitude() + deltaLongitude);

                        String fileContents = "New location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput("gps_data.txt", Context.MODE_APPEND);
                            outputStream.write((fileContents + "\n").getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(() -> {
                            TextView textView = findViewById(R.id.textView1);
                            textView.setText(fileContents);
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
