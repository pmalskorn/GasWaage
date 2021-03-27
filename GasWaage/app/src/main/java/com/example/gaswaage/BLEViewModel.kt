package com.example.gaswaage

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BLEViewModel : ViewModel() {
    val isScanning = MutableLiveData<Boolean>(false)
    var devices = MutableLiveData<List<ScanResult>>(mutableListOf())

    companion object {
        var singelton: BLEViewModel = BLEViewModel()
    }


    lateinit var gatt : BluetoothGatt

    var scanPeriod: Long = 10000
    var strengthThreshold = -200


    val connectionState = MutableLiveData<Int>(BluetoothProfile.STATE_DISCONNECTED)

    private val callBack: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            var rssi = result.rssi

            var tmp = devices.value?.toMutableList()
            val allreadyScaned = tmp?.filter { it.device.address == result.device.address}?.size?: 0 > 0

            if (!allreadyScaned && result.rssi > strengthThreshold){
                tmp?.add(result)
                devices.postValue(tmp)
            }
        }
    }

    lateinit var bta: BluetoothAdapter
    lateinit var bls: BluetoothLeScanner
    var isScan = false

    fun setupBluetooth(activity: FragmentActivity?) {
        val btm = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bta = btm.adapter
        bls = bta.bluetoothLeScanner
    }

    fun startScanning() {
        if (::bls.isInitialized) {
            devices.postValue(listOf())
            viewModelScope.launch {
                isScanning.postValue(true)
                bls.startScan(callBack)
                delay(scanPeriod)
                bls.stopScan(callBack)
                isScanning.postValue(false)
            }
        }
    }

    fun connectToDevice(scanresult: ScanResult, context: Context){
        gatt = scanresult.device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicChanged(gatt, characteristic)
                println("")
            }

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                connectionState.postValue(newState)
                when (newState) {
                    (BluetoothProfile.STATE_CONNECTED) -> {
                        gatt!!.discoverServices()   // To list services provided by the device
                    }
                    (BluetoothProfile.STATE_DISCONNECTED) -> {}
                    (BluetoothProfile.STATE_CONNECTING) -> {}
                    (BluetoothProfile.STATE_DISCONNECTING) -> {}
                    else -> {}
                }
            }

        })
    }

    fun send(){
        if (::gatt.isInitialized){
            for (gattService in gatt.getServices()) {
                var c = gattService.getCharacteristics()
                for (characteristic in c){
                    var message = "a"
                    var b = message.encodeToByteArray()
                    if (characteristic != null){
                        characteristic.value = b
                        gatt.writeCharacteristic(characteristic)

                        gatt.readCharacteristic(characteristic)
                    }
                }
            }


        }
    }
}