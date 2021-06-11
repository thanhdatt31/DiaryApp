package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import com.example.diaryapp.database.DiaryDatabase
import com.example.diaryapp.model.Calendar
import com.example.diaryapp.model.Diary
import kotlinx.android.synthetic.main.item_calendar.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val THIS_MONTH = 0
    var month: Int = 0
    var year: Int = 0
    var listener: OnItemClickListener? = null
    var listCalendar: ArrayList<Calendar> = arrayListOf()
    private var dateNow: LocalDate = LocalDate.now()
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 500

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == THIS_MONTH) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
            context = parent.context
            MonthHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_2, parent, false)
            context = parent.context
            NextMonthHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listCalendar.size
    }

    override fun getItemViewType(position: Int): Int {
        return listCalendar[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (listCalendar[position].type == THIS_MONTH) {
            (holder as MonthHolder).bind(position)
        } else {
            (holder as NextMonthHolder).bind(position)
        }
    }

    inner class MonthHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayOfMonth: TextView = itemView.cellDayText
        var bg: ImageView = itemView.findViewById(R.id.img_bg)
        @SuppressLint("ClickableViewAccessibility")
        fun bind(position: Int) {
            val calendar = listCalendar[position]
            if (calendar.day == dateNow.dayOfMonth && calendar.month == dateNow.monthValue) {
                dayOfMonth.setTextColor(Color.parseColor("#46c9cc"))
            }
            dayOfMonth.text = calendar.day.toString()
            val date: String = calendar.day.toString() + " " + calendar.month + ", " + calendar.year
            GlobalScope.launch(Dispatchers.IO) {
                val listDiaryByDate = DiaryDatabase.getDatabase(context).diaryDao()
                    .getDiaryByTime(date) as ArrayList<Diary>
                if (listDiaryByDate.size > 0) {
                    bg.visibility = View.VISIBLE
                    bg.setOnTouchListener { v, event ->
                        onDoubleClick(event, calendar)
                        false
                    }
                    bg.setOnLongClickListener {
                        listener!!.onClicked(calendar)
                        false
                    }
                } else {
                    dayOfMonth.setOnTouchListener { v, event ->
                        onDoubleClick(event, calendar)
                        true
                    }
                }
            }
        }
    }

    private fun onDoubleClick(event: MotionEvent, calendar: Calendar) {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                clickCount++
            }
            MotionEvent.ACTION_UP -> {
                val time = System.currentTimeMillis() - startTime
                duration += time
                if (clickCount == 2) {
                    if (duration <= MAX_DURATION) {
                        listener!!.onDoubleClick(calendar)
                    }
                    clickCount = 0
                    duration = 0
                }
            }
        }
    }

    fun setMonthYear(month: Int, year: Int) {
        this.month = month
        this.year = year
        notifyDataSetChanged()
    }


    inner class NextMonthHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayOfMonth: TextView = itemView.cellDayText
        fun bind(position: Int) {
            val item = listCalendar[position]
            dayOfMonth.text = item.day.toString()
        }
    }

    fun setData(data: ArrayList<Calendar>) {
        listCalendar = data
        notifyDataSetChanged()
    }
    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }
    interface OnItemClickListener {
        fun onClicked(calendar: Calendar)
        fun onDoubleClick(calendar: Calendar)
    }
}