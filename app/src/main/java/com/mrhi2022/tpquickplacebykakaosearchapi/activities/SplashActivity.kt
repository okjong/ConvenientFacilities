package com.mrhi2022.tpquickplacebykakaosearchapi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Handler(Looper.getMainLooper()).postDelayed({
            startActivity( Intent(this, LoginActivity::class.java) )
            finish()
        }, 1500)

    }
}