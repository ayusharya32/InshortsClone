package com.appsbyayush.quicknews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        auth = Firebase.auth

        lifecycleScope.launch {
            val intent = if(auth.currentUser == null) {
                Intent(this@SplashActivity, LoginActivity::class.java)
            } else {
                Intent(this@SplashActivity, HomeActivity::class.java)
            }

            delay(1500L)
            startActivity(intent)
            finish()
        }
    }
}