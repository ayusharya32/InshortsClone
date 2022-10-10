package com.easycodingg.newsfeedapp.models

import com.easycodingg.newsfeedapp.util.Constants.NO_IMAGE

data class Query(
    val name: String,
    val queryType: String,
    val queryValue: String,
    val queryImage: Int = NO_IMAGE
)