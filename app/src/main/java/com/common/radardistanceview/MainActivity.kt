package com.common.radardistanceview

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                radarView.post {
                    val maxProgress = seekBar.max / 100f
                    val currentProgress = progress / 100f

                    //decrease scale factor
                    val scaleFactor = maxProgress - currentProgress
                    radarView.setScaleFactor(scaleFactor)
                }
            }
        })

        //scale to 50% and than decrease scale factor
        seekBar.progress = seekBar.max / 2


        //Pulse animation
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                radarView.setPulsingScaleFactor(animatedValue)
            }
            duration = 5000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }

        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                radarView.setspsf(animatedValue)
            }
            startDelay = 2500
            duration = 5000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }
}
