package com.transportation.afriride_admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginPage : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var navController: NavController
    private lateinit var dialog: AlertDialog
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login_page, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        loginButton.setOnClickListener { initiateLogin() }
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView)
        forgotPasswordTextView.setOnClickListener { openForgotPasswordPage() }
        sharedPreferences = requireContext().getSharedPreferences(AFRICA_RIDE_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        rememberMeCheckBox = view.findViewById(R.id.rememberMeCheckBox)
        navController = findNavController()

        auth = Firebase.auth
        db = Firebase.firestore

        getStoredLoginDetails()
        return view
    }

    private fun initiateLogin(){
        hideKeyboard()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        when {
            email.isEmpty() -> {
                Toast.makeText(context, "Please enter a valid Admin Id", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
            }
            password.length < 8 -> {
                Toast.makeText(context, "Password must contain at least 8 characters", Toast.LENGTH_SHORT).show()
            }
            !containsAlphabetAndDigit(password) -> {
                Toast.makeText(context, "Password must contain at least one alphabet and one digit", Toast.LENGTH_SHORT).show()
            }
            else -> {
                loginUser(email, password)
            }
        }
    }

    private fun containsAlphabetAndDigit(input: String): Boolean {
        val regex = "^(?=.*[A-Za-z])(?=.*\\d).+\$".toRegex()
        return regex.matches(input)
    }

    private fun loginUser(email: String, password: String) {
        showLoadingPopup()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                checkAdminAuthenticity(email, password)
            }
        }.addOnFailureListener { exception ->
            dialog.dismiss()
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAdminAuthenticity(email: String, password: String){
        // access database
        val adminCollectionRef = db.collection("admin")
        // get UID
        val uid = auth.uid
        // check if database admin collection contains document with id: UID
        if (uid != null) {
            val adminRef = adminCollectionRef.document(uid)
            adminRef.get().addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val documentSnapshot = task.result
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // save login details and open home page if so
                        saveLoginDetails(email, password)
                        dialog.dismiss()
                        openHomePage()
                    } else {
                        // display "Account is Unauthorized!" message if not
                        Toast.makeText(context, "Account is Unauthorized!", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(context, "An error occurred while authenticating user!", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
        }
    }

    private fun saveLoginDetails(email: String, password: String) {
        val editor = sharedPreferences.edit()

        // Store the Keep Me Logged In state
        val keepMeLoggedIn = rememberMeCheckBox.isChecked
        editor.putBoolean(REMEMBER_ME_BOOLEAN_VALUE_SHPR, keepMeLoggedIn)
        editor.apply()

        // check whether to save driver key depending on remember me checkbox
        if (rememberMeCheckBox.isChecked){
            sharedPreferences.edit().putString(STORED_ADMIN_EMAIL, email).apply()
            sharedPreferences.edit().putString(STORED_ADMIN_PASSWORD, password).apply()
        }else {
            sharedPreferences.edit().remove(STORED_ADMIN_EMAIL).apply()
            sharedPreferences.edit().remove(STORED_ADMIN_PASSWORD).apply()
        }
    }

    private fun getStoredLoginDetails() {
        val rememberMe = sharedPreferences.getBoolean(REMEMBER_ME_BOOLEAN_VALUE_SHPR, false)
        if (rememberMe){
            rememberMeCheckBox.isChecked = true

            val email = sharedPreferences.getString(STORED_ADMIN_EMAIL, "")
            val password = sharedPreferences.getString(STORED_ADMIN_PASSWORD, "")

            emailEditText.setText(email)
            passwordEditText.setText(password)
        }
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

    private fun openHomePage() {
        // Navigate to the Main Page
        navController.navigate(R.id.action_loginPage_to_homePage)
    }

    private fun openForgotPasswordPage(){
        navController.navigate(R.id.action_loginPage_to_forgotPasswordPage)
    }
}