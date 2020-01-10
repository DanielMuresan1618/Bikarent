package com.example.bikarent.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bikarent.R
import com.example.bikarent.utils.login
import com.example.bikarent.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_login.edit_text_password
import kotlinx.android.synthetic.main.activity_login.progressbar
import kotlinx.android.synthetic.main.activity_login.text_email
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        button_register.setOnClickListener {view ->
            if (!verifyInputThenRegister()){
                return@setOnClickListener
            }
        }

        text_view_login.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun verifyInputThenRegister():Boolean{
        val email = text_email.text.toString().trim()
        val password = edit_text_password.text.toString().trim()
        val name = edit_text_name.text.toString().trim()

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

        if (password.isEmpty() || password.length < 6) {
            edit_text_password.error = "6 char password required"
            edit_text_password.requestFocus()
            return false
        }

        if (name.isEmpty()){
            edit_text_name.error = "Name Required"
            edit_text_name.requestFocus()
            return false
        }
        registerUser(email, name, password)
        return true
    }

    private fun registerUser(email: String,name: String, password: String) {
        showProgressBar(true)

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    val user= com.example.bikarent.models.User(email=email, username = name,user_id = uid, avatar = null)
                    val settings = FirebaseFirestoreSettings.Builder().build()
                    mDb.firestoreSettings = settings

                    val newUserRef = mDb
                        .collection(getString(R.string.collection_users))
                        .document(uid)

                    newUserRef.set(user).addOnCompleteListener(this){
                        if (it.isSuccessful){
                            showProgressBar(false)
                            toast("Succesfully registered")
                            login()

                        }else {
                            task.exception?.message?.let {text ->
                                toast(text)
                            }
                        }
                    }
                } else {
                    task.exception?.message?.let {
                        toast(it)
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            login()
        }
    }

    private fun showProgressBar(on:Boolean){
        if(on){
            progressbar.visibility = View.VISIBLE
        }
        else{
            progressbar.visibility = View.GONE
        }
    }
}
