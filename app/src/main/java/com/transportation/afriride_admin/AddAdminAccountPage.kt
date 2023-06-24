package com.transportation.afriride_admin


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddAdminAccountPage : Fragment() {

    private lateinit var adminProfilePic: ImageView
    private lateinit var changeProfilePicButton: Button
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var createAdminButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_admin_account_page, container, false)

        adminProfilePic = view.findViewById(R.id.adminProfilePic)

        changeProfilePicButton = view.findViewById(R.id.changeProfilePicButton)
        changeProfilePicButton.setOnClickListener {
            selectImageFromGallery()
        }

        usernameEditText = view.findViewById(R.id.usernameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)

        createAdminButton = view.findViewById(R.id.createAdminButton)
        createAdminButton.setOnClickListener {
            createAdmin()
        }

        db = Firebase.firestore
        val storage = Firebase.storage
        storageRef = storage.reference
        return view
    }

    private fun createAdmin() {
        showLoadingPopup()

        // Get reference to admin collection
        val adminRef = db.collection("admin").document()
        // Get admin data from views
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val adminProfileData = hashMapOf(
            "username" to username,
            "email" to email
        )

        // TODO: Create Admin Account should also sign up user using email and password

        // create new document and store admin data in document
        adminRef.set(adminProfileData).addOnSuccessListener {
            // get document id and bitmap from adminProfilePic ImageView
            val bitmap = adminProfilePic.drawable.toBitmap()
            // call uploadImageToFirebase() method
            val adminId = adminRef.id
            uploadImageToFirebase(bitmap, adminId)
        }
        .addOnFailureListener { e ->
            Log.w(tag, "Error creating admin", e)
            Toast.makeText(context, "Error Creating Admin!", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImage = data?.data
            // Process the selected image
            selectedImage?.let {
                val bitmap: Bitmap? = loadBitmapFromUri(it)
                bitmap?.let {
                    // TODO: Profile pics can be unnecessarily large; must find a way to compress it

                    adminProfilePic.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap, adminId: String) {
        val imagesRef: StorageReference = storageRef.child("adminProfilePictures/$adminId.jpg")

        // Convert the Bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // Upload the byte array to Firebase Storage
        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image upload successful
                val downloadUrl = imagesRef.downloadUrl
                // You can save the download URL to the database or perform any other operations with it
                storeAdminProfilePicDownloadUrl(adminId, downloadUrl)
            } else {
                // Image upload failed
                val exception = task.exception
                Log.w(tag, "Image Upload Failed", exception)
                Toast.makeText(context, "Image Upload Failed!", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }

    private fun storeAdminProfilePicDownloadUrl(adminId: String, downloadUrlTask: Task<Uri>) {
        downloadUrlTask.addOnSuccessListener { uri ->
            // get download url
            val downloadUrl = uri.toString()
            // upload download url to admin profile
            val adminRef = db.collection("admin").document(adminId)
            adminRef.update("profilePicUrl", downloadUrl)
            dialog.dismiss()
            // close add admin page
            findNavController().navigateUp()
        }.addOnFailureListener { e ->
            Log.w(tag, "Couldn't get download url!", e)
            dialog.dismiss()
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun showLoadingPopup() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog.show()
    }
}