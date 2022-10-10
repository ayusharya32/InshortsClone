package com.easycodingg.newsfeedapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var newsPrefs: List<String> = listOf("Cryptocurrency", "Cricket", "Music", "Hollywood")
): Parcelable
