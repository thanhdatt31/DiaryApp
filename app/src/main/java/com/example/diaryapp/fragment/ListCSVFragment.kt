package com.example.diaryapp.fragment

import android.app.AlertDialog
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diaryapp.R
import com.example.diaryapp.Ultis
import com.example.diaryapp.Ultis.NEW_LINE
import com.example.diaryapp.adapter.CSVAdapter
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_list_c_s_v.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class ListCSVFragment : BottomSheetDialogFragment() {
    private val listUri: ArrayList<Uri> = arrayListOf()
    private var csvAdapter: CSVAdapter = CSVAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_c_s_v, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListFile()
        recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            listUri.reverse()
            csvAdapter.setData(listUri)
            adapter = csvAdapter
            csvAdapter.setOnClickListener(onClicked)
        }
        ic_back.setOnClickListener {
            dismiss()
        }
    }

    private fun getListFile() {
        File(Environment.getExternalStorageDirectory().absolutePath + "/BackupVip").walkBottomUp()
            .forEach {
                if (it.isFile) {
                    listUri.add(Uri.fromFile(it))
                }
            }
    }

    private val onClicked = object : CSVAdapter.OnItemClickListener {
        override fun onClicked(uri: Uri) {
            showDialog(uri)
        }

    }

    private fun showDialog(uri: Uri) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alert")
        builder.setMessage("Delete all data and restore data from ${getFileName(uri)} ?")

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
           GlobalScope.launch {
               DiaryDatabase.getDatabase(requireContext()).clearAllTables()
           }
            importData(uri)
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun importData(uri: Uri) {
        try {
            val fileOutputStream = requireContext().contentResolver.openInputStream(uri)
            val reader = InputStreamReader(fileOutputStream)
            val bufferReader = BufferedReader(reader)
            var line = bufferReader.readLine()
            while (line != null) {
                val arr: List<String> = line.split(Ultis.KEY_SPACE)
                if (arr.size < 5) {
                    val titleDiary = arr[1].replace(NEW_LINE, "\n")
                    val contentDiary = arr[2].replace(NEW_LINE, "\n")
                    val dateDiary = arr[3]
                    CoroutineScope(Dispatchers.IO).launch {
                        val diary = Diary()
                        diary.diaryText = contentDiary
                        diary.dateTime = dateDiary
                        diary.title = titleDiary
                        DiaryDatabase.getDatabase(requireContext()).diaryDao().insertDiary(diary)
                    }
                    line = bufferReader.readLine()
                }
            }
            fileOutputStream?.close()
            reader.close()
            Toast.makeText(requireContext(), "Restore successful", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {

        }
        dismiss()
    }
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }
}

