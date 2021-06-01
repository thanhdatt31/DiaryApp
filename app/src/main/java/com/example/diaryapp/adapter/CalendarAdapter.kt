package com.example.diaryapp.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
        var bg : ImageView = itemView.findViewById(R.id.img_bg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_calendar, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

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
                    holder.dayOfMonth.setOnClickListener {

                    }
                } else {
                    holder.dayOfMonth.setOnClickListener {
                        listener!!.onClicked(calendar)
                    }
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
    }


}