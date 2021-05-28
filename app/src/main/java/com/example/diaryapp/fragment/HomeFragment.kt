package com.example.diaryapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.BaseFragment
import com.example.diaryapp.R
import com.example.diaryapp.SwipeToDel
import com.example.diaryapp.adapter.DiaryAdapter
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


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
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

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
        val swipeDelete = object : SwipeToDel() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                diaryAdapter.delItem(viewHolder.adapterPosition)
            }

        }
        val touchHelper = ItemTouchHelper(swipeDelete)
        touchHelper.attachToRecyclerView(recyclerView)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_toolbar, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calendar -> {
                Toast.makeText(context, "Alo", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}