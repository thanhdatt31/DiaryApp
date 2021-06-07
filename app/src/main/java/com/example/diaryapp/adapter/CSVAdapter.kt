package com.example.diaryapp.adapter

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaryapp.R


class CSVAdapter : RecyclerView.Adapter<CSVAdapter.ViewHolder>() {
    private lateinit var context: Context
    private var listUri: ArrayList<Uri> = arrayListOf()
    var listener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_csv, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri: Uri = listUri[position]
        holder.title.text = getFileName(uri)
        holder.itemView.setOnClickListener {
            listener!!.onClicked(uri)
        }
    }

    override fun getItemCount(): Int {
        return listUri.size
    }

    fun setData(data: ArrayList<Uri>) {
        listUri = data
        notifyDataSetChanged()
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    interface OnItemClickListener {
        fun onClicked(uri: Uri)
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }
}