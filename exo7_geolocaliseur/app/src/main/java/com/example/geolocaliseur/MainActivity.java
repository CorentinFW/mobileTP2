package com.example.geolocaliseur;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvPrecision;
    private TextView tvStatus;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (Boolean.TRUE.equals(fineGranted) || Boolean.TRUE.equals(coarseGranted)) {
                    startLocationUpdates();
                } else {
                    tvStatus.setText("Permission refusée. Impossible d'obtenir la position.");
                    Toast.makeText(this, "Permission de localisation refusée.", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvLatitude  = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvPrecision = findViewById(R.id.tvPrecision);
        tvStatus    = findViewById(R.id.tvStatus);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    updateUI(location);
                }
            }
        };

        btnRefresh.setOnClickListener(v -> requestLocationPermissionOrStart());
        requestLocationPermissionOrStart();
    }

    private void requestLocationPermissionOrStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tvStatus.setText("Récupération de la position en cours...");

        // Obtenir la dernière position connue immédiatement
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateUI(location);
            }
        });

        // Demander des mises à jour régulières
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        fusedLocationClient.requestLocationUpdates(request, locationCallback, getMainLooper());
    }

    private void updateUI(Location location) {
        tvLatitude.setText(String.format(Locale.getDefault(),
                "Latitude : %.6f °", location.getLatitude()));
        tvLongitude.setText(String.format(Locale.getDefault(),
                "Longitude : %.6f °", location.getLongitude()));
        tvPrecision.setText(String.format(Locale.getDefault(),
                "Précision : ± %.1f m", location.getAccuracy()));
        tvStatus.setText("Position mise à jour ✓");
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}

