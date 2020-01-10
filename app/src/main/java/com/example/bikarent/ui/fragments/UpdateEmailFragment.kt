package com.example.bikarent.ui.fragments


import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.example.bikarent.R
import com.example.bikarent.utils.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_update_email.*

class UpdateEmailFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchLayouts(false)

        button_authenticate.setOnClickListener{view ->
            if (!authenticate(view))
                return@setOnClickListener
        }
        button_update.setOnClickListener{ view ->
            if(!updateEmail(view))
                return@setOnClickListener
        }

    }

    private fun updateEmail(view: View):Boolean{
        val email =  edit_text_email.text.toString().trim()

        if (email.isEmpty()) {
            edit_text_email.error = "Email Required"
            edit_text_email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edit_text_email.error = "Valid Email Required"
            edit_text_email.requestFocus()
            return false
        }

        showProgressBar(true)

        currentUser?.let { user ->
            user.updateEmail(email).addOnCompleteListener{
                if (it.isSuccessful){
                    val action  = UpdateEmailFragmentDirections.actionEmailUpdated()
                    Navigation.findNavController(view).navigate(action)
                }
                else {
                    context?.toast(it.exception?.message!!)
                }
            }
        }

        showProgressBar(false)
        return true
    }

    private fun authenticate(view: View):Boolean{
        val password = edit_text_password.text.toString().trim()

        if (password.isEmpty()){
            edit_text_password.error = "Password required!"
            edit_text_password.requestFocus()
            return false
        }

        showProgressBar(true)

        currentUser?.let { user ->
            val credentials = EmailAuthProvider.getCredential(user.email!!,password)
            user.reauthenticate(credentials).addOnCompleteListener{
                when {
                    it.isSuccessful -> {
                        switchLayouts(true)
                    }
                    it.exception is FirebaseAuthInvalidCredentialsException -> {
                        edit_text_password.error="Wrong password"
                        edit_text_password.requestFocus()
                    }
                    else -> context?.toast(it.exception?.message!!)
                }
            }
        }
        showProgressBar(false)
        return true
    }

    private fun showProgressBar(on:Boolean){
        if (on) {
            progressbar_update_email.visibility = View.VISIBLE
        }else {
            progressbar_update_email.visibility= View.GONE
        }
    }

    private fun switchLayouts(passwordLayoutBoolean:Boolean){
        if (passwordLayoutBoolean){
            layoutUpdateEmail.visibility = View.VISIBLE
            layoutPassword.visibility = View.GONE
        }
        else {
            layoutUpdateEmail.visibility = View.GONE
            layoutPassword.visibility = View.VISIBLE
        }
    }
}
