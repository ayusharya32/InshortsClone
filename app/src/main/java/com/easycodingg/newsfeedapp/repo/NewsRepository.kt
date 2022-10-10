package com.easycodingg.newsfeedapp.repo

import androidx.room.withTransaction
import com.easycodingg.newsfeedapp.api.NewsApi
import com.easycodingg.newsfeedapp.db.NewsDatabase
import com.easycodingg.newsfeedapp.models.*
import com.easycodingg.newsfeedapp.util.Constants.FIRST_PAGE_NUMBER
import com.easycodingg.newsfeedapp.util.Resource
import com.easycodingg.newsfeedapp.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepository @Inject constructor(
        private val newsApi: NewsApi,
        private val newsDatabase: NewsDatabase,
) {

    private val newsDao = newsDatabase.getNewsDao()

    fun getBreakingNews(
            onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Article>>> =
            networkBoundResource(
                    query = {
                        newsDao.getSavedBreakingNews()
                    },
                    fetch = {
                        val newsResponse = newsApi.getBreakingNews()
                        newsResponse.articles
                    },
                    saveFetchResult = { updatedBreakingNewsArticles ->
                        val bookmarkedArticles = newsDao.getAllBookmarkedArticles().first()

                        val breakingNewsArticles = updatedBreakingNewsArticles.map { newsArticle ->
                            val isBookmarked = bookmarkedArticles.any { bookmarkedArticle ->
                                bookmarkedArticle.url == newsArticle.url
                            }

                            Article(
                                    title = newsArticle.title,
                                    description = newsArticle.description,
                                    content = newsArticle.content,
                                    imageUrl = newsArticle.urlToImage,
                                    url = newsArticle.url,
                                    sourceName = newsArticle.source?.name,
                                    isBookmarked = isBookmarked
                            )
                        }

                        newsDatabase.withTransaction {
                            val breakingNews = breakingNewsArticles.map { article ->
                                BreakingNews(article.url)
                            }
                            newsDao.deleteAllBreakingNews()
                            newsDao.insertArticles(breakingNewsArticles)
                            newsDao.insertBreakingNews(breakingNews)
                        }
                    },
                    shouldFetch = { cachedBreakingNews ->
                        shouldRefresh(cachedBreakingNews)
                    },
                    onFetchFailed = onFetchFailed
            )

    fun getCategoryNews(
            category: String,
            pageNumber: Int,
            onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Article>>> {

        return networkBoundResource(
                query = {
                    newsDao.getSavedCategoryNews(category)
                },
                fetch = {
                    val response = newsApi.getBreakingNews(category = category, page = pageNumber)
                    response.articles
                },
                saveFetchResult = { updatedCategoryNewsArticles ->
                    val bookmarkedArticles = newsDao.getAllBookmarkedArticles().first()

                    val categoryNewsArticles = updatedCategoryNewsArticles.map { newsArticle ->
                        val isBookmarked = bookmarkedArticles.any { article ->
                            article.url == newsArticle.url
                        }

                        Article(
                                title = newsArticle.title,
                                description = newsArticle.description,
                                content = newsArticle.content,
                                imageUrl = newsArticle.urlToImage,
                                url = newsArticle.url,
                                sourceName = newsArticle.source?.name,
                                isBookmarked = isBookmarked
                        )
                    }

                    newsDatabase.withTransaction {
                        val categoryNews = categoryNewsArticles.map { article ->
                            CategoryNews(category, article.url)
                        }

                        if(pageNumber == FIRST_PAGE_NUMBER) {
                            newsDao.deleteCategoryNewsByCategory(category)
                        }

                        newsDao.insertArticles(categoryNewsArticles)
                        newsDao.insertCategoryNews(categoryNews)
                    }
                },
                shouldFetch = { cachedCategoryNewsList ->
                    if(pageNumber > 1) {
                        true
                    } else {
                        shouldRefresh(cachedCategoryNewsList)
                    }
                },
                onFetchFailed = onFetchFailed
        )
    }

    fun getSearchNews(
            searchQuery: String,
            pageNumber: Int,
            onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Article>>> =
            networkBoundResource(
                    query = {
                        newsDao.getSavedSearchNews(searchQuery)
                    },
                    fetch = {
                        val response = newsApi.searchNews(query = searchQuery)
                        response.articles
                    },
                    saveFetchResult = { updatedSearchNewsArticles ->
                        val bookmarkedArticles = newsDao.getAllBookmarkedArticles().first()

                        val searchNewsArticles = updatedSearchNewsArticles.map { newsArticle ->
                            val isBookmarked = bookmarkedArticles.any { article ->
                                article.url == newsArticle.url
                            }

                            Article(
                                    title = newsArticle.title,
                                    description = newsArticle.description,
                                    content = newsArticle.content,
                                    imageUrl = newsArticle.urlToImage,
                                    url = newsArticle.url,
                                    sourceName = newsArticle.source?.name,
                                    isBookmarked = isBookmarked
                            )
                        }

                        newsDatabase.withTransaction {
                            val searchNews = searchNewsArticles.map { article ->
                                SearchNews(searchQuery, article.url)
                            }

                            if(pageNumber == FIRST_PAGE_NUMBER) {
                                newsDao.deleteSearchNewsBySearchQuery(searchQuery)
                            }

                            newsDao.insertArticles(searchNewsArticles)
                            newsDao.insertSearchNews(searchNews)
                        }
                    },
                    shouldFetch = { cachedSearchNewsList ->
                        if(pageNumber > 1) {
                            true
                        } else {
                            shouldRefresh(cachedSearchNewsList)
                        }
                    },
                    onFetchFailed = onFetchFailed
            )

    fun getAllBookmarkedArticles(): Flow<Resource<List<Article>>> =
            newsDao.getAllBookmarkedArticles().map {
                Resource.Success(it)
            }

    suspend fun deleteCachedArticlesOlderThanTimeStamp(timeStampInMillis: Long) =
            newsDao.deleteCachedArticlesOlderThanTimeStamp(timeStampInMillis)

    suspend fun updateArticle(article: Article) = newsDao.updateArticle(article)

    private fun shouldRefresh(cachedNewsList: List<Article>): Boolean {
        if(cachedNewsList.firstOrNull() == null) {
            return true
        }
        val sortedCachedBreakingNews = cachedNewsList.sortedBy { article ->
            article.updatedAt
        }
        val oldestTimeStamp = sortedCachedBreakingNews.firstOrNull()?.updatedAt

        return oldestTimeStamp == null ||
                TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - oldestTimeStamp)) > 5
    }

    /* EXPERIMENTAL */
    fun getDateNews(
        date: String,
        pageNumber: Int,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Article>>> =
        networkBoundResource(
            query = {
                newsDao.getSavedDateNews(date)
            },
            fetch = {

                val temp = date.split("N")
                val fromDate = temp[0]
                val toDate = temp[1]

                Timber.d("From: $fromDate, To: $toDate")

                val response = newsApi.searchNewsByDate(from = fromDate, to = toDate)
                response.articles
            },
            saveFetchResult = { updatedDateNewsArticles ->
                val bookmarkedArticles = newsDao.getAllBookmarkedArticles().first()

                val dateNewsArticles = updatedDateNewsArticles.map { newsArticle ->
                    val isBookmarked = bookmarkedArticles.any { article ->
                        article.url == newsArticle.url
                    }

                    Article(
                        title = newsArticle.title,
                        description = newsArticle.description,
                        content = newsArticle.content,
                        imageUrl = newsArticle.urlToImage,
                        url = newsArticle.url,
                        sourceName = newsArticle.source?.name,
                        isBookmarked = isBookmarked
                    )
                }

                newsDatabase.withTransaction {
                    val dateNews = dateNewsArticles.map { article ->
                        DateNews(date, article.url)
                    }

                    if(pageNumber == FIRST_PAGE_NUMBER) {
                        newsDao.deleteDateNewsByDate(date)
                    }

                    newsDao.insertArticles(dateNewsArticles)
                    newsDao.insertDateNews(dateNews)
                }
            },
            shouldFetch = { cachedDateNewsList ->
                if(pageNumber > 1) {
                    true
                } else {
                    shouldRefresh(cachedDateNewsList)
                }
            },
            onFetchFailed = onFetchFailed
        )
}