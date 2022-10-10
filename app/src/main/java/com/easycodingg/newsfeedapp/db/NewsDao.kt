package com.easycodingg.newsfeedapp.db

import androidx.room.*
import androidx.room.Query
import com.easycodingg.newsfeedapp.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreakingNews(breakingNews: List<BreakingNews>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryNews(categoryNews: List<CategoryNews>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchNews(categoryNews: List<SearchNews>)

    @Update
    suspend fun updateArticle(article: Article)

    @Query("SELECT * FROM breaking_news_table INNER JOIN articles ON articleUrl = url")
    fun getSavedBreakingNews(): Flow<List<Article>>

    @Query("SELECT * FROM category_news_table INNER JOIN articles ON articleUrl = url WHERE category = :category")
    fun getSavedCategoryNews(category: String): Flow<List<Article>>

    @Query("SELECT * FROM search_news_table INNER JOIN articles ON articleUrl = url WHERE searchQuery = :searchQuery")
    fun getSavedSearchNews(searchQuery: String): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1")
    fun getAllBookmarkedArticles(): Flow<List<Article>>

    @Query("DELETE FROM breaking_news_table")
    suspend fun deleteAllBreakingNews()

    @Query("DELETE FROM category_news_table WHERE category = :category")
    suspend fun deleteCategoryNewsByCategory(category: String)

    @Query("DELETE FROM search_news_table WHERE searchQuery = :searchQuery")
    suspend fun deleteSearchNewsBySearchQuery(searchQuery: String)

    @Query("DELETE FROM articles WHERE updatedAt < :timeStampInMillis AND isBookmarked = 0")
    suspend fun deleteCachedArticlesOlderThanTimeStamp(timeStampInMillis: Long)

    /* EXPERIMENTAL */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDateNews(dateNews: List<DateNews>)

    @Query("SELECT * FROM date_news_table INNER JOIN articles ON articleUrl = url WHERE date = :date")
    fun getSavedDateNews(date: String): Flow<List<Article>>

    @Query("DELETE FROM date_news_table WHERE date = :date")
    suspend fun deleteDateNewsByDate(date: String)
}