package com.easycodingg.newsfeedapp.api

import com.easycodingg.newsfeedapp.models.NewsResponse
import com.easycodingg.newsfeedapp.util.Constants.API_KEY
import com.easycodingg.newsfeedapp.util.Constants.CATEGORY_GENERAL
import com.easycodingg.newsfeedapp.util.Constants.COUNTRY_CODE_INDIA
import com.easycodingg.newsfeedapp.util.Constants.DEFAULT_PAGE_SIZE
import com.easycodingg.newsfeedapp.util.Constants.FIRST_PAGE_NUMBER
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @Headers("X-Api-Key: $API_KEY")
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
            @Query("country") countryCode: String = COUNTRY_CODE_INDIA,
            @Query("category") category: String = CATEGORY_GENERAL,
            @Query("page") page: Int = FIRST_PAGE_NUMBER,
            @Query("pageSize") pageSize: Int = DEFAULT_PAGE_SIZE
    ): NewsResponse

    @Headers("X-Api-Key: $API_KEY")
    @GET("v2/everything")
    suspend fun searchNews(
            @Query("q") query: String,
            @Query("page") page: Int = FIRST_PAGE_NUMBER,
            @Query("pageSize") pageSize: Int = DEFAULT_PAGE_SIZE
    ): NewsResponse

    /* EXPERIMENTAL */
    @Headers("X-Api-Key: $API_KEY")
    @GET("v2/everything")
    suspend fun searchNewsByDate(
            @Query("q") query: String = "india",
            @Query("from") from: String,
            @Query("to") to: String,
            @Query("page") page: Int = FIRST_PAGE_NUMBER,
            @Query("pageSize") pageSize: Int = DEFAULT_PAGE_SIZE
    ): NewsResponse

}