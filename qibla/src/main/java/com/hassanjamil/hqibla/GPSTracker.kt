package com.hassanjamil.hqibla

import android.location.LocationListener
import android.location.LocationManager
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import java.lang.Exception

class GPSTracker(private val mContext: Context) : Service(), LocationListener {
    // flag for GPS status
    private var isGPSEnabled = false

    // flag for network status
    private var isNetworkEnabled = false

    // flag for GPS status
    private var canGetLocation = false
    private var location : Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    // Declaring a Location Manager
    private var locationManager: LocationManager? = null
    @SuppressLint("MissingPermission")
    fun getLocation(): Location {
        try {
            locationManager = mContext
                .getSystemService(LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
            // getting network status
            isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                if (isNetworkEnabled) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    location =
                        locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    latitude = location?.latitude!!
                    longitude = location?.longitude!!
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location =
                                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                            latitude = location?.latitude!!
                            longitude = location?.longitude!!
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location!!
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    @SuppressLint("MissingPermission")
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)
        // Setting Dialog Title
        alertDialog.setTitle(mContext.resources.getString(R.string.gps_settings_title))
        // Setting Dialog Message
        alertDialog.setMessage(mContext.resources.getString(R.string.gps_settings_text))
        // On pressing Settings button
        alertDialog.setPositiveButton(mContext.resources.getString(R.string.settings_button_ok)) { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        }
        // on pressing cancel button
        alertDialog.setNegativeButton(mContext.resources.getString(R.string.settings_button_cancel)) { dialog, _ -> dialog.cancel() }
        // Showing Alert Message
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 100 // in meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 // in secs
                ).toLong()
    }

    init {
        //location = getLocation()
    }
}