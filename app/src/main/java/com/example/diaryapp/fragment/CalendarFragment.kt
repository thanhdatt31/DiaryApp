package com.example.diaryapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import com.example.diaryapp.adapter.CalendarAdapter
import com.example.diaryapp.model.Calendar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class CalendarFragment : BottomSheetDialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private var calendarAdapter = CalendarAdapter()
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.recycler_view_calendar
        recyclerView.apply {
            view.tv_month_title.text = monthYearFromDate(selectedDate)
            layoutManager = GridLayoutManager(context, 7)
            val listDay = daysInMonthArray(selectedDate)
            calendarAdapter.setData(listDay)
            adapter = calendarAdapter
            calendarAdapter.setOnClickListener(onClicked)
        }
        ic_back.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            view.tv_month_title.text = monthYearFromDate(selectedDate)
            val listDay = daysInMonthArray(selectedDate)
            calendarAdapter.setData(listDay)
        }
        ic_forward.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            view.tv_month_title.text = monthYearFromDate(selectedDate)
            val listDay = daysInMonthArray(selectedDate)
            calendarAdapter.setData(listDay)
        }
        ic_home.setOnClickListener {
            selectedDate = LocalDate.now()
            view.tv_month_title.text = monthYearFromDate(selectedDate)
            val listDay = daysInMonthArray(selectedDate)
            calendarAdapter.setData(listDay)
        }


        super.onViewCreated(view, savedInstanceState)
    }


    private fun daysInMonthArray(date: LocalDate): ArrayList<Calendar> {
        val daysInMonthArray = ArrayList<Calendar>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 2..43) {
            if (i < dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(Calendar(0, 0, 0))
            } else {
                daysInMonthArray.add(
                    Calendar(
                        (i - dayOfWeek),
                        selectedDate.monthValue,
                        selectedDate.year
                    )
                )
            }
        }
        return daysInMonthArray
    }

    private val onClicked = object : CalendarAdapter.OnItemClickListener {
        override fun onClicked(calendar: Calendar) {
            val intent = Intent("calendar")
            val date: String = calendar.day.toString() + "/" + calendar.month + "/" + calendar.year
            intent.putExtra("date", date)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(
                intent
            )
            dismiss()
        }

        override fun onDoubleClick(calendar: Calendar) {
            dismiss()
            val dateTime: String = calendar.day.toString() + " " +
                    calendar.month + ", " + calendar.year
            val fragment: Fragment = CreateDiaryFragment()
            val bundle = Bundle()
            bundle.putString("date_time", dateTime)
            fragment.arguments = bundle
            replaceFragment(fragment)
        }


    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM, yyyy")
        return date.format(formatter)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack("").commit()
    }

}