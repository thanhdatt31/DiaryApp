package com.example.diaryapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_app_lock.*
import kotlinx.android.synthetic.main.activity_set_pass.*
import kotlinx.android.synthetic.main.activity_set_pass.edt_1
import kotlinx.android.synthetic.main.activity_set_pass.edt_2
import kotlinx.android.synthetic.main.activity_set_pass.edt_3
import kotlinx.android.synthetic.main.activity_set_pass.edt_4
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SetPassActivity : AppCompatActivity(), TextWatcher {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pass)
        edt_1.addTextChangedListener(this)
        edt_2.addTextChangedListener(this)
        edt_3.addTextChangedListener(this)
        edt_4.addTextChangedListener(this)
        btn_save.setOnClickListener {
            val pinCode =
                edt_1.text.toString() + edt_2.text.toString() + edt_3.text.toString() + edt_4.text.toString()
            if (pinCode.length == 4) {
                savePassCode(pinCode)
                showDialog()
            } else {
                Toast.makeText(this@SetPassActivity, "Save failed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Restart required")
        builder.setMessage("Save Success ! You must restart to apply changes")

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            val intent = baseContext.packageManager.getLaunchIntentForPackage(
                baseContext.packageName
            )
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        builder.show()
    }

    private fun savePassCode(passCode: String) {
        val pref = applicationContext.getSharedPreferences("my_pre", MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("pin_code", passCode)
        editor.apply()
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