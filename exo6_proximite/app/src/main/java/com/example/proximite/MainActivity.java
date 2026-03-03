package com.example.proximite;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;

    private ImageView imageProximite;
    private TextView textProximite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageProximite = findViewById(R.id.imageProximite);
        textProximite  = findViewById(R.id.textProximite);

        sensorManager  = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            textProximite.setText("Capteur de proximité non disponible");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            float maxRange = proximitySensor.getMaximumRange();
            // Sur la plupart des téléphones : 0 = proche, maxRange = loin
            // On gère aussi le cas où maxRange == 0 (capteur binaire pur)
            boolean isClose = (maxRange <= 0) ? (distance == 0) : (distance < maxRange);

            if (isClose) {
                imageProximite.setImageResource(R.drawable.ic_close);
                textProximite.setText(R.string.status_close);
                textProximite.setTextColor(getColor(android.R.color.holo_red_dark));
            } else {
                imageProximite.setImageResource(R.drawable.ic_far);
                textProximite.setText(R.string.status_far);
                textProximite.setTextColor(getColor(android.R.color.holo_green_dark));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}