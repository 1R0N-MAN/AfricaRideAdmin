package com.transportation.afriride_admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DriverAccountsPage : Fragment() {

    private lateinit var driverAccountsSearchBar: SearchView
    private lateinit var driverAccountsRecyclerView: RecyclerView
    private lateinit var addDriverAccountButton: ExtendedFloatingActionButton
    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_driver_accounts_page, container, false)

        driverAccountsSearchBar = view.findViewById(R.id.driverAccountsSearchBar)
        driverAccountsRecyclerView = view.findViewById(R.id.driverAccountsRecyclerView)
        addDriverAccountButton = view.findViewById(R.id.addDriverAccountButton)

        db = Firebase.firestore

        getDriverAccounts()
        return view
    }

    private fun getDriverAccounts() {
        showLoadingPopup()
        // access database
        val driverAccountsRef = db.collection("driver-details-admin")
        // get all driver accounts
        driverAccountsRef.get()
            .addOnSuccessListener { documents ->
                // put driver accounts in list
                val driverAccountsList = mutableListOf<DriverAccount>()
                for (document in documents){
                    val driverAccount = loadDriverAccount(document)
                    driverAccountsList.add(driverAccount)
                }
                // put list into adapter
                val adapter = DriverAccountsRecyclerAdapter(requireContext(), driverAccountsList)
                // set driverAccountsRecyclerView Adapter
                driverAccountsRecyclerView.adapter = adapter

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

    private fun loadDriverAccount(document: QueryDocumentSnapshot): DriverAccount {
        val driverAccountData = document.data

        val profilePicUrl = driverAccountData["profilePicUrl"].toString()
        val licensePlate = driverAccountData["licensePlate"].toString()
        val driverName = driverAccountData["driverName"].toString()

        return DriverAccount(profilePicUrl, licensePlate, driverName)
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