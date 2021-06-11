package com.example.diaryapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.diaryapp.R
import com.example.diaryapp.adapter.ViewPageAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import java.time.LocalDate


class BottomSheetFragment : BottomSheetDialogFragment() {
    var startOfWeek: Int = 0
    var size = 2000
    var selectDate: LocalDate = LocalDate.now()
    lateinit var viewPageAdapter: ViewPageAdapter
    var listFragment = MutableList(size) { index -> Fragment() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFragment[size / 2 - 1] = CalendarFragment(selectDate.minusMonths(1), startOfWeek)
        listFragment[size / 2] = CalendarFragment(selectDate, startOfWeek)
        listFragment[size / 2 + 1] = CalendarFragment(selectDate.plusMonths(1), startOfWeek)

        viewPageAdapter = ViewPageAdapter(listFragment, childFragmentManager, lifecycle)
        view_pager2.adapter = viewPageAdapter
        view_pager2.setCurrentItem(size / 2, false)
        view_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var countLeft = 2
            var countRight = 2
            var jump = size / 2
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position < jump) {
                    viewPageAdapter.reloadFragment(jump)
                    jump = position
                    viewPageAdapter.setLeft(
                        CalendarFragment(
                            selectDate.minusMonths(countLeft.toLong()), startOfWeek
                        ), size / 2 - countLeft
                    )
                    countLeft++
                }

                if (position > jump) {
                    viewPageAdapter.reloadFragment(jump)
                    jump = position
                    viewPageAdapter.setRight(
                        CalendarFragment(
                            selectDate.plusMonths(countRight.toLong()), startOfWeek
                        ), size / 2 + countRight
                    )
                    countRight++
                }
            }
        })
        if (restorePrefData() == "") {
            change(6)
        } else {
            when (restorePrefData().toInt()) {
                1 -> change(6)
                2 -> change(5)
                3 -> change(4)
                4 -> change(3)
                5 -> change(2)
                6 -> change(1)
            }
        }
    }

    private fun change(startOfWeek: Int) {
        val listFragmentNew = MutableList(size) { index -> Fragment() }
        listFragmentNew[size / 2 - 1] = CalendarFragment(selectDate.minusMonths(1), startOfWeek)
        listFragmentNew[size / 2] = CalendarFragment(selectDate, startOfWeek)
        listFragmentNew[size / 2 + 1] = CalendarFragment(selectDate.plusMonths(1), startOfWeek)
        viewPageAdapter = ViewPageAdapter(listFragmentNew, childFragmentManager, lifecycle)
        view_pager2.adapter = viewPageAdapter
        view_pager2.setCurrentItem(size / 2, false)
        viewPageAdapter.notifyDataSetChanged()

    }

    private fun restorePrefData(): String {
        val pref = requireContext().getSharedPreferences(
            "myPrefs",
            AppCompatActivity.MODE_PRIVATE
        )
        return pref.getString("dayStart", "")!!
    }
}
