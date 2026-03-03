package com.example.listecapteur;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // recupere sous forme d'objet tous les capteurs du téléphone
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // sous forme de liste en se basant sur l'objet
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // La liste a afficher sur le listView
        ArrayList<String> sensorNames = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            sensorNames.add(sensor.getName() + "\nType : " + sensor.getStringType());
        }

        // on met sur le listView
        ListView lv = findViewById(R.id.listViewSensors);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                sensorNames
        );
        lv.setAdapter(adapter);


    }
}