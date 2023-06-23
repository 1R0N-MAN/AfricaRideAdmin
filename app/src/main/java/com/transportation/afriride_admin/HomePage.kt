package com.transportation.afriride_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomePage : Fragment() {

    private lateinit var toolbarTitle: TextView
    private lateinit var adminAccountsCard: CardView
    private lateinit var driverAccountsCard: CardView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        toolbarTitle = view.findViewById(R.id.toolbarTitle)

        adminAccountsCard = view.findViewById(R.id.adminAccountsCard)
        adminAccountsCard.setOnClickListener { openAdminAccountsPage() }

        driverAccountsCard = view.findViewById(R.id.driverAccountsCard)
        driverAccountsCard.setOnClickListener { openDriverAccountsPage() }

        auth = Firebase.auth
        db = Firebase.firestore

        setWelcomeText()
        return view
    }

    private fun openDriverAccountsPage() {
        findNavController().navigate(R.id.action_homePage_to_driverAccountsPage)
    }

    private fun openAdminAccountsPage() {
        findNavController().navigate(R.id.action_homePage_to_adminAccountsPage)
    }

    private fun setWelcomeText(){
        val adminRef = db.collection("admin").document("${auth.uid}")
        adminRef.get().addOnSuccessListener { result ->
            val username = result["username"]
            val welcomeText = getString(R.string.welcome_username, "$username")
            toolbarTitle.text = welcomeText
        }
    }
}