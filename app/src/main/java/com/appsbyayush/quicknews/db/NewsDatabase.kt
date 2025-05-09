package com.appsbyayush.quicknews.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appsbyayush.quicknews.models.*

@Database(
        entities = [Article::class, BreakingNews::class, CategoryNews::class, SearchNews::class, DateNews::class],
        version = 1
)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
}