package com.example.bikarent.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.bikarent.R
import com.example.bikarent.utils.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        button_reset_password.setOnClickListener {
            val email = text_email.text.toString().trim()
            if (!validateEmail(email)){
                return@setOnClickListener
            }
            sendPasswordResetMail(email)
        }
    }

    private fun sendPasswordResetMail(email: String) {
        showProgressBar(true)
        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showProgressBar(false)
                if (task.isSuccessful) {
                    this.toast("Check your email")
                } else {
                    this.toast(task.exception?.message!!)
                }
            }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            text_email.error = "Email Required"
            text_email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            text_email.error = "Valid Email Required"
            text_email.requestFocus()
            return false
        }
        return true
    }

    private fun showProgressBar(on:Boolean) {
        if(on) {
            progressbar.visibility = View.VISIBLE
        }else{
            progressbar.visibility = View.GONE
        }
    }

}
