package com.example.diaryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import com.example.diaryapp.model.Diary

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    private var listDiary: ArrayList<Diary> = arrayListOf()
    var listener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tvTitle)
        var dateTime: TextView = itemView.findViewById(R.id.tvDate)
        var desc: TextView = itemView.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_diary_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val diary: Diary = listDiary[position]
        holder.title.text = diary.title
        holder.dateTime.text = diary.dateTime
        holder.desc.text = diary.diaryText
        holder.itemView.setOnClickListener {
            listener!!.onClicked(diary)
        }
    }

    override fun getItemCount(): Int {
        return listDiary.size
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    fun setData(data: List<Diary>) {
        listDiary = data as ArrayList<Diary>
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClicked(diary: Diary)
    }
}