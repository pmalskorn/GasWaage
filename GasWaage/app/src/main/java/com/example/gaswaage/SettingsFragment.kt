package com.example.gaswaage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.gaswaage.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    lateinit var dataBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentSettingsBinding.inflate(layoutInflater)
        setupButtons()
        return dataBinding.root
    }

    private fun setupButtons() {
        dataBinding.bReset.setOnClickListener {
            SettingsViewModel.singelton.removeSetting("BT_DEVICE")
            dataBinding.textView6.text = "Not Initialized"
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "Settings"
        with(SettingsViewModel.singelton){
            dataBinding.textView2.text = if (getIntSetting("WEIGHT_ZERO") == 0) "Not Initialized" else "${getIntSetting("WEIGHT_ZERO")}"
            dataBinding.textView4.text = if (getIntSetting("WEIGHT_ONE_KG") == 0) "Not Initialized" else "${getIntSetting("WEIGHT_ONE_KG")}"
            dataBinding.textView6.text = if (getStringSetting("BT_DEVICE").isNullOrEmpty()) "Not Initialized" else getStringSetting("BT_DEVICE")
        }
    }


}