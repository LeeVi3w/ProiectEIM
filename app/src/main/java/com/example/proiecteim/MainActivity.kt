package com.example.proiecteim

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private val DB_INSTANCE_URL = "https://proiecteim-b2735-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var databaseReference: DatabaseReference
    private var mutableLocationList: ArrayList<Location> = ArrayList()
    private var snackBar: Snackbar? = null
    private var hasInternetConnection = true
    lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        databaseReference = FirebaseDatabase.getInstance(DB_INSTANCE_URL).getReference("Locations")
        locationAdapter = LocationAdapter(mutableLocationList)
        loadData()

        startService()

        locationList.adapter = locationAdapter
        locationList.layoutManager = LinearLayoutManager(this)
        runnable = Runnable {
//            Log.d("postDelayed", "RUNNABLE RUNNABLE RUNNABLE RUNNABLE")
            if (hasInternetConnection) {
                for ((position, location) in locationAdapter.getLocations().withIndex()) {
                    val newLocation = location.name?.let { getLocation(it) }
                    if (newLocation != null) {
                        newLocation.alertMinTemp = null
                        newLocation.alertMaxTemp = null

//                        // If the alert hase been removed by the service from the DB, don't insert it again
                        databaseReference.child(location.name).child("alertMinTemp").get().addOnSuccessListener { result1 ->
                                val dbAlertMinTemp = if (result1.value == null) null else if (result1.value is Long) result1.value as Long else result1.value as Double
                            databaseReference.child(location.name).child("alertMaxTemp").get().addOnSuccessListener { result2 ->
                                val dbAlertMaxTemp = if (result2.value == null) null else if (result2.value is Long) result2.value as Long else result2.value as Double
                                if (dbAlertMinTemp != null && dbAlertMaxTemp != null) {
                                    newLocation.alertMinTemp = dbAlertMinTemp.toFloat()
                                    newLocation.alertMaxTemp = dbAlertMaxTemp.toFloat()
                                }

                                databaseReference.child(location.name).setValue(newLocation).addOnSuccessListener {
                                    locationAdapter.setLocation(position, newLocation)
                                }
                            }
                        }
                    }
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
                        databaseReference.child(locationName).setValue(location).addOnSuccessListener {
                        locationAdapter.addLocation(location)
                        citySearchBox.text.clear()
                    }
                } else {
                    Toast.makeText(this, "City not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
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

    override fun onPause() {
        locationList.removeCallbacks(runnable)
        super.onPause()
    }

    override fun onResume() {
        locationList.postDelayed(runnable, 15000)
        ConnectivityReceiver.connectivityReceiverListener = this
//        stopServiceWrapper()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        for (location in mutableLocationList) {
            location.name?.let {
                databaseReference.child(it).setValue(location)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("MainActivity", "Saved instance")

        outState.putParcelableArrayList("MutableLocationsList", ArrayList(mutableLocationList))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mutableLocationList = savedInstanceState.getParcelableArrayList("MutableLocationsList")!!

        Log.d("MainActivity", " ~~~~~~ON RESTORE INSTANCE~~~~~~~")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val location = data?.extras?.getParcelable<Location>("Location")
        val idx = data?.extras?.getInt("LocationIdx")
        if (resultCode == RESULT_OK) {
            Log.d("onActivityResult", location?.alertMinTemp.toString())
            Log.d("onActivityResult", location?.alertMaxTemp.toString())
            if (location != null && idx != null) {
                locationAdapter.setLocation(idx, location)
            }
        }
    }

    private fun loadData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val locationHashmap = postSnapshot.value as HashMap<*, *>

                    val name = locationHashmap["name"].toString()
                    val description = locationHashmap["description"].toString()
                    val currTemp = if (locationHashmap["currTemp"] is Long)
                        (locationHashmap["currTemp"] as Long).toDouble()
                    else
                        locationHashmap["currTemp"] as Double
                    val feelsLike = if (locationHashmap["feelsLike"] is Long)
                        (locationHashmap["feelsLike"] as Long).toDouble()
                    else
                        locationHashmap["feelsLike"] as Double
                    val windSpeed = locationHashmap["windSpeed"] as Double
                    val pressure = locationHashmap["pressure"] as Long
                    val humidity = locationHashmap["humidity"] as Long
                    var alertMinTemp: Float? = null
                    var alertMaxTemp: Float? = null

                    if (locationHashmap["alertMinTemp"] != null) {
                        alertMinTemp = if (locationHashmap["alertMinTemp"] is Double)
                            (locationHashmap["alertMinTemp"] as Double).toFloat()
                        else
                            (locationHashmap["alertMinTemp"] as Long).toFloat()
                    }

                    if (locationHashmap["alertMaxTemp"] != null) {
                        alertMaxTemp = if (locationHashmap["alertMaxTemp"] is Double)
                            (locationHashmap["alertMaxTemp"] as Double).toFloat()
                        else
                            (locationHashmap["alertMaxTemp"] as Long).toFloat()
                    }

                    val newLocation = Location(
                        name,
                        description,
                        currTemp.toFloat(),
                        feelsLike.toFloat(),
                        windSpeed.toFloat(),
                        pressure.toInt(),
                        humidity.toInt(),
                        alertMinTemp,
                        alertMaxTemp
                    )

//                    Log.d("postSnapshot", newLocation.toString())
                    locationAdapter.addLocation(newLocation)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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
                val description = weatherData!!.getString("description")
                val currTemp = weatherData!!.getString("temp")
                val feelsLike = weatherData!!.getString("feels_like")
                val windSpeed = weatherData!!.getString("speed")
                val pressure = weatherData!!.getString("pressure")
                val humidity = weatherData!!.getString("humidity")

                return Location(
                    cityName,
                    description,
                    currTemp.toFloat(),
                    feelsLike.toFloat(),
                    windSpeed.toFloat(),
                    pressure.toInt(),
                    humidity.toInt(),
                    null,
                    null
                )
            }
        }
        return null
    }

    private fun startService() {
        val serviceIntent = Intent(this, AlertService::class.java)
//        val input = "Acesta este un serviciu"
//        serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopServiceWrapper() {
        val serviceIntent = Intent(this, AlertService::class.java)
        stopService(serviceIntent)
    }

    companion object {
        fun getWeather(cityName: String): JSONObject {
            val url =
                "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=361d8bf5e3a482bf972852106c1d0698&units=metric"
            val resultJSON = URL(url).readText()
            val resultsJSONObject = JSONObject(resultJSON)

            val mainJSONObject = resultsJSONObject.getJSONObject("main")
            val windJSONObject = resultsJSONObject.getJSONObject("wind")
            val weatherJSONArray = resultsJSONObject.getJSONArray("weather")

            val resultWeatherData = JSONObject()
            resultWeatherData.put("temp", mainJSONObject.getString("temp"))
            resultWeatherData.put("feels_like", mainJSONObject.getString("feels_like"))
            resultWeatherData.put("pressure", mainJSONObject.getString("pressure"))
            resultWeatherData.put("humidity", mainJSONObject.getString("humidity"))
            resultWeatherData.put("speed", windJSONObject.getString("speed"))
            resultWeatherData.put("description", weatherJSONArray.getJSONObject(0).getString("main"))

            return resultWeatherData
        }
    }

//    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
//        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
//            if (serviceClass.name == service.service.className) {
//                Log.d("ServiceRunning", "SERVICE RUNNING")
//                return true
//            }
//        }
//        return false
//    }
}