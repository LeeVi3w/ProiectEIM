package com.example.proiecteim

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.fragment_extra_info.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [ExtraInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExtraInfoFragment : Fragment() {
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            location = it.getParcelable("ExtraInfoLocation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_extra_info, container, false)

        val description = location!!.description.toString().lowercase()
        if (description == "clouds")
            rootView.gifDescription.setImageResource(R.drawable.clouds)
        else if (description == "clear")
            rootView.gifDescription.setImageResource(R.drawable.clear)
        else if (description == "snow")
            rootView.gifDescription.setImageResource(R.drawable.snow)
        else if (description == "thunderstorm" || description == "rain")
            rootView.gifDescription.setImageResource(R.drawable.thunderstorm)

        val minTempText = "Feels like\n\n${location!!.feelsLike}°C"
        rootView.tvFeelsLike.text = minTempText

        val maxTempText = "Wind speed\n\n${location!!.windSpeed} m/s"
        rootView.tvWindSpeed.text = maxTempText

        val humidityText = "Humidity\n\n${location!!.humidity}%"
        rootView.tvHumidity.text = humidityText

        val pressureText = "Pressure\n\n${location!!.pressure} hPa"
        rootView.tvPressure.text = pressureText

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(location: Location?) =
            ExtraInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("ExtraInfoLocation", location)
                }
            }
    }
}