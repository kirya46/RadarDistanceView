package com.common.radardistanceview

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        radar.post {
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 3000
                addUpdateListener {
                    radar.setAngle(it.animatedValue as Float)
                }
                start()
            }
        }
    }
}
