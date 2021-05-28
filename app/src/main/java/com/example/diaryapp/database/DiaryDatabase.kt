package com.example.diaryapp.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.diaryapp.dao.DiaryDao
import com.example.diaryapp.model.Diary

@Database(entities = [Diary::class], version = 1, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {
    companion object {
       var diaryDatabase: DiaryDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): DiaryDatabase {
            if (diaryDatabase == null) {
                diaryDatabase = Room.databaseBuilder(context.applicationContext, DiaryDatabase::class.java, "diary.db").build()
            }
            return diaryDatabase!!
        }
    }
    abstract fun diaryDao(): DiaryDao

}