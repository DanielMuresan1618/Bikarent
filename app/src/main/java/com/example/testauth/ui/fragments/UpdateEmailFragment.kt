package com.example.testauth.ui.fragments


import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.example.testauth.R
import com.example.testauth.utils.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_update_email.*
import kotlinx.android.synthetic.main.fragment_update_email.edit_text_password
import kotlinx.android.synthetic.main.activity_login.progressbar as progressbar1

class UpdateEmailFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutUpdateEmail.visibility = View.GONE
        layoutPassword.visibility = View.VISIBLE

        button_authenticate.setOnClickListener{
            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()){
                edit_text_password.error = "Password required!"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            progressbar_update_email.visibility= View.VISIBLE

            currentUser?.let { user ->
                val credentials = EmailAuthProvider.getCredential(user.email!!,password)
                user.reauthenticate(credentials).addOnCompleteListener{
                    when {
                        it.isSuccessful -> {
                            layoutUpdateEmail.visibility = View.VISIBLE
                            layoutPassword.visibility = View.GONE
                        }
                        it.exception is FirebaseAuthInvalidCredentialsException -> {
                            edit_text_password.error="Wrong password"
                            edit_text_password.requestFocus()
                        }
                        else -> context?.toast(it.exception?.message!!)
                    }
                }
            }
            progressbar_update_email.visibility= View.GONE
        }
        button_update.setOnClickListener{view ->
            val email =  edit_text_email.text.toString().trim()

            if (email.isEmpty()) {
                text_email.error = "Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                text_email.error = "Valid Email Required"
                text_email.requestFocus()
                return@setOnClickListener
            }

            progressbar_update_email.visibility= View.VISIBLE

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
            progressbar_update_email.visibility= View.GONE
        }
    }


}
