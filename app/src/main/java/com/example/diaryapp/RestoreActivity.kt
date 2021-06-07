package com.example.diaryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import kotlinx.android.synthetic.main.activity_restore.*

class RestoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)
        btn_restore.setOnClickListener {
            openFile()
        }
    }
    private fun openFile() {
        val intent = Intent(Intent(Intent.ACTION_GET_CONTENT))
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "*/*"
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, "pickerInitialUri")
        startActivityForResult(Intent.createChooser(intent, "Open CSV"), 756)

    }

}