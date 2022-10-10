package com.easycodingg.newsfeedapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easycodingg.newsfeedapp.R
import com.easycodingg.newsfeedapp.models.Query
import com.easycodingg.newsfeedapp.repo.NewsRepository
import com.easycodingg.newsfeedapp.util.Constants.BOOKMARKS_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.CATEGORY_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.COUNTRY_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.NO_IMAGE
import com.easycodingg.newsfeedapp.util.Constants.SEARCH_QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    var queryList: List<Query> = listOf()

    init {
        queryList = getQueriesList()


        viewModelScope.launch {
            repository.deleteCachedArticlesOlderThanTimeStamp(
                    timeStampInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            )
        }
    }

    private fun getQueriesList() =
            listOf(
                    Query("Business", CATEGORY_QUERY_TYPE, "business"),
                    Query("Entertainment", CATEGORY_QUERY_TYPE, "entertainment"),
                    Query("Health", CATEGORY_QUERY_TYPE, "health"),
                    Query("Sports", CATEGORY_QUERY_TYPE, "sports"),
                    Query("Technology", CATEGORY_QUERY_TYPE, "technology"),
                    Query("Science", CATEGORY_QUERY_TYPE, "science")
            )

    fun getNewsTypesList(userFeedQuery: String) =
            listOf(
                    Query("My Feed", SEARCH_QUERY_TYPE, userFeedQuery, R.drawable.ic_feed),
                    Query("Trending", COUNTRY_QUERY_TYPE, "in", R.drawable.ic_trending),
                    Query("Bookmarked", BOOKMARKS_QUERY_TYPE, "bookmarks", R.drawable.ic_bookmarks)
            )
}