package com.example.proiecteim

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var locationAdapter: LocationAdapter
    private var mutableLocationList: ArrayList<Location> = ArrayList()
    private var snackBar: Snackbar? = null
    private var hasInternetConnection = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        locationAdapter = LocationAdapter(mutableLocationList)
        locationList.adapter = locationAdapter
        locationList.layoutManager = LinearLayoutManager(this)
        lateinit var runnable: Runnable
        runnable = Runnable() {
            Log.d("postDelayed", "RUNNABLE RUNNABLE RUNNABLE RUNNABLE")
            if (hasInternetConnection) {
                for ((position, location) in locationAdapter.getLocations().withIndex()) {
                    val newLocation = location.name?.let { getLocation(it) }
                    newLocation!!.alertTemp = location.alertTemp
                    locationAdapter.setLocation(position, newLocation)
                }
            }
            locationList.postDelayed(runnable, 15000)
        }
        locationList.postDelayed(runnable, 15000)

        searchButton.setOnClickListener {
            val locationName = citySearchBox.text.toString()
            if (locationName.isNotEmpty()) {
                val location = getLocation(locationName)
                if (location != null) {
                    locationAdapter.addLocation(location)
                    citySearchBox.text.clear()
                }
            }
        }

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            hasInternetConnection = false
            snackBar = Snackbar.make(window.decorView.rootView, "No internet connection!", Snackbar.LENGTH_LONG)
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        } else {
            hasInternetConnection = true
            snackBar?.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("MainActivity", "Saved instance")

        outState.putParcelableArrayList("MutableLocationsList", ArrayList(mutableLocationList))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mutableLocationList = savedInstanceState.getParcelableArrayList<Location>("MutableLocationsList")!!

        Log.d("MainActivity", " ~~~~~~ON RESTORE INSTANCE~~~~~~~")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val location = data?.extras?.getParcelable<Location>("Location")
        val idx = data?.extras?.getInt("LocationIdx")
        if (resultCode == RESULT_OK) {
            Log.d("onActivityResult", idx.toString())
            Log.d("onActivityResult", location.toString())
            Log.d("onActivityResult", location?.alertTemp.toString())
            if (location != null && idx != null) {
                locationAdapter.setLocation(idx, location)
            }
        }
    }

    private fun getLocation(cityName: String): Location? {
        if (hasInternetConnection) {
            var weatherData: JSONObject? = null
            runBlocking {
                launch(IO) {
                    try {
                        weatherData = getWeather(cityName)
                    } catch (e: Exception) {
                        Log.d("Exception", e.toString())
                    }
                }
            }

            if (weatherData != null) {
                val currTemp = weatherData!!.getString("temp")
                val minTemp = weatherData!!.getString("temp_min")
                val maxTemp = weatherData!!.getString("temp_max")
                val pressure = weatherData!!.getString("pressure")
                val humidity = weatherData!!.getString("humidity")

                return Location(
                    cityName,
                    currTemp.toFloat(),
                    maxTemp.toFloat(),
                    minTemp.toFloat(),
                    pressure.toInt(),
                    humidity.toInt(),
                    null
                )
            }
        }
        return null
    }

    private fun getWeather(cityName: String): JSONObject {
        val url: String =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=361d8bf5e3a482bf972852106c1d0698&units=metric"
        val resultJSON = URL(url).readText()
        Log.d("Weather Result", resultJSON)
        val jsonObj = JSONObject(resultJSON)
        return jsonObj.getJSONObject("main")
    }
}