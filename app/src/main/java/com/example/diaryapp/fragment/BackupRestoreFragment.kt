package com.example.diaryapp.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.diaryapp.R
import com.example.diaryapp.Ultis.KEY_SPACE
import com.example.diaryapp.Ultis.NEW_LINE
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.fragment_backup_restore.*
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
        storagePermission = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        ic_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        view.view_backup.setOnClickListener {
            if (checkPermission()) {
                showDialog()
            } else {
                requestStoragePermissionExport()
            }
        }
        view.view_restore.setOnClickListener {
            if (checkPermissionRead()) {
                openCSVList()
            } else {
                requestStoragePermissionImport()
            }

        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun openCSVList() {
        val listCSVFragment = ListCSVFragment()
        listCSVFragment.show(childFragmentManager, "")
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
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermissionExport() {
        requestPermissions(
            storagePermission,
            STORAGE_REQUEST_CODE_EXPORT
        )
    }

    private fun requestStoragePermissionImport() {
        requestPermissions(
            storagePermission,
            STORAGE_REQUEST_CODE_IMPORT
        )
    }

    private fun exportCSV(name: String) {
        val folder = File("${Environment.getExternalStorageDirectory()}/BackupVip")
        var isFolderCreated = false
        if (!folder.exists()) isFolderCreated = folder.mkdirs()
        val csvFilename = "$name.csv"
        val fileNameAndPath = "$folder/$csvFilename"
        GlobalScope.launch(Dispatchers.IO) {
            val recordList = DiaryDatabase.getDatabase(requireContext()).diaryDao()
                .getAllDiary() as ArrayList<Diary>
            try {
                val fw = FileWriter(fileNameAndPath)
                for (i in recordList) {
                    fw.append("${i.id}")
                    fw.append(KEY_SPACE)
                    fw.append(i.title?.replace("\n".toRegex(), NEW_LINE))
                    fw.append(KEY_SPACE)
                    fw.append(i.diaryText?.replace("\n".toRegex(), NEW_LINE))
                    fw.append(KEY_SPACE)
                    fw.append(i.dateTime)
                    fw.append("\n")
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
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Backup Failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Title Required!")
        val input = EditText(requireContext())
        input.hint = "Enter File Name"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            when (input.text.toString()) {
                "" -> {
                    Toast.makeText(
                        requireContext(),
                        "You must enter file name !",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                else -> {
                    exportCSV(input.text.toString())
                }
            }

        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUEST_CODE_EXPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialog()
                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            STORAGE_REQUEST_CODE_IMPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCSVList()
                } else {
                    Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}