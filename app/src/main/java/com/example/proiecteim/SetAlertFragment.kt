package com.example.proiecteim

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Some fuckery, create a new ExtraInfoFragment using same location obj and replace the SetAlertFragment
        rootView.btnConfirmAlert.setOnClickListener {
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