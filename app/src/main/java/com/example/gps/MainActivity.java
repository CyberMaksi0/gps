package com.example.gps;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView longitudeTextView, latitudeTextView;
    private Button showLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudeTextView = findViewById(R.id.longitude);
        latitudeTextView = findViewById(R.id.latitude);
        showLocationButton = findViewById(R.id.button);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        showLocationButton.setOnClickListener(this::requestLocation);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocation(View view) {
        if (hasLocationPermission()) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, this::handleLocation);
        } catch (SecurityException e) {
            handleSecurityException(e);
        }
    }

    private void handleLocation(Location location) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                latitudeTextView.setText(getString(R.string.latitude, String.valueOf(addresses.get(0).getLatitude())));
                longitudeTextView.setText(getString(R.string.longitude, String.valueOf(addresses.get(0).getLongitude())));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                handlePermissionDenied();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handlePermissionDenied() {
        Toast.makeText(this, "Wymagane są uprawnienia do lokalizacji.", Toast.LENGTH_SHORT).show();
    }

    private void handleSecurityException(SecurityException e) {
        Toast.makeText(this, "Brak uprawnień do lokalizacji.", Toast.LENGTH_SHORT).show();
    }
}
