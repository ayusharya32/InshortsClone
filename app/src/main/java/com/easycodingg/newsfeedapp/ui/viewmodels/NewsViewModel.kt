package com.easycodingg.newsfeedapp.ui.viewmodels

import androidx.lifecycle.*
import com.easycodingg.newsfeedapp.models.Article
import com.easycodingg.newsfeedapp.repo.NewsRepository
import com.easycodingg.newsfeedapp.util.Constants.BOOKMARKS_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.CATEGORY_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.DATE_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Constants.FIRST_PAGE_NUMBER
import com.easycodingg.newsfeedapp.util.Constants.SEARCH_QUERY_TYPE
import com.easycodingg.newsfeedapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
        private val repository: NewsRepository
): ViewModel() {

    var queryType = ""
    var queryValue = ""
    private val pageNumber = MutableLiveData(FIRST_PAGE_NUMBER)

    var articles: LiveData<Resource<List<Article>>> = MutableLiveData()

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    /*Query articles when fragment in foreground according to it's query type*/
    fun onFragmentInForeground()  {
        if(queryType == BOOKMARKS_QUERY_TYPE) {
            getAllBookmarkedArticles()
        } else {
            articles = pageNumber.switchMap { currentPageNumber ->
                when (queryType) {
                    CATEGORY_QUERY_TYPE -> {
                        getCategoryNews(queryValue, currentPageNumber)
                    }
                    SEARCH_QUERY_TYPE -> {
                        getSearchNews(queryValue, currentPageNumber)
                    }
                    DATE_QUERY_TYPE -> {
                        getDateNews(queryValue, currentPageNumber)
                    }
                    else -> getBreakingNews()
                }
            }
        }
    }

    /*Setting same value to LiveData to trigger switchMap*/
    fun onRetryClicked() {
        pageNumber.value = pageNumber.value
    }

    /*Get articles for next page by increasing page number*/
    fun onViewPagerLastItem() {
        if(queryType == SEARCH_QUERY_TYPE || queryType == CATEGORY_QUERY_TYPE) {
            pageNumber.postValue(pageNumber.value?.inc())
        }
    }

    /*Update article with new bookmark value*/
    fun onBookmarkButtonClicked(article: Article) = viewModelScope.launch {
        val updatedArticle = article.copy(isBookmarked = !article.isBookmarked)
        repository.updateArticle(updatedArticle)
    }

    private fun getAllBookmarkedArticles() = viewModelScope.launch {
        articles = repository.getAllBookmarkedArticles().asLiveData(viewModelScope.coroutineContext)
    }

    private fun getBreakingNews(): LiveData<Resource<List<Article>>> =
            repository.getBreakingNews(
                    onFetchFailed = {
                        sendErrorMessageEvent(it)
                    }
            ).asLiveData(viewModelScope.coroutineContext)

    private fun getCategoryNews(
            category: String,
            pageNumber: Int
    ): LiveData<Resource<List<Article>>> =
            repository.getCategoryNews(
                    category = category,
                    pageNumber = pageNumber,
                    onFetchFailed = {
                        sendErrorMessageEvent(it)
                    }
        ).asLiveData(viewModelScope.coroutineContext)

    private fun getSearchNews(
        searchQuery: String,
        pageNumber: Int
    ): LiveData<Resource<List<Article>>> =
            repository.getSearchNews(
                    searchQuery = searchQuery,
                    pageNumber = pageNumber,
                    onFetchFailed = {
                        sendErrorMessageEvent(it)
                    }
            ).asLiveData(viewModelScope.coroutineContext)

    private fun getDateNews(
        date: String,
        pageNumber: Int
    ): LiveData<Resource<List<Article>>> =
        repository.getDateNews(
            date = date,
            pageNumber = pageNumber,
            onFetchFailed = {
                sendErrorMessageEvent(it)
            }
        ).asLiveData(viewModelScope.coroutineContext)

    private fun sendErrorMessageEvent(t: Throwable) = viewModelScope.launch {
        eventChannel.send(Event.ShowErrorMessage(t))
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable): Event()
    }
}