package com.example.diaryapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.activity_backup.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class BackupActivity : AppCompatActivity() {
    private val STORE_REQUEST_CODE_EXPORT = 1
    private val STORE_REQUEST_CODE_IMPORT = 2
    private lateinit var storagePermission: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        btn_backup.setOnClickListener {
            if (checkStoragePermission()) {
                exportCSV()
            } else {
                requestPermissionsExport()
            }

        }
    }

    private fun exportCSV() {
        val folder = File("${Environment.getExternalStorageDirectory()}/BackupVip")
        var isFolderCreated = false
        if (!folder.exists()) isFolderCreated = folder.mkdirs()
        val csvFilename = "CSVBackup.csv"
        val fileNameAndPath = "$folder/$csvFilename"
        GlobalScope.launch(Dispatchers.IO) {
            val recordList = DiaryDatabase.getDatabase(this@BackupActivity).diaryDao()
                .getAllDiary() as ArrayList<Diary>
            try {
                val fw = FileWriter(fileNameAndPath)
                for (i in recordList.indices) {
                    fw.append("" + recordList[i].id)
                    fw.append(",")
                    fw.append("" + recordList[i].dateTime)
                    fw.append(",")
                    fw.append("" + recordList[i].title)
                    fw.append(",")
                    fw.append("" + recordList[i].diaryText)
                    fw.append(",")
                }
                fw.flush()
                fw.close()
                Toast.makeText(
                    this@BackupActivity,
                    "Backup to $fileNameAndPath",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("datnt", "exportCSV: saved")
            } catch (e: Exception) {
//                Toast.makeText(this@BackupActivity, "Backup Failed !", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissionsImport() {
        ActivityCompat.requestPermissions(this, storagePermission, STORE_REQUEST_CODE_IMPORT)
    }

    private fun requestPermissionsExport() {
        ActivityCompat.requestPermissions(this, storagePermission, STORE_REQUEST_CODE_EXPORT)
    }


}