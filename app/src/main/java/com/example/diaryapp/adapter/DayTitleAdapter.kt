package com.example.diaryapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R
import java.util.ArrayList

class DayTitleAdapter : RecyclerView.Adapter<DayTitleAdapter.ViewHolder>() {
    private var listTitle: ArrayList<Int> = arrayListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayTitle: TextView = itemView.findViewById(R.id.tv_day_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_day_title, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (listTitle[position]) {
            0 -> holder.dayTitle.text = "MON"
            1 -> holder.dayTitle.text = "TUE"
            2 -> holder.dayTitle.text = "WED"
            3 -> holder.dayTitle.text = "THU"
            4 -> holder.dayTitle.text = "FRI"
            5 -> holder.dayTitle.text = "SAT"
            6 -> {
                holder.dayTitle.text = "SUN"
            holder.dayTitle.setTextColor(Color.parseColor("#E80505"))}

        }
    }

    override fun getItemCount(): Int {
        return listTitle.size
    }

    fun setData(data: ArrayList<Int>) {
        listTitle = data
        notifyDataSetChanged()

    }
}