package com.example.diaryapp.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.diaryapp.R
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.fragment_backup_restore.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter


class BackupRestoreFragment : Fragment() {
    private val STORAGE_REQUEST_CODE_EXPORT: Int = 1
    private val STORAGE_REQUEST_CODE_IMPORT: Int = 2
    private lateinit var storagePermission: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.view_backup.setOnClickListener {
            exportCSV()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BackupRestoreFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun checkPermissionRead(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            storagePermission,
            STORAGE_REQUEST_CODE_EXPORT
        )
    }

    private fun requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            storagePermission,
            STORAGE_REQUEST_CODE_IMPORT
        )
    }

    private fun exportCSV() {
        val folder = File("${Environment.getExternalStorageDirectory()}/BackupVip")
        var isFolderCreated = false
        if (!folder.exists()) isFolderCreated = folder.mkdirs()
        val csvFilename = "CSVBackup.csv"
        val fileNameAndPath = "$folder/$csvFilename"
        GlobalScope.launch(Dispatchers.IO) {
            val recordList = DiaryDatabase.getDatabase(requireContext()).diaryDao()
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
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Backup to $fileNameAndPath",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
//                Toast.makeText(this@BackupActivity, "Backup Failed !", Toast.LENGTH_SHORT).show()
            }
        }

    }

}