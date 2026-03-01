package com.example.accelerometre;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Seuils de classification des valeurs de l'accéléromètre (en m/s²)
    // La gravité terrestre vaut environ 9.81 m/s²
    private static final float SEUIL_BAS   = 5.0f;   // en dessous : vert
    private static final float SEUIL_HAUT  = 15.0f;  // au-dessus  : rouge  |  entre : noir

    private SensorManager sensorManager;
    private Sensor accelerometre;

    private ConstraintLayout mainLayout;
    private TextView tvAccelValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mainLayout    = findViewById(R.id.main);
        tvAccelValues = findViewById(R.id.tvAccelValues);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation du SensorManager et de l'accéléromètre
        sensorManager  = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometre  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometre == null) {
            tvAccelValues.setText(R.string.accelerometre_non_disponible);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometre != null) {
            sensorManager.registerListener(this, accelerometre, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Magnitude du vecteur accélération (sans gravité constante)
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

            // Choisir la couleur selon les seuils
            int couleur;
            String categorie;
            if (magnitude < SEUIL_BAS) {
                couleur    = Color.GREEN;   // valeurs inférieures → vert
                categorie  = "Faible (Vert)";
            } else if (magnitude <= SEUIL_HAUT) {
                couleur    = Color.BLACK;   // valeurs moyennes → noir
                categorie  = "Moyen (Noir)";
            } else {
                couleur    = Color.RED;     // valeurs supérieures → rouge
                categorie  = "Élevée (Rouge)";
            }

            mainLayout.setBackgroundColor(couleur);

            String affichage = String.format(Locale.getDefault(),
                    "X: %.2f\nY: %.2f\nZ: %.2f\n\nMagnitude: %.2f\nCatégorie: %s",
                    x, y, z, magnitude, categorie);
            tvAccelValues.setText(affichage);

            // Adapter la couleur du texte pour la lisibilité
            tvAccelValues.setTextColor(
                    (couleur == Color.BLACK) ? Color.WHITE : Color.BLACK);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilisé
    }
}