package com.example.gaswaage

import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gaswaage.databinding.GasTankFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class GasTankFragment : Fragment(), SerialListener, ServiceConnection {
    lateinit var viewBinding: GasTankFragmentBinding
    val bleViewModel = BLEViewModel.singelton
    val settings = SettingsViewModel.singelton

    var running = true

    private val service = SerialService()
    private lateinit var socket: SerialSocket

    companion object {
        fun newInstance() = GasTankFragment()
    }

    val gasTankViewModel = GasTankViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = GasTankFragmentBinding.inflate(inflater)
        setupButtons()

        service.attach(this)
        socket = SerialSocket(requireActivity().applicationContext, bleViewModel.gatt.device)

        service.connect(socket)

        GlobalScope.launch(Dispatchers.IO) {
            while (running) {
                send("a")
                delay(1000)
            }
        }

        //setProgress(1)

        return viewBinding.root
    }

    private fun setupButtons() {
    }

    fun setProgress(value: Int, duration: Long = 1000) {
        val oldvalue = gasTankViewModel.progress.value ?: 0

        val anim = ValueAnimator.ofInt(oldvalue * 2, value * 2)

        anim.addUpdateListener { valueAnimator ->
            viewBinding.vProgress.layoutParams = viewBinding.vProgress.layoutParams.apply {
                var tmp = valueAnimator.animatedValue as Int

                height = if (tmp > 0){
                    tmp*2
                }else {
                    1
                }
            }
            viewBinding.tvProgress.text = "${(valueAnimator.animatedValue as Int) / 2}%"
        }
        anim.duration = duration
        anim.start()
        gasTankViewModel.progress.postValue(value)


    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "Gas Level"
        running = true
        GlobalScope.launch(Dispatchers.IO) {

                send("a")
                delay(1000)

        }
    }

    override fun onStop() {
        super.onStop()
    }

    fun send(message: String) {

        var b = message.encodeToByteArray()
        try {
            service.write(b)
        } catch (e: Exception){
            //egal ^^
        }
    }

    override fun onSerialConnect() {

    }

    override fun onSerialConnectError(e: Exception?) {

    }

    override fun onSerialRead(data: ByteArray?) {
        var answer = String(data ?: byteArrayOf()).split('\n').firstOrNull()?.toInt() ?: 0
        bleViewModel.lastValue.postValue(answer)
        //var mappedvalue = (16373965 - answer) / 24.545
        //Toast.makeText(requireContext(), mappedvalue.toString(), Toast.LENGTH_SHORT).show()
        //setProgress((mappedvalue / 2000 * 100).toInt())
        var weight = WeightCell.getWeightFromMeasurement(answer, Accuracy.G)

        var maxGas = settings.getIntSetting("BOTTLE_FULL")-settings.getIntSetting("BOTTLE_EMPTY")
        val percent = (weight-settings.getIntSetting("BOTTLE_EMPTY"))/maxGas

        when{
            percent > 1.0 -> {setProgress(100)}
            percent <= 0 -> {setProgress(0)}
            else -> {setProgress((percent*100).toInt())}
        }

    }

    override fun onSerialIoError(e: Exception?) {

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }
}