package com.example.gaswaage

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gaswaage.databinding.GasTankFragmentBinding
import kotlin.random.Random


class GasTankFragment : Fragment() {

    lateinit var viewBinding: GasTankFragmentBinding

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
        viewBinding.bTest.setOnClickListener {
            val newvalue = 1 + Random.nextInt(99)
            setProgress(newvalue)
        }
    }

    fun setProgress(value: Int, duration: Long = 5000){
        if (value in 0..100) {
            val anim = ValueAnimator.ofInt(1, value*2)
            anim.addUpdateListener { valueAnimator ->
                viewBinding.vProgress.layoutParams = viewBinding.vProgress.layoutParams.apply { height = (valueAnimator.animatedValue as Int).dp }
                viewBinding.tvProgress.text = "${(valueAnimator.animatedValue as Int)/2}%"
            }
            anim.duration = duration
            anim.start()
            gasTankViewModel.progress.postValue(value)
        }
    }
}