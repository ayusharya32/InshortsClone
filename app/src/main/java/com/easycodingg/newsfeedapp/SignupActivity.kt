package com.easycodingg.newsfeedapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easycodingg.newsfeedapp.databinding.ActivitySignupBinding
import com.easycodingg.newsfeedapp.models.User
import com.easycodingg.newsfeedapp.util.AuthValidation
import com.easycodingg.newsfeedapp.util.Constants.USERS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SignupActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    private val usersRef = Firebase.firestore.collection(USERS_COLLECTION)
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {

            btnSignUp.setOnClickListener {
                firstName = etFirstName.text.trim().toString()
                lastName = etLastName.text.trim().toString()
                email = etEmail.text.trim().toString()
                password = etPassword.text.trim().toString()

                val userCredentialsValid = validateUserCredentials()

                if(userCredentialsValid) {
                    attemptRegistration()
                }
            }
        }
    }

    private fun validateUserCredentials(): Boolean {

        val (isFirstNameValid, errorFirstName) = AuthValidation.validateName(firstName)
        if(!isFirstNameValid) {
            Toast.makeText(this, errorFirstName, Toast.LENGTH_SHORT).show()
            return false
        }

        val (isLastNameValid, errorLastName) = AuthValidation.validateName(lastName)
        if(!isLastNameValid) {
            Toast.makeText(this, errorLastName, Toast.LENGTH_SHORT).show()
            return false
        }

        val (isEmailNameValid, errorEmail) = AuthValidation.validateEmail(email)
        if(!isEmailNameValid) {
            Toast.makeText(this, errorEmail, Toast.LENGTH_SHORT).show()
            return false
        }

        val (isPasswordNameValid, errorPassword) = AuthValidation.validatePassword(password)
        if(!isPasswordNameValid) {
            Toast.makeText(this, errorPassword, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun attemptRegistration() {
        binding.pbSignup.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                binding.pbSignup.visibility = View.GONE
                closeKeyboard()
            }
            .addOnSuccessListener {
                addUserDetails()
                Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun addUserDetails() {
        val user = User(firstName, lastName, email)

        /*usersRef.add(user)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }*/

        usersRef.document(email).set(user)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus

        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
