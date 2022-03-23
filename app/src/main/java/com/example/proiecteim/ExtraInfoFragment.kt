package com.example.proiecteim

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val minTempText = "Minimum Temperature\n\n${location!!.minTemp}°C"
        rootView.tvMinTemp.text = minTempText

        val maxTempText = "Maximum Temperature\n\n${location!!.maxTemp}°C"
        rootView.tvMaxTemp.text = maxTempText

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