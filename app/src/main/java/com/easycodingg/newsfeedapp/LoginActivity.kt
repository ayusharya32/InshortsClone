package com.easycodingg.newsfeedapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.easycodingg.newsfeedapp.databinding.ActivityLoginBinding
import com.easycodingg.newsfeedapp.util.AuthValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.apply {
            btnLogin.setOnClickListener {
                email = etEmail.text.trim().toString()
                password = etPassword.text.trim().toString()

                val userCredentialsValid = validateUserCredentials()

                if(userCredentialsValid) {
                    attemptLogin()
                }
            }

            tvNewUser.setOnClickListener {
                Intent(this@LoginActivity, SignupActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun validateUserCredentials(): Boolean {
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

    private fun attemptLogin() {
        binding.pbLogin.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                binding.pbLogin.visibility = View.GONE
                closeKeyboard()
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_SHORT).show()
                Intent(this, NewsTypeSelectionActivity::class.java).also {
                    startActivity(it)
                }
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