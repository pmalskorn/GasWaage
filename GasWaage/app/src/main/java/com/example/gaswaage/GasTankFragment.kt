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
    }

    fun send(message: String) {

        var b = message.encodeToByteArray()
        try {
            service.write(b)
        } catch (e: Exception){
            //egal ^^
        }
    }

    override fun onStart() {
        super.onStart()
        service.attach(this)
        socket = SerialSocket(requireActivity().applicationContext, bleViewModel.gatt.device)

        service.connect(socket)

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                send("a")
            }
        }
    }

    override fun onSerialConnect() {

    }

    override fun onSerialConnectError(e: Exception?) {

    }

    override fun onSerialRead(data: ByteArray?) {
        var answer = String(data ?: byteArrayOf()).split('\n').firstOrNull()?.toInt() ?: 0
        var mappedvalue = (16373965 - answer) / 24.545
        Toast.makeText(requireContext(), mappedvalue.toString(), Toast.LENGTH_SHORT).show()
        setProgress((mappedvalue / 2000 * 100).toInt())
    }

    override fun onSerialIoError(e: Exception?) {

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }
}