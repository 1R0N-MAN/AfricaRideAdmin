package com.transportation.afriride_admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminAccountsPage : Fragment() {

    private lateinit var adminAccountsRecyclerView: RecyclerView
    private lateinit var addAdminAccountButton: Button
    private lateinit var dialog: AlertDialog
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_accounts_page, container, false)

        adminAccountsRecyclerView = view.findViewById(R.id.adminAccountsRecyclerView)

        addAdminAccountButton = view.findViewById(R.id.addAdminAccountButton)

        db = Firebase.firestore

        getAdminAccounts()
        return view
    }

    private fun getAdminAccounts() {
        showLoadingPopup()
        // access database
        val adminAccountsRef = db.collection("admin")
        // get all driver accounts
        adminAccountsRef.get()
            .addOnSuccessListener { documents ->
                // put driver accounts in list
                val adminAccountsList = mutableListOf<AdminAccount>()
                for (document in documents){
                    val driverAccount = loadAdminAccount(document)
                    adminAccountsList.add(driverAccount)
                }
                // put list into adapter
                val adapter = AdminAccountsRecyclerAdapter(requireContext(), adminAccountsList)
                // set driverAccountsRecyclerView Adapter
                adminAccountsRecyclerView.adapter = adapter

                dialog.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(
                    context,
                    "There was a problem finding drivers! Please try again!",
                    Toast.LENGTH_LONG).show()

                dialog.dismiss()
            }
    }

    private fun loadAdminAccount(document: QueryDocumentSnapshot): AdminAccount {
        val adminData = document.data

        val profilePicUrl = adminData["profilePicUrl"].toString()
        val email = adminData["email"].toString()
        val username = adminData["username"].toString()

        return AdminAccount(profilePicUrl, email, username)
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