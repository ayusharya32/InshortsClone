package com.easycodingg.newsfeedapp.util

import android.util.Patterns

object AuthValidation {

    fun validateName(name: String): Pair<Boolean, String> {
        if(name.length < 4) {
            return Pair(false, "Name must contain more than 4 characters")
        }

        if(name.contains(".*\\d.*".toRegex())) {
            return Pair(false, "Name should not contain any number")
        }

        if(!name.matches("^[A-Za-z]+$".toRegex())) {
            return Pair(false, "Name should only contain alphabets")
        }
        return Pair(true, "")
    }

    fun validateEmail(email: String): Pair<Boolean, String> {
        return if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Pair(true, "")
        } else {
            Pair(false, "Please Enter Valid Email Address")
        }
    }

    fun validatePassword(password: String): Pair<Boolean, String> {
        if(password.length < 4) {
            return Pair(false, "Password must contain more than 4 characters")
        }

        if(!password.contains(".*\\d.*".toRegex())) {
            return Pair(false, "Password should contain a number")
        }

        if(!password.matches("^(?=.*[A-Z]).+$".toRegex())) {
            return Pair(false, "Password should an uppercase alphabet")
        }

        if(!password.matches("^(?=.*[a-z]).+$".toRegex())) {
            return Pair(false, "Password should a lowercase alphabet")
        }
        return Pair(true, "")
    }
}