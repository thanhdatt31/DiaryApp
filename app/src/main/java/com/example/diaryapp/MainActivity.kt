package com.example.diaryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.diaryapp.fragment.HomeFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,HomeFragment()).commit()
    }
}