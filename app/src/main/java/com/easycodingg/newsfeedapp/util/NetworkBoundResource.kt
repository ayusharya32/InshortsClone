package com.easycodingg.newsfeedapp.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
        crossinline query: () -> Flow<ResultType>,
        crossinline fetch: suspend () -> RequestType,
        crossinline saveFetchResult: suspend (RequestType) -> Unit,
        crossinline onFetchFailed: (Throwable) -> Unit = { },
        crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {

    val data = query().first()

    val flow = if(shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            query().map { Resource.Success(it) }

        } catch(throwable: Throwable) {
            onFetchFailed(throwable)
            query().map {
                Resource.Error(throwable, it)
            }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}