package com.example.diaryapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_app_lock.*

class AppLockActivity : AppCompatActivity(), TextWatcher {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock)
        edt_1.addTextChangedListener(this)
        edt_2.addTextChangedListener(this)
        edt_3.addTextChangedListener(this)
        edt_4.addTextChangedListener(this)

        btn_submit.setOnClickListener {
            val passCode =
                edt_1.text.toString() + edt_2.text.toString() + edt_3.text.toString() + edt_4.text.toString()
            if (!getPinCode().isNullOrBlank()) {
                if (passCode == getPinCode()) {
                    val intent = Intent(this@AppLockActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPinCode(): String? {
        val pref = applicationContext.getSharedPreferences("my_pre", MODE_PRIVATE)
        return pref.getString("pin_code", "")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s != null) {
            if (s.length == 1) {
                if (edt_1.length() == 1) {
                    edt_2.requestFocus()
                }
                if (edt_2.length() == 1) {
                    edt_3.requestFocus()
                }
                if (edt_3.length() == 1) {
                    edt_4.requestFocus()
                }
            }
            if (s.isEmpty()) {
                if (edt_4.length() == 0) {
                    edt_3.requestFocus()
                }
                if (edt_3.length() == 0) {
                    edt_2.requestFocus()
                }
                if (edt_2.length() == 0) {
                    edt_1.requestFocus()
                }
            }

        }
    }
}