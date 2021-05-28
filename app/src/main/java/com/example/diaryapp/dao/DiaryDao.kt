package com.example.diaryapp.dao

import androidx.room.*
import com.example.diaryapp.model.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary ORDER BY id DESC")
    suspend fun getAllDiary(): List<Diary>

    @Query("SELECT * FROM diary WHERE id =:id")
    suspend fun getSpecificDiary(id: Int): Diary

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(diary: Diary)

    @Delete
    suspend fun deleteNote(diary: Diary)

    @Query("DELETE FROM diary WHERE id =:id")
    suspend fun deleteSpecificNote(id: Int)

    @Update
    suspend fun updateNote(diary: Diary)
}