package com.example.proiecteim

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_set_alert.*
import kotlinx.android.synthetic.main.fragment_set_alert.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [SetAlertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SetAlertFragment : Fragment() {
    private val DB_INSTANCE_URL = "https://proiecteim-b2735-default-rtdb.europe-west1.firebasedatabase.app"
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
            Log.d("SetAlertFragment", etAlertMinTemp.text.toString())
            val alertMinTemp = etAlertMinTemp.text.toString().toFloatOrNull()
            val alertMaxTemp = etAlertMaxTemp.text.toString().toFloatOrNull()

            if (alertMinTemp != null && alertMaxTemp != null && alertMinTemp <= alertMaxTemp) {
                location!!.alertMinTemp = alertMinTemp
                location!!.alertMaxTemp = alertMaxTemp
                val databaseReference = FirebaseDatabase.getInstance(DB_INSTANCE_URL).getReference("Locations")
                requireActivity().recreate()

                val locationIdx = activity?.intent?.extras?.getInt("LocationIdx")
                if (locationIdx != null) {
                    activity?.intent?.extras?.getParcelableArrayList<Location>("LocationList")?.get(locationIdx)?.alertMinTemp = alertMinTemp
                    activity?.intent?.extras?.getParcelableArrayList<Location>("LocationList")?.get(locationIdx)?.alertMaxTemp = alertMaxTemp

                    databaseReference.child(location!!.name!!).setValue(location).addOnSuccessListener {
                        Log.d("addedAlert", location.toString())
                    }
                }
//                Log.d("SetAlertFragment", activity?.intent?.extras?.getParcelableArrayList<Location>("LocationList").toString())
                if (locationList == null)
//                    Log.d("SetAlertFragment", "Fuck")
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