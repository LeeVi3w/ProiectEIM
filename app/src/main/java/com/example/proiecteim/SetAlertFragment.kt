package com.example.proiecteim

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_set_alert.*
import kotlinx.android.synthetic.main.fragment_set_alert.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetAlertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetAlertFragment : Fragment() {
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            location = it.getParcelable("SetAlertLocation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_set_alert, container, false)

        // Create a new ExtraInfoFragment using same location obj and replace the SetAlertFragment
        rootView.btnConfirmAlert.setOnClickListener {
            // Some other logic will go here
            Log.d("SetAlertFragment", etAlertTemp.text.toString())
            val alertTemp = etAlertTemp.text.toString().toFloatOrNull()

            if (alertTemp != null) {
                location!!.alertTemp = alertTemp
                requireActivity().recreate()

                val locationIdx = activity?.intent?.extras?.getInt("LocationIdx")
                if (locationIdx != null) {
                    activity?.intent?.extras?.getParcelableArrayList<Location>("LocationList")?.get(locationIdx)?.alertTemp = alertTemp
                }
                Log.d("SetAlertFragment", activity?.intent?.extras?.getParcelableArrayList<Location>("LocationList").toString())
                if (locationList == null)
                    Log.d("SetAlertFragment", "Fuck")
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flLocationFragment, ExtraInfoFragment.newInstance(location))
                    commit()
                }

            }
        }

        rootView.btnCancelAlert.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.flLocationFragment, ExtraInfoFragment.newInstance(location))
                commit()
            }
        }

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(location: Location?) =
            SetAlertFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("SetAlertLocation", location)
                }
            }
    }
}