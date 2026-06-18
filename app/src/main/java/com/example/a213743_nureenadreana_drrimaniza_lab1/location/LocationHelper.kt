package com.example.a213743_nureenadreana_drrimaniza_lab1.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onResult: (String) -> Unit) {
        // Gunakan getCurrentLocation (Fresh) bukannya lastLocation (Cached)
        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, // Minta ketepatan tinggi (GPS)
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    // Tambah try-catch untuk handle internet/geocoder error
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Bangi, Selangor"
                    onResult(address)
                } catch (e: Exception) {
                    onResult("Lat: ${location.latitude}, Lon: ${location.longitude}")
                }
            } else {
                onResult("Location not found. Please wait and try again.")
            }
        }.addOnFailureListener {
            onResult("Error getting location: ${it.message}")
        }
    }


    private fun getAddress(lat: Double, lng: Double, onResult: (String) -> Unit) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown address at $lat, $lng"
            onResult(address)
        } catch (e: Exception) {
            onResult("Coordinates found ($lat, $lng), but failed to get address: ${e.localizedMessage}")
        }
    }
}