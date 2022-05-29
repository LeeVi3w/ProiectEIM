package com.example.proiecteim

import android.graphics.drawable.PictureDrawable
import android.media.ImageReader
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import kotlinx.android.synthetic.main.activity_map.*
import java.lang.Exception
import java.net.URL

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        HttpsTrustManager.allowAllSSL()

        val url = "https://www.meteoromania.ro/avertizari/"
        val advertsHTML = URL(url).readText()

        val startIndexString = "https://www.meteoromania.ro/wp-content/plugins/meteo/harti/harta.svg.php?id_avertizare="
        val startIndex = advertsHTML.indexOf(startIndexString)
        val mapLink = advertsHTML.substring(startIndex, startIndex + startIndexString.length + 4)

        val svgString = URL(mapLink).readText()
        val svg = SVG.getFromString(svgString)
        val drawable = PictureDrawable(svg.renderToPicture())

        Glide.with(this).load(drawable).into(ivMap)
    }
}