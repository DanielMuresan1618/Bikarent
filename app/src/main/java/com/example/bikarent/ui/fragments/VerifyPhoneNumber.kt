package com.example.bikarent.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.bikarent.R
import com.example.bikarent.utils.toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_verify_phone_number.*
import java.util.concurrent.TimeUnit

class VerifyPhoneNumber : Fragment() {

    private  var verificationId:String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_phone_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchLayouts(true)

        button_send_verification.setOnClickListener {view ->
            if (!sendVerification(view)){
                return@setOnClickListener
            }
        }

        button_verify.setOnClickListener {view ->
           if (!verifyPhone(view)){
               return@setOnClickListener
           }
        }
    }

    private fun sendVerification(view:View):Boolean{
        val phone = edit_text_phone.text.toString().trim()
        if (phone.isEmpty() || phone.length != 10) {
            edit_text_phone.error = "Enter a correct phone number"
            edit_text_phone.requestFocus()
            return false
        }
        val phoneNumber = '+' + ccp.selectedCountryCode + phone
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber
            , 60
            , TimeUnit.SECONDS
            , requireActivity()
            , phoneAuthCallback
        )

        switchLayouts(false)
        return true
    }

    private fun verifyPhone(view: View):Boolean{
        val code = edit_text_code.text.toString().trim()
        if (code.isEmpty()) {
            edit_text_code.error = "Code required"
            edit_text_code.requestFocus()
            return false
        }
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(it, code)
            addPhoneNumber(credential)
        }
        return true
    }

    private fun addPhoneNumber(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(phoneAuthCredential)?.addOnCompleteListener{
            if (it.isSuccessful){
                context?.toast("Phone Added")
                val action = VerifyPhoneNumberDirections.actionPhoneVerified()
                Navigation.findNavController(button_verify).navigate(action)
            }
            else{
                context?.toast(it.exception?.message!!)
            }
        }
    }

    private val phoneAuthCallback  =  object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            p0.let { phoneAuthCredential ->
                addPhoneNumber(phoneAuthCredential)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            context?.toast(p0.message!!)
        }
    }

    private fun switchLayouts(phoneLayoutBoolean:Boolean){
        if (phoneLayoutBoolean){
            layoutPhone.visibility = View.VISIBLE
            layoutVerification.visibility = View.GONE
        }else{
            layoutPhone.visibility = View.GONE
            layoutVerification.visibility = View.VISIBLE
        }
    }
}
