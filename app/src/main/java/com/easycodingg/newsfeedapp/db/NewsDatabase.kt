package com.easycodingg.newsfeedapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.easycodingg.newsfeedapp.models.*

@Database(
        entities = [Article::class, BreakingNews::class, CategoryNews::class, SearchNews::class, DateNews::class],
        version = 1
)
abstract class NewsDatabase: RoomDatabase() {

    abstract fun getNewsDao(): NewsDao
}