package com.example.diaryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val sp  = PreferenceManager.getDefaultSharedPreferences(this@SplashActivity)
        val test = sp.getBoolean("sync",true)
        Log.d("datnt", "onCreate: $test")
        GlobalScope.launch(Dispatchers.Main) {
            delay(3000)
            val intent = Intent(this@SplashActivity, AppLockActivity::class.java )
            startActivity(intent)
            finish()

        }
    }
}