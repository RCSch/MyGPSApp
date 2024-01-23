package com.example.mygpsapp;

import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient flpc;
    private TextView txt_Lat, txt_Lon, txt_Bea, txt_Acc, txt_Spd, txt_Alt;

    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flpc = LocationServices.getFusedLocationProviderClient(this);
        txt_Lat = findViewById(R.id.tv_latitude);
        txt_Lon = findViewById(R.id.tv_longitude);
        txt_Alt = findViewById(R.id.tv_altitude);
        txt_Acc = findViewById(R.id.tv_accuracy);
        txt_Bea = findViewById(R.id.tv_bearing);
        txt_Spd = findViewById(R.id.tv_speed);


        getUpdates();
        updateBtn = findViewById(R.id.btn_update);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUpdates();
            }
        });
    }



    private void permissions() {
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted =
                            result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted =
                            result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if (fineLocationGranted != null && fineLocationGranted) {
                        // Precise location access granted.
                        getUpdates();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Only approximate location access granted.
                    } else {
                        // No location access granted.
                        Toast.makeText(this, "F... U. Det er altså en GPS app, dummy.",
                                Toast.LENGTH_LONG).show();
                        this.finish();
                    }
                });

// ...

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    private void getUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions();
            return;
        }

        flpc.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                txt_Lat.setText(String.valueOf(location.getLatitude()));
                txt_Lon.setText(String.valueOf(location.getLongitude()));
                txt_Alt.setText(String.valueOf((location.getAltitude())));
                txt_Acc.setText(String.valueOf(location.getAccuracy()));
                txt_Bea.setText(String.valueOf((location.getBearing())));
                txt_Spd.setText((String.valueOf(location.getSpeed())));
            }

        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            permissions();
            return;
        }

        flpc.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                txt_Lat.setText(String.valueOf(location.getLatitude()));
                txt_Lon.setText(String.valueOf(location.getLongitude()));
                txt_Alt.setText(String.valueOf((location.getAltitude())));
                txt_Bea.setText(String.valueOf((location.getBearing())));
                txt_Spd.setText((String.valueOf(location.getSpeed())));
                if (location.hasAccuracy())
                    txt_Acc.setText(String.valueOf(location.getAccuracy()));
            }

        });
    }
}