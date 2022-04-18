package com.example.proiecteim

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.fragment_extra_info.*
import kotlinx.android.synthetic.main.fragment_set_alert.*

class LocationActivity : AppCompatActivity() {
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        location = intent.getParcelableExtra<Location>("Location")

        val extraInfoFragment = ExtraInfoFragment.newInstance(location)
        val setAlertFragment = SetAlertFragment.newInstance(location)

        val alertTemp: String = if (location!!.alertMinTemp != null && location!!.alertMaxTemp != null)
            "\n(Alert: ${location!!.alertMinTemp.toString()} - ${location!!.alertMaxTemp.toString()}°C)"
        else
            ""

        val currTempText = "${location!!.name}\n${location!!.currTemp}°C" + alertTemp
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("Location", location)
        intent.putExtra("LocationIdx", getIntent().extras?.getInt("LocationIdx"))
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }
}