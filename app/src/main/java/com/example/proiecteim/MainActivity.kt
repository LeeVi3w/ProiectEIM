package com.example.proiecteim

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationAdapter = LocationAdapter(mutableListOf())

        locationList.adapter = locationAdapter
        locationList.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val locationName = citySearchBox.text.toString()
            if (locationName.isNotEmpty()) {
                addLocation(locationName)
            }
        }

    }

    private fun addLocation(cityName: String) {
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

            val location = Location(cityName, currTemp.toFloat(), maxTemp.toFloat(), minTemp.toFloat(), pressure.toInt(), humidity.toInt())
            locationAdapter.addLocation(location)
            citySearchBox.text.clear()
        }

    }

    private fun getWeather(cityName: String): JSONObject {
        val url: String =
            "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=361d8bf5e3a482bf972852106c1d0698&units=metric"
        val resultJSON = URL(url).readText()
        Log.d("Weather Result", resultJSON)
        val jsonObj = JSONObject(resultJSON)
        return jsonObj.getJSONObject("main")
    }
}