package com.transportation.afriride_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DriverAccountsPage : Fragment() {

    private lateinit var driverAccountsSearchBar: SearchView
    private lateinit var driverAccountsRecyclerView: RecyclerView
    private lateinit var addDriverAccountButton: FloatingActionButton
    private lateinit var db: FirebaseFirestore

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
        return view
    }
}