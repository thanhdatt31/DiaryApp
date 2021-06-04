package com.example.diaryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var listCalendar: ArrayList<Calendar> = arrayListOf()
    var listener: OnItemClickListener? = null
    private lateinit var context: Context
    private var clickCount = 0
    private var startTime: Long = 0
    private var duration: Long = 0
    private val MAX_DURATION = 500

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
        var bg: ImageView = itemView.findViewById(R.id.img_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_calendar, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendar: Calendar = listCalendar[position]
        if (calendar.day == 0) {
            holder.dayOfMonth.text = ""
            holder.itemView.setOnClickListener {
            }
        } else {
            holder.dayOfMonth.text = calendar.day.toString()
            val date: String = calendar.day.toString() + " " + calendar.month + ", " + calendar.year
            GlobalScope.launch(Dispatchers.Main) {
                val listDiaryByDate = DiaryDatabase.getDatabase(context).diaryDao()
                    .getDiaryByTime(date) as ArrayList<Diary>
                if (listDiaryByDate.size > 0) {
                    holder.bg.visibility = View.VISIBLE
                    holder.bg.setOnTouchListener { v, event ->
                        onDoubleClick(event, calendar)
                        false
                    }
                    holder.bg.setOnLongClickListener {
                        listener!!.onClicked(calendar)
                        false
                    }

                } else {
                    holder.dayOfMonth.setOnTouchListener { v, event ->
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

    override fun getItemCount(): Int {
        return listCalendar.size
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    fun setData(data: ArrayList<Calendar>) {
        listCalendar = data
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClicked(calendar: Calendar)
        fun onDoubleClick(calendar: Calendar)
    }


}