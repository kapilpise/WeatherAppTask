package com.code.myweather.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.code.myweather.ui.home.HomeActivity
import com.code.myweatherapp.R
import com.code.myweatherapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        setUpSplashCounter()
    }

    private fun setUpSplashCounter() {
        object : CountDownTimer(3000, 200) {

            override fun onFinish() {
                var intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                println("Navigating to HomeActivity")
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }.start()
    }
}
