package com.easycodingg.newsfeedapp.di

import android.content.Context
import androidx.room.Room
import com.easycodingg.newsfeedapp.api.NewsApi
import com.easycodingg.newsfeedapp.db.NewsDatabase
import com.easycodingg.newsfeedapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
            client: OkHttpClient
    ): Retrofit = Retrofit.Builder().apply {
        baseUrl(BASE_URL)
        addConverterFactory(GsonConverterFactory.create())
        client(client)
    }.build()

    @Singleton
    @Provides
    fun provideNewsApi(retrofit: Retrofit): NewsApi =
            retrofit.create(NewsApi::class.java)

    @Singleton
    @Provides
    fun provideNewsDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, NewsDatabase::class.java, "newsDB")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideNewsDao(db: NewsDatabase) = db.getNewsDao()
}