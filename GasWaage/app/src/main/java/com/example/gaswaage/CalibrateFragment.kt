package com.example.gaswaage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.gaswaage.databinding.FragmentCalibrateBinding


class CalibrateFragment : Fragment() {

    private lateinit var dataBinding: FragmentCalibrateBinding
    val bleViewModel = BLEViewModel.singelton
    val settingsViewModel = SettingsViewModel.singelton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = FragmentCalibrateBinding.inflate(inflater)
        setupButtons()
        return dataBinding.root

    }

    private fun setupButtons() {
        dataBinding.bWightZero.setOnClickListener {
            WeightCell.setZero(bleViewModel.lastValue.value ?: 0)
        }
        dataBinding.bWeightOne.setOnClickListener {
            WeightCell.setOneKg(bleViewModel.lastValue.value ?: 0)
        }
        dataBinding.bSetEmpty.setOnClickListener {
            settingsViewModel.writeSetting("BOTTLE_EMPTY", dataBinding.etEmptyWeight.text.toString().toIntOrNull() ?: 0)
        }
        dataBinding.bSetFull.setOnClickListener {
            settingsViewModel.writeSetting("BOTTLE_FULL", dataBinding.etFullWeight.text.toString().toIntOrNull() ?: 0)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "Calibration"
    }
}