package com.example.diaryapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.BaseFragment
import com.example.diaryapp.R
import com.example.diaryapp.adapter.DiaryAdapter
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : BaseFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var listDiary: ArrayList<Diary> = arrayListOf()
    private val diaryAdapter: DiaryAdapter = DiaryAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = recycler_view
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        GlobalScope.launch {
            val diary = DiaryDatabase.getDatabase(requireContext()).diaryDao().getAllDiary()
            diaryAdapter.setData(diary)
            listDiary = diary as ArrayList<Diary>
            recyclerView.adapter = diaryAdapter

        }
        diaryAdapter.setOnClickListener(onClicked)

        //
        fabBtnCreateDiary.setOnClickListener {
            replaceFragment(CreateDiaryFragment.newInstance())
        }
        //
        searchView = view.search_view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val listDiaryTemp: ArrayList<Diary> = arrayListOf()
                for (diary in listDiary) {
                    if (diary.title!!.lowercase(Locale.getDefault()).contains(newText.toString()) ||
                        diary.diaryText!!.lowercase(Locale.getDefault())
                            .contains(newText.toString())
                    ) {
                        listDiaryTemp.add(diary)
                    }
                }
                diaryAdapter.setData(listDiaryTemp)
                diaryAdapter.notifyDataSetChanged()
                return true
            }

        })

    }

    private val onClicked = object : DiaryAdapter.OnItemClickListener {
        override fun onClicked(diary: Diary) {
            val fragment: Fragment = CreateDiaryFragment()
            val bundle = Bundle()
            bundle.putInt("diaryId", diary.id!!)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack("").commit()
    }


}