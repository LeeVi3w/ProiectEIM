package com.example.proiecteim

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Runnable
import java.net.URL

class AlertService : Service() {
    private val channelId = "Notification from Service"
    private lateinit var runnable: Runnable
    private var isServiceStarted = false
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var databaseReference: DatabaseReference
    private val DB_INSTANCE_URL = "https://proiecteim-b2735-default-rtdb.europe-west1.firebasedatabase.app"
    private val thisService = this
    private var currNotificationID = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val channel =
            NotificationChannel(
                channelId,
                "Alert channel",
                NotificationManager.IMPORTANCE_HIGH
            )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE", Build.VERSION.SDK_INT.toString())
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Service started")
            .setContentText("")
            .setSmallIcon(R.drawable.background)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        startService()

        return START_NOT_STICKY
    }

    private fun startService() {
        if (isServiceStarted) return
        Toast.makeText(this, "Service is starting", Toast.LENGTH_SHORT).show()
        isServiceStarted = true

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire(10*60*1000L /*10 minutes*/)
            }
        }

        checkAlerts()
    }

    private fun checkAlerts() {
        databaseReference = FirebaseDatabase.getInstance(DB_INSTANCE_URL).getReference("Locations")

        HttpsTrustManager.allowAllSSL()

        Thread {
            while (isServiceStarted) {
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val locationHashmap = postSnapshot.value as HashMap<*, *>

                            val locationName = locationHashmap["name"].toString()
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

                            val currentWeather = getWeather(locationName)
                            if (alertMinTemp != null && alertMaxTemp != null) {
                                val currTemp = currentWeather.getString("temp").toFloat()
                                if (alertMinTemp <= currTemp && currTemp <= alertMaxTemp) {
                                    databaseReference.child(locationName).child("alertMinTemp")
                                        .removeValue().addOnSuccessListener {
                                            Log.d("REMOVED MIN TEMP FOR ", locationName)
                                        }
                                    databaseReference.child(locationName).child("alertMaxTemp")
                                        .removeValue().addOnSuccessListener {
                                            Log.d("REMOVED MAX TEMP FOR", locationName)
                                        }

                                    val content =
                                        "The temperature has reached $currTemp degrees Celsius!"
                                    val notificationIntent =
                                        Intent(thisService, MainActivity::class.java)
                                    val pendingIntent = PendingIntent.getActivity(
                                        thisService,
                                        0, notificationIntent, 0
                                    )
                                    val notification: Notification =
                                        NotificationCompat.Builder(thisService, channelId)
                                            .setContentTitle("Temperature in $locationName")
                                            .setContentText(content)
                                            .setSmallIcon(R.drawable.background)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .build()

                                    with(NotificationManagerCompat.from(thisService)) {
                                        notify(currNotificationID++, notification)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
                Thread.sleep(7000)
            }
        }.start()


        // Get alerts from meteoromania.ro ---- Not really viable, their XML is an unstructured mess
//        Thread {
//            val url = "https://www.meteoromania.ro/avertizari-xml.php"
//            val resultXML = URL(url).readText()
//
//            Log.d("XML", resultXML.substring(2500, 2600))
//
//            Thread.sleep(30000)
//        }.start()
    }

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

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}