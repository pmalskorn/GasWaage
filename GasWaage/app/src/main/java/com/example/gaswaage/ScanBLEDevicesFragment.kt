package com.example.gaswaage

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gaswaage.databinding.BluetoothListItemBinding
import com.example.gaswaage.databinding.ScanBLeDevicesFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ScanBLEDevicesFragment : Fragment() {
    val bleViewModel = BLEViewModel.singelton
    lateinit var dataBinding : ScanBLeDevicesFragmentBinding

    val PERMISSION_REQUEST_CODE: Int = 101
    val PERMISSION_LIST = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH
    )

    val locationPermissionRequestId = 1

    companion object {
        fun newInstance() = ScanBLEDevicesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ScanBLeDevicesFragmentBinding.inflate(inflater)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.vm = bleViewModel

        requestPermission()
        setupObservers()


        return dataBinding.root
    }

    private fun setupObservers() {
        bleViewModel.devices.observe(viewLifecycleOwner){
            dataBinding.llContainer.removeAllViews()
            it?.forEach{ scanResult ->
               val listItem = BluetoothListItemBinding.inflate(layoutInflater).apply {
                   name = scanResult.device.name ?: scanResult.device.address
                   address = scanResult.device.address
                   strenght = scanResult.rssi.toString()
                   lifecycleOwner = viewLifecycleOwner
               }
                listItem.root.setOnClickListener {
                    showDialog(scanResult, requireContext())
                }
                dataBinding.llContainer.addView(listItem.root)
            }
        }

        bleViewModel.isScanning.observe(viewLifecycleOwner){
            if (it){
                GlobalScope.launch(Dispatchers.IO) {
                    var progress = 0
                    while (progress < 100){
                        dataBinding.pb.progress = progress
                        progress += 5
                        delay(500)
                    }
                }
            } else {
                dataBinding.pb.progress = 0
            }
        }
    }

    private fun showDialog(scanResult: ScanResult, context: Context) {
        var dialog: Dialog? = null
        var builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to Connect to ${scanResult.device.name ?: scanResult.device.address}")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    bleViewModel.connectToDevice(scanResult, context)
                    findNavController().navigate(
                     R.id.action_global_gasTankFragment
                    )
                })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        bleViewModel.setupBluetooth(activity)
    }

    private fun requestPermission() = requestPermissions(PERMISSION_LIST, PERMISSION_REQUEST_CODE)

}