package com.example.tsuki;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Helper untuk mengambil lokasi user (kota, negara) menggunakan
 * FusedLocationProviderClient + Geocoder.
 *
 * Cara pakai:
 *   LocationHelper.getLocation(context, (city, country) -> {
 *       // pakai city dan country di sini
 *   }, errorMessage -> {
 *       // tangani error
 *   });
 */
public class LocationHelper {

    public interface OnLocationResult {
        void onResult(String city, String country);
    }

    public interface OnLocationError {
        void onError(String message);
    }

    /**
     * Ambil lokasi sekali (one-shot) lalu reverse geocode ke nama kota & negara.
     * Callback dipanggil di main thread.
     */
    public static void getLocation(Context context,
                                   OnLocationResult onResult,
                                   OnLocationError onError) {

        // Cek permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            notifyError(onError, "Location permission not granted");
            return;
        }

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(context);

        // Coba ambil last known location dulu (cepat, hemat baterai)
        client.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        reverseGeocode(context, location, onResult, onError);
                    } else {
                        // Fallback: minta satu update lokasi baru
                        requestFreshLocation(context, client, onResult, onError);
                    }
                })
                .addOnFailureListener(e ->
                        notifyError(onError, "Failed to get location: " + e.getMessage()));
    }

    // ─── Minta update lokasi baru (satu kali) ────────────────────────────────

    private static void requestFreshLocation(Context context,
                                             FusedLocationProviderClient client,
                                             OnLocationResult onResult,
                                             OnLocationError onError) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            notifyError(onError, "Location permission not granted");
            return;
        }

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000)
                .setMaxUpdates(1)
                .build();

        client.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                client.removeLocationUpdates(this);
                Location location = result.getLastLocation();
                if (location != null) {
                    reverseGeocode(context, location, onResult, onError);
                } else {
                    notifyError(onError, "Could not determine location");
                }
            }
        }, Looper.getMainLooper());
    }

    // ─── Reverse geocode koordinat → nama kota & negara ──────────────────────

    private static void reverseGeocode(Context context,
                                       Location location,
                                       OnLocationResult onResult,
                                       OnLocationError onError) {
        if (!Geocoder.isPresent()) {
            // Geocoder tidak tersedia — simpan koordinat saja
            String coords = String.format(Locale.getDefault(),
                    "%.4f, %.4f", location.getLatitude(), location.getLongitude());
            if (onResult != null) onResult.onResult(coords, "");
            return;
        }

        // Jalankan geocoding di background thread agar tidak block UI
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);

                android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Ambil nama kota dengan fallback bertingkat
                    String city = address.getLocality();
                    if (city == null || city.isEmpty()) city = address.getSubAdminArea();
                    if (city == null || city.isEmpty()) city = address.getAdminArea();

                    String country = address.getCountryName();

                    final String finalCity    = city    != null ? city    : "";
                    final String finalCountry = country != null ? country : "";

                    mainHandler.post(() -> {
                        if (onResult != null) onResult.onResult(finalCity, finalCountry);
                    });
                } else {
                    mainHandler.post(() ->
                            notifyError(onError, "Could not determine city from location"));
                }
            } catch (IOException e) {
                android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());
                mainHandler.post(() ->
                        notifyError(onError, "Geocoder error: " + e.getMessage()));
            }
        }).start();
    }

    // ─── Util ─────────────────────────────────────────────────────────────────

    private static void notifyError(OnLocationError onError, String message) {
        if (onError != null) onError.onError(message);
    }
}
