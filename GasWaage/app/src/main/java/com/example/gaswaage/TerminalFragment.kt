package com.example.gaswaage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gaswaage.databinding.FragmentTerminalBinding


class TerminalFragment : Fragment() {
    lateinit var dataBinding : FragmentTerminalBinding
    val bleViewModel = BLEViewModel.singelton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentTerminalBinding.inflate(inflater)

        setupButton()

        return dataBinding.root
    }

    fun setupButton(){
        dataBinding.button.setOnClickListener {
            bleViewModel.send()
        }
    }


}