package com.transportation.afriride_admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class AdminAccountsRecyclerAdapter(
    private val context: Context,
    private val adminAccountsList: List<AdminAccount>
): RecyclerView.Adapter<AdminAccountsRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val adminAccountCard: ConstraintLayout = itemView.findViewById(R.id.adminAccountCard)
        val adminProfilePic: ImageView = itemView.findViewById(R.id.adminProfilePic)
        val adminUsername: TextView = itemView.findViewById(R.id.adminUsername)
        val adminEmailAddress: TextView = itemView.findViewById(R.id.adminEmailAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.admin_accounts_recycler_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val adminAccount = adminAccountsList[position]

        holder.adminAccountCard.setOnClickListener { openAdminAccountPage() }
        Picasso.get().load(adminAccount.profilePicUrl).into(holder.adminProfilePic)
        holder.adminUsername.text = adminAccount.username
        holder.adminEmailAddress.text = adminAccount.email
    }

    private fun openAdminAccountPage() {
        //TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return adminAccountsList.size
    }
}