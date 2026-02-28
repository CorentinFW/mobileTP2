package com.example.detecteur;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Classe interne représentant un capteur à vérifier
    private static class SensorInfo {
        String name;
        boolean isAvailable;
        String[] affectedFeatures;
        String statusAvailable = "Disponible";
        String statusUnavailable = "Indisponible";

        SensorInfo(String name, boolean isAvailable, String[] affectedFeatures) {
            this.name = name;
            this.isAvailable = isAvailable;
            this.affectedFeatures = affectedFeatures;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        PackageManager pm = getPackageManager();
        LinearLayout llSensors = findViewById(R.id.llSensors);
        TextView tvGlobalMessage = findViewById(R.id.tvGlobalMessage);

        // Vérification de chaque capteur
        List<SensorInfo> sensors = buildSensorList(sensorManager, pm);

        boolean anyUnavailable = false;
        LayoutInflater inflater = LayoutInflater.from(this);

        for (SensorInfo info : sensors) {
            View item = inflater.inflate(R.layout.item_sensor, llSensors, false);

            ImageView ivStatus = item.findViewById(R.id.ivStatus);
            TextView tvName = item.findViewById(R.id.tvSensorName);
            TextView tvStatus = item.findViewById(R.id.tvSensorStatus);
            TextView tvAffected = item.findViewById(R.id.tvAffectedFeatures);

            tvName.setText(info.name);

            if (info.isAvailable) {
                tvStatus.setText(info.statusAvailable);
                tvStatus.setTextColor(0xFF388E3C); // vert
                ivStatus.setImageResource(android.R.drawable.presence_online);
                ivStatus.setColorFilter(0xFF388E3C);
                tvAffected.setVisibility(View.GONE);
            } else {
                tvStatus.setText(info.statusUnavailable);
                tvStatus.setTextColor(0xFFB00020); // rouge
                ivStatus.setImageResource(android.R.drawable.presence_busy);
                ivStatus.setColorFilter(0xFFB00020);

                // Affichage des fonctionnalités affectées
                StringBuilder sb = new StringBuilder("⚠ Fonctionnalités indisponibles :\n");
                for (String feat : info.affectedFeatures) {
                    sb.append("  • ").append(feat).append("\n");
                }
                tvAffected.setText(sb.toString().trim());
                tvAffected.setVisibility(View.VISIBLE);
                anyUnavailable = true;
            }

            llSensors.addView(item);
        }

        // Message global en bas
        if (anyUnavailable) {
            tvGlobalMessage.setText(
                "⚠ Certaines fonctionnalités sont limitées en raison de capteurs manquants sur cet appareil."
            );
            tvGlobalMessage.setVisibility(View.VISIBLE);
        } else {
            tvGlobalMessage.setText("✔ Tous les capteurs sont disponibles sur cet appareil.");
            tvGlobalMessage.setTextColor(0xFF388E3C);
            tvGlobalMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Construit la liste des capteurs à vérifier avec leurs fonctionnalités associées.
     */
    private List<SensorInfo> buildSensorList(SensorManager sm, PackageManager pm) {
        List<SensorInfo> list = new ArrayList<>();

        // Accéléromètre
        list.add(new SensorInfo(
            "Accéléromètre",
            sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null,
            new String[]{
                "Détection de mouvement et secousse",
                "Rotation de l'écran automatique",
                "Comptage de pas (podomètre)"
            }
        ));

        // Gyroscope
        list.add(new SensorInfo(
            "Gyroscope",
            sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null,
            new String[]{
                "Réalité augmentée (AR)",
                "Navigation et boussole précise",
                "Jeux de mouvement"
            }
        ));

        // Magnétomètre (boussole)
        list.add(new SensorInfo(
            "Magnétomètre (Boussole)",
            sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null,
            new String[]{
                "Application boussole",
                "Orientation géographique",
                "Navigation intérieure"
            }
        ));

        // Capteur de proximité
        list.add(new SensorInfo(
            "Capteur de proximité",
            sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null,
            new String[]{
                "Extinction de l'écran lors d'un appel",
                "Gestes mains-libres sans contact"
            }
        ));

        // Capteur de lumière ambiante
        list.add(new SensorInfo(
            "Capteur de lumière ambiante",
            sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null,
            new String[]{
                "Ajustement automatique de la luminosité",
                "Mode nuit automatique"
            }
        ));

        // Baromètre
        list.add(new SensorInfo(
            "Baromètre (Pression)",
            sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null,
            new String[]{
                "Mesure de l'altitude",
                "Prévisions météo locales",
                "Navigation GPS améliorée"
            }
        ));

        // Thermomètre
        list.add(new SensorInfo(
            "Thermomètre ambiant",
            sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null,
            new String[]{
                "Mesure de la température ambiante",
                "Alertes de chaleur/froid"
            }
        ));

        // GPS
        list.add(new SensorInfo(
            "GPS (Localisation)",
            pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS),
            new String[]{
                "Navigation et cartographie",
                "Géolocalisation des photos",
                "Applications de fitness en extérieur",
                "Alertes de zone géographique"
            }
        ));

        // Bluetooth
        list.add(new SensorInfo(
            "Bluetooth",
            pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH),
            new String[]{
                "Connexion à des appareils sans fil (casque, clavier…)",
                "Transfert de fichiers Bluetooth",
                "Localisation intérieure BLE"
            }
        ));

        // NFC
        list.add(new SensorInfo(
            "NFC",
            pm.hasSystemFeature(PackageManager.FEATURE_NFC),
            new String[]{
                "Paiement sans contact",
                "Lecture de tags NFC",
                "Partage de contenu par contact"
            }
        ));

        // Caméra
        list.add(new SensorInfo(
            "Caméra",
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY),
            new String[]{
                "Photographie et vidéo",
                "Scanner de QR Code / code-barres",
                "Réalité augmentée",
                "Visioconférence"
            }
        ));

        return list;
    }
}