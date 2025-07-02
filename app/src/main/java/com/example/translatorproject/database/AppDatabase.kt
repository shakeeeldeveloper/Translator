package com.example.translatorproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.translatorproject.dao.TranslationDao
import com.example.translatorproject.model.BookmarkEntity
import com.example.translatorproject.model.HistoryEntity

@Database(entities = [BookmarkEntity::class, HistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "translator_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
