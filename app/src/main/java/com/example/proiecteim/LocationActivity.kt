package com.example.proiecteim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.fragment_extra_info.*
import kotlinx.android.synthetic.main.fragment_set_alert.*

class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val location = intent.getParcelableExtra<Location>("Location")
        Log.d("LocationActivity", location.toString())

        val extraInfoFragment = ExtraInfoFragment.newInstance(location)
        val setAlertFragment = SetAlertFragment.newInstance(location)

        val currTempText = "${location!!.name}\n\n${location.currTemp}°C"
        tvCurrentTemp.text = currTempText

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flLocationFragment, extraInfoFragment)
            commit()
        }

        btnSetAlert.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flLocationFragment, setAlertFragment)
                commit()
            }
        }

    }
}