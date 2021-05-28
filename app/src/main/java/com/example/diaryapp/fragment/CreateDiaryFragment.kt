package com.example.diaryapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diaryapp.BaseFragment
import com.example.diaryapp.R
import com.example.diaryapp.database.DiaryDatabase
import kotlinx.android.synthetic.main.fragment_create_diary.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateDiaryFragment : BaseFragment() {
    private var diaryId = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_diary, container, false)
    }
    git config --global user.name "thanhdatt31"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diaryId = requireArguments().getInt("diaryId",-1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(diaryId != -1){
           GlobalScope.launch {
               val diary = DiaryDatabase.getDatabase(requireContext()).diaryDao()
                   .getSpecificDiary(diaryId)
                    edt_title.setText(diary.title)
           }
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