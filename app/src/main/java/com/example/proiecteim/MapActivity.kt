package com.example.proiecteim

import android.graphics.drawable.PictureDrawable
import android.media.ImageReader
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
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

        Log.d("MAP ACITVITY", "MAPA ACASCIASCASCAS")

        val url = "https://www.meteoromania.ro/avertizari/"
        val advertsHTML = URL(url).readText()

        val startIndexString = "https://www.meteoromania.ro/wp-content/plugins/meteo/harti/harta.svg.php?id_avertizare="
        val startIndex = advertsHTML.indexOf(startIndexString)
        val mapLink = advertsHTML.substring(startIndex, startIndex + startIndexString.length + 4)
        Log.d("Map Link", mapLink)


        val svgString = URL(mapLink).readText()
        val svg = SVG.getFromString(svgString)
        val drawable = PictureDrawable(svg.renderToPicture())

        Glide.with(this).load(drawable).into(ivMap)

//        Log.v("svg", URL(mapLink).readText())
//        ivMap.loadSvg(mapLink)
    }

    fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
}