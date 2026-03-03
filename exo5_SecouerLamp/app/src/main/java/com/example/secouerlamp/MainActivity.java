package com.example.secouerlamp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // ── Seuil de secousse (m/s²) ──────────────────────────────────────────────
    private static final float SHAKE_THRESHOLD = 15.0f;
    // Délai minimum entre deux détections (ms) pour éviter les faux positifs
    private static final long SHAKE_COOLDOWN_MS = 1000;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private CameraManager cameraManager;
    private String cameraId;

    private boolean torchOn = false;
    private long lastShakeTime = 0;

    private TextView statusText;
    private ImageView torchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        statusText = findViewById(R.id.statusText);
        torchIcon  = findViewById(R.id.torchIcon);

        // ── SensorManager ──────────────────────────────────────────────────────
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer == null) {
                Toast.makeText(this, "Accéléromètre non disponible !", Toast.LENGTH_LONG).show();
            }
        }

        // ── CameraManager (lampe torche) ───────────────────────────────────────
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException | ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "Caméra non disponible !", Toast.LENGTH_LONG).show();
        }
    }

    // ── Cycle de vie : enregistrement / désenregistrement du capteur ───────────

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // On éteint la torche si l'appli passe en arrière-plan
        if (torchOn) {
            setTorch(false);
        }
    }

    // ── SensorEventListener ────────────────────────────────────────────────────

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Magnitude de l'accélération (sans la gravité, on prend la norme brute)
        double magnitude = Math.sqrt(x * x + y * y + z * z);

        // On soustrait g (≈ 9.81 m/s²) pour obtenir l'accélération linéaire nette
        float linearAcceleration = (float) Math.abs(magnitude - SensorManager.GRAVITY_EARTH);

        if (linearAcceleration > SHAKE_THRESHOLD) {
            long now = System.currentTimeMillis();
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now;
                toggleTorch();
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }

    // ── Lampe torche ───────────────────────────────────────────────────────────

    private void toggleTorch() {
        setTorch(!torchOn);
    }

    private void setTorch(boolean enable) {
        if (cameraId == null) return;
        try {
            cameraManager.setTorchMode(cameraId, enable);
            torchOn = enable;
            updateUI();
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Impossible d'accéder à la lampe torche.", Toast.LENGTH_SHORT).show();
        }
    }

    // ── Mise à jour de l'interface ─────────────────────────────────────────────

    private void updateUI() {
        if (torchOn) {
            statusText.setText("💡 Lampe torche : ALLUMÉE");
            torchIcon.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            statusText.setText("🔦 Lampe torche : ÉTEINTE");
            torchIcon.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
}