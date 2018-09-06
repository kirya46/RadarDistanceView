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
                    val angle = maxProgress - currentProgress
                    radarView.setScaleFactor(angle)
                }
            }
        })

        radarView.setScaleFactor(seekBar.max / 100f)




        ValueAnimator.ofFloat(0f, 100f).apply {
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                if (animatedValue == 100f) {
                    radarView.setPulsingScaleFactor(1 / 100f)
                } else {
                    radarView.setPulsingScaleFactor(animatedValue / 100f)
                }
            }
            duration = 5000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }
}
