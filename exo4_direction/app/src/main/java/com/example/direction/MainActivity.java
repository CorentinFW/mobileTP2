package com.example.direction;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView directionText;
    private static final float THRESHOLD = 5.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialiser le TextView
        directionText = findViewById(R.id.directionText);

        // Initialiser le gestionnaire de capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null && sensorManager != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // Mouvements gauche/droite
            float y = event.values[1]; // Mouvements haut/bas
            float z = event.values[2]; // Profondeur

            String direction = "En attente...";

            // Déterminer la direction basée sur les valeurs d'accélération
            if (Math.abs(x) > THRESHOLD) {
                if (x > 0) {
                    direction = "➡️ DROITE";
                } else {
                    direction = "⬅️ GAUCHE";
                }
            } else if (Math.abs(y) > THRESHOLD) {
                if (y > 0) {
                    direction = "⬇️ BAS";
                } else {
                    direction = "⬆️ HAUT";
                }
            }

            // Mettre à jour le TextView en temps réel
            directionText.setText(direction);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé pour cette application
    }
}