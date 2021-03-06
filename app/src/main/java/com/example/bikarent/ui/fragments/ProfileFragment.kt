package com.example.bikarent.ui.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.bikarent.R
import com.example.bikarent.utils.Constants.DEFAULT_IMAGE_URL
import com.example.bikarent.utils.Constants.REQUEST_IMAGE_CAPTURE
import com.example.bikarent.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var  imageUri: Uri
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_view.setOnClickListener{
            takePictureIntent()
        }

        displayUserData()

        button_save.setOnClickListener {
            val name = edit_text_name.text.toString().trim()
            if(!emptyNameVerification(name)){
                return@setOnClickListener
            }
            updateProfile(currentUser, name)
        }

        text_not_verified.setOnClickListener {
            sendEmailVerification()
        }

        text_phone.setOnClickListener{
            val action = ProfileFragmentDirections.actionVerifyPhoneNumber()
            Navigation.findNavController(it).navigate(action)
        }

        profile_text_email.setOnClickListener{
            val action = ProfileFragmentDirections.actionUpdateEmail()
            Navigation.findNavController(it).navigate(action)
        }

        text_password.setOnClickListener{view->
            val action = ProfileFragmentDirections.actionUpdatePassowrd()
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadAndSaveImage(imageBitmap)
        }
    }

    private fun sendEmailVerification() {
        currentUser?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                context?.toast("Verification Email Sent")
            } else {
                context?.toast(it.exception?.message!!)
            }
        }
    }

    private fun updateProfile(currentUser: FirebaseUser?, name: String) {
        val photo = when {
            ::imageUri.isInitialized -> imageUri
            currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
            else -> currentUser.photoUrl
        }


        val updates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(photo)
            .build()

        progressbar.visibility = View.VISIBLE

        currentUser?.updateProfile(updates)
            ?.addOnCompleteListener { task ->
                progressbar.visibility = View.INVISIBLE
                if (task.isSuccessful) {
                    context?.toast("Profile Updated")
                } else {
                    context?.toast(task.exception?.message!!)
                }
            }
    }

    private fun emptyNameVerification(name: String): Boolean {
        if (name.isEmpty()) {
            edit_text_name.error = "name required"
            edit_text_name.requestFocus()
            return false
        }
        return true
    }

    private fun displayUserData() {
        currentUser?.let { user ->
            Glide.with(this).load(user.photoUrl).into(image_view)
            edit_text_name.setText(user.displayName)
            profile_text_email.text = user.email
            text_phone.text =
                if (user.phoneNumber.isNullOrEmpty()) "Add number" else user.phoneNumber
            if (user.isEmailVerified)
                text_not_verified.visibility = View.INVISIBLE
            else
                text_not_verified.visibility = View.VISIBLE
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            .also { pictureIntent -> pictureIntent.resolveActivity(activity?.packageManager!!)
                .also{startActivityForResult(pictureIntent,
                    REQUEST_IMAGE_CAPTURE
                )}}

    }

    private fun uploadAndSaveImage(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
        val image = baos.toByteArray()
        val upload = storageRef.putBytes(image)
        progressbar_pic.visibility = View.VISIBLE

        upload.addOnCompleteListener{
            progressbar_pic.visibility = View.INVISIBLE
            if (it.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{
                    it.result?.let{
                        imageUri = it
                        image_view.setImageBitmap(imageBitmap)
                    }
                }
            }
            else {
                it.exception?.let{
                    activity?.toast(it.message!!)
                }
            }
        }
    }
}
