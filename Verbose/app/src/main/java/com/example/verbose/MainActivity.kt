package com.example.verbose

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates
import android.location.LocationListener
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private lateinit var wifiManager: WifiManager

    private var pos: Location = Location("dummyprovider");

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var locationListener: LocationListener


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mHandler = Handler()

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                pos = location
                Log.v("Info", "Latitude:" + pos.latitude + "\nLongitude:" + pos.longitude)
            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        var rssi = 0

        sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("Info","IF")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);

        mRunnable = Runnable {
            rssi = wifiManager.connectionInfo.rssi

            Log.v("Info wifi", ""+rssi+" dBm")
            var posText = ""

            pos?.let {
                posText = "Latitude:" + pos.latitude + "\nLongitude:" + pos.longitude
            }

            leitura.text = "RSSI: $rssi dBm\n" + posText
            mHandler.postDelayed(mRunnable, 1000)
        }

        mHandler.postDelayed(mRunnable, 1000)

    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunnable)

        // Change the text view text
        Log.v("Info wifi","Handler call backs removed.")
    }

    override fun onResume() {
        super.onResume()

        mHandler.postDelayed(mRunnable, 1000)
    }


}

