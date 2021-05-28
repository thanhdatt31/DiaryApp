package com.example.diaryapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.diaryapp.BaseFragment
import com.example.diaryapp.R
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.fragment_create_diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CreateDiaryFragment : BaseFragment() {
    private var diaryId = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_diary, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = requireArguments().getInt("diaryId", -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (diaryId != -1) {
            GlobalScope.launch {
                val diary = DiaryDatabase.getDatabase(requireContext()).diaryDao()
                    .getSpecificDiary(diaryId)
                val formatter = DateTimeFormatter.ofPattern("dd M, yyyy", Locale.ENGLISH)
                val date: LocalDate = LocalDate.parse(diary.dateTime, formatter)
                val dateOfDiary =
                    date.dayOfMonth.toString() + "/" + date.monthValue + "/" + date.year
                requireActivity().runOnUiThread {
                    edt_title.setText(diary.title)
                    edt_desc.setText(diary.diaryText)
                    tvDateTime.text = dateOfDiary
                }
            }
        }
        ic_done.setOnClickListener {
            if (diaryId != -1) {
                updateDiary()
            } else {
                addDiary()
            }
        }
        ic_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun addDiary() {
        if (edt_title.text.isNullOrEmpty()) {
            Toast.makeText(context, "You forgot the title ", Toast.LENGTH_SHORT).show()
        } else if (edt_desc.text.isNullOrEmpty()) {
            Toast.makeText(context, "You have to write something", Toast.LENGTH_SHORT).show()
        } else {
            launch {
                val diary = Diary()
                diary.title = edt_title.text.toString()
                diary.diaryText = edt_desc.text.toString()
                val currentDate = LocalDate.now()
                val dateOfDiary =
                    currentDate.dayOfMonth.toString() + " " + currentDate.monthValue + ", " + currentDate.year
                diary.dateTime = dateOfDiary
                DiaryDatabase.getDatabase(requireContext()).diaryDao().insertDiary(diary)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun updateDiary() {
        GlobalScope.launch {
            val diary =
                DiaryDatabase.getDatabase(requireContext()).diaryDao().getSpecificDiary(diaryId)
            diary.diaryText = edt_desc.text.toString()
            diary.title = edt_title.text.toString()
            val currentDate = LocalDate.now()
            val dateOfDiary =
                currentDate.dayOfMonth.toString() + " " + currentDate.monthValue + ", " + currentDate.year
            diary.dateTime = dateOfDiary
            DiaryDatabase.getDatabase(requireContext()).diaryDao().updateDiary(diary)
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateDiaryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}