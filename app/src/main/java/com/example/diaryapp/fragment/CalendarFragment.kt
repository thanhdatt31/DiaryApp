package com.example.diaryapp.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import com.example.diaryapp.adapter.CalendarAdapter
import com.example.diaryapp.adapter.DayTitleAdapter
import com.example.diaryapp.model.Calendar
import kotlinx.android.synthetic.main.fragment_calendar2.view.*
import java.time.LocalDate
import java.time.YearMonth


class CalendarFragment(
    var date: LocalDate,
    var dayStart: Int
) : Fragment() {
    private var listTitle: ArrayList<Int> = arrayListOf()
    private lateinit var recyclerViewTitle: RecyclerView
    private lateinit var recyclerViewCalendar: RecyclerView
    private val dayTitleAdapter: DayTitleAdapter = DayTitleAdapter()
    private val calendarAdapter = CalendarAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar2, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listTitle = arrayListOf(0, 1, 2, 3, 4, 5, 6)
        recyclerViewTitle = view.rv_day_title
        recyclerViewTitle.apply {
            layoutManager = GridLayoutManager(context, 7)
            dayTitleAdapter.setData(listTitle)
            adapter = dayTitleAdapter
        }
        val array = setMonthView(date, dayStart)
        recyclerViewCalendar = view.calendarRecyclerView
        recyclerViewCalendar.apply {
            layoutManager = GridLayoutManager(context, 7, GridLayoutManager.VERTICAL, false)
            calendarAdapter.setMonthYear(date.monthValue, date.year)
            calendarAdapter.setData(array)
            calendarAdapter.setOnClickListener(onClicked)
            adapter = calendarAdapter
        }
        view.tvMonth.text = date.month.toString() + " " + date.year
        if (restorePrefData() == "") {
            switchTitle(0)
        } else {
            switchTitle(restorePrefData().toInt())
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun setMonthView(day: LocalDate, start: Int): ArrayList<Calendar> {
        val listCurrent: ArrayList<Calendar> = arrayListOf()
        val listPrevious: ArrayList<Calendar> = arrayListOf()
        val listNext: ArrayList<Calendar> = arrayListOf()
        val yearMonth = YearMonth.from(day)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = day.withDayOfMonth(1)

        var dayOfWeek = firstOfMonth.dayOfWeek.value + start
        if (dayOfWeek > 7) {
            dayOfWeek -= 7
        }
        var previous = previousMonth(day)
        var next = 1
        for (i in 1..42) {
            when {
                i <= dayOfWeek -> {
                    listPrevious.add(Calendar(1, previous, day.monthValue - 1, day.year))
                    previous--
                }
                i > daysInMonth + dayOfWeek -> {
                    listNext.add(Calendar(1, next, day.monthValue + 1, day.year))
                    next++
                }
                else -> {
                    listCurrent.add(Calendar(0, (i - dayOfWeek), day.monthValue, day.year))
                }
            }
        }
        for (i in 0 until listPrevious.size) {
            listCurrent.add(0, listPrevious[i])
        }
        for (i in 0 until listNext.size) {
            listCurrent.add(listCurrent.size, listNext[i])
        }
        return listCurrent
    }


    private fun previousMonth(date: LocalDate): Int {
        val data = date.minusMonths(1)
        val yearMonth = YearMonth.from(data)
        return yearMonth.lengthOfMonth()
    }

    private fun switchTitle(i: Int) {
        when (i) {
            1 -> listTitle = arrayListOf(0, 1, 2, 3, 4, 5, 6)
            2 -> listTitle = arrayListOf(1, 2, 3, 4, 5, 6, 0)
            3 -> listTitle = arrayListOf(2, 3, 4, 5, 6, 0, 1)
            4 -> listTitle = arrayListOf(3, 4, 5, 6, 0, 1, 2)
            5 -> listTitle = arrayListOf(4, 5, 6, 0, 1, 2, 3)
            6 -> listTitle = arrayListOf(5, 6, 0, 1, 2, 3, 4)
            7 -> listTitle = arrayListOf(6, 0, 1, 2, 3, 4, 5)
        }
        dayTitleAdapter.setData(listTitle)

    }

    private fun restorePrefData(): String {
        val pref = requireContext().getSharedPreferences(
            "myPrefs",
            AppCompatActivity.MODE_PRIVATE
        )
        return pref.getString("dayStart", "")!!
    }

    private val onClicked = object : CalendarAdapter.OnItemClickListener {
        override fun onClicked(calendar: Calendar) {
            val intent = Intent("calendar")
            val date: String = calendar.day.toString() + "/" + calendar.month + "/" + calendar.year
            intent.putExtra("date", date)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                intent
            )
        }

        override fun onDoubleClick(calendar: Calendar) {
            val dateTime: String = calendar.day.toString() + " " +
                    calendar.month + ", " + calendar.year
            val fragment: Fragment = CreateDiaryFragment()
            val bundle = Bundle()
            bundle.putString("date_time", dateTime)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack("").commit()
    }
}