package com.easycodingg.newsfeedapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles"
)
data class Article(
    val title: String,
    val content: String?,
    val description: String?,
    @PrimaryKey
    val url: String,
    val imageUrl: String?,
    val sourceName: String?,
    val isBookmarked: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "category_news_table"
)
data class CategoryNews(
    val category: String,
    @PrimaryKey
    val articleUrl: String
)

@Entity(
    tableName = "breaking_news_table"
)
data class BreakingNews(
    val articleUrl: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)

@Entity(
    tableName = "search_news_table"
)
data class SearchNews(
    val searchQuery: String,
    @PrimaryKey
    val articleUrl: String,
)

/* EXPERIMENTAL */
@Entity(
    tableName = "date_news_table"
)
data class DateNews(
    val date: String,
    @PrimaryKey
    val articleUrl: String,
)