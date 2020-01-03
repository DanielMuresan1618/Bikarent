package com.example.bikarent.ui.fragments


import android.os.Bundle
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

import kotlinx.android.synthetic.main.fragment_update_password.*
import kotlinx.android.synthetic.main.fragment_update_password.button_authenticate
import kotlinx.android.synthetic.main.fragment_update_password.edit_text_password
import kotlinx.android.synthetic.main.fragment_update_password.layoutPassword


class UpdatePasswordFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutUpdatePassword.visibility = View.GONE
        layoutPassword.visibility = View.VISIBLE

        button_authenticate.setOnClickListener{
            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()){
                edit_text_password.error = "Password required!"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility= View.VISIBLE

            currentUser?.let { user ->
                val credentials = EmailAuthProvider.getCredential(user.email!!,password)
                user.reauthenticate(credentials).addOnCompleteListener{
                    when {
                        it.isSuccessful -> {
                            layoutUpdatePassword.visibility = View.VISIBLE
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
            progressbar.visibility= View.GONE
        }

        button_update.setOnClickListener{
            val password = edit_text_new_password.text.toString().trim()
            if (password.isEmpty()){
                edit_text_new_password.error = "At least 6 characters required"
                edit_text_new_password.requestFocus()
                return@setOnClickListener
            }
            if (password != edit_text_new_password_confirm.text.toString().trim()){
                edit_text_new_password_confirm.error = "The passwords don't match!"
                edit_text_new_password_confirm.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currentUser?.let{user ->
                user.updatePassword(password).addOnCompleteListener{
                    if (it.isSuccessful){
                        val action = UpdatePasswordFragmentDirections.actionPasswordUpdated()
                        Navigation.findNavController(view).navigate(action)
                        context?.toast("Password Updated!")
                    }else{
                        context?.toast(it.exception?.message!!)
                    }
                }
            }
            progressbar.visibility = View.GONE
        }
    }


}
